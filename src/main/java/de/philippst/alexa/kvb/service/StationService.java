package de.philippst.alexa.kvb.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import de.philippst.alexa.kvb.exception.KvbException;
import de.philippst.alexa.kvb.model.KvbStation;
import de.philippst.alexa.kvb.model.KvbDisruption;
import de.philippst.alexa.kvb.utils.KvbStationDomExtractor;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class StationService {

    private final Logger logger = LoggerFactory.getLogger(StationService.class);

    private DynamoDB docClient;

    @Inject
    public StationService(AmazonDynamoDB amazonDynamoDB) {
        this.docClient = new DynamoDB(amazonDynamoDB);
    }

    public Integer stationProcessor(String stationString) {
        Table table = docClient.getTable("AlexaKvb_Station");
        Item item = table.getItem("StationTitle", stationString.toLowerCase());
        Integer stationId = (item == null) ? null : item.getInt("StationId");
        logger.info("translate stationTitle: {} => {}", stationString, stationId);
        return stationId;
    }

    public KvbStation getStationDeparture(int stationId) throws KvbException, IOException, URISyntaxException {
        if(stationId == 0) throw new KvbException("Invalid kvb station id");

        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kvb.koeln")
                .setPath("/qr/"+stationId).build();

        logger.info("Request KVB Station: {}",uri);

        Document dom = Jsoup.parse(uri.toURL(),1000*5);

        KvbStation kvbStation = new KvbStation();
        kvbStation.setTitle(KvbStationDomExtractor.getStationTitle(dom));
        kvbStation.setDisruptionMessage(KvbStationDomExtractor.getDisruptionMessage(dom));
        kvbStation.setDepartures(KvbStationDomExtractor.getDepartures(dom));
        return kvbStation;
    }

    public List<KvbDisruption> getGlobalDisruptionMessages(boolean bus) throws IOException {
        String url = "https://kvb.koeln/fahrtinfo/betriebslage/bahn/";
        if(bus) url = "https://kvb.koeln/fahrtinfo/betriebslage/bus/";

        logger.info("Request KVB Disruptions: {}",url);

        Document dom = Jsoup.parse(new URL(url).openStream(),"ISO-8859-1",url);
        return KvbStationDomExtractor.getGlobalDisruptionMessage(dom);
    }

    @SuppressWarnings("unused")
    public HashMap<String,Integer> getStations() throws KvbException, IOException {

        String url = "https://www.kvb.koeln/haltestellen/index.html";
        logger.info("Request KVB Stations: {}",url);

        Document dom = Jsoup.parse(new URL(url).openStream(),"ISO-8859-1",url);

        Elements elements = dom.select("#content > div.fliesstext.mobile100pc > div > div > table > tbody > tr a");

        HashMap<String,Integer> stationMap = new HashMap<>();
        for(Element element : elements){
            String stationTitle = element.text();
            String stationUrl = element.attr("href").substring(4);
            stationUrl = stationUrl.substring(0,stationUrl.length()-1);
            stationMap.put(stationTitle,Integer.valueOf(stationUrl));
        }

        return stationMap;
    }
}
