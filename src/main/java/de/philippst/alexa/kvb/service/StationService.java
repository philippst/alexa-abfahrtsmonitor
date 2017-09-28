package de.philippst.alexa.kvb.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import de.philippst.alexa.kvb.exception.KvbException;
import de.philippst.alexa.kvb.model.KvbStation;
import de.philippst.alexa.kvb.utils.KvbStationDomExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
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
        Item item = table.getItem("StationTitle", stationString);
        Integer stationId = (item == null) ? null : item.getInt("StationId");
        logger.info("translate stationTitle: {} => {}", stationString, stationId);
        return stationId;
    }

    public KvbStation getStationDeparture(int stationId) throws KvbException, IOException {
        if(stationId == 0) throw new KvbException("Invalid kvb station id");

        KvbStation kvbStation = new KvbStation();

        String url = String.format("http://www.kvb-koeln.de/qr/%d/", stationId);

        Document dom = Jsoup.parse(new URL(url).openStream(),"ISO-8859-1",url);

        kvbStation.setTitle(KvbStationDomExtractor.getStationTitle(dom));
        kvbStation.setDisruptionMessage(KvbStationDomExtractor.getDisruptionMessage(dom));
        kvbStation.setDepartures(KvbStationDomExtractor.getDepartures(dom));

        return kvbStation;
    }

    public List<String> getGlobalDisruptionMessages(boolean bus) throws IOException {
        String url = "http://www.kvb-koeln.de/german/home/mofis_bahn.html";
        if(bus) url = "http://www.kvb-koeln.de/german/home/mofis_bus.html";

        Document dom = Jsoup.parse(new URL(url).openStream(),"ISO-8859-1",url);
        return KvbStationDomExtractor.getGlobalDisruptionMessage(dom);
    }

    public HashMap<String,Integer> getStations() throws KvbException, IOException {

        String url = "http://www.kvb-koeln.de/german/mofis/mofis.html";
        Document dom = Jsoup.parse(new URL(url).openStream(),"ISO-8859-1",url);

        Elements elements = dom.select("#content > div.fliesstext.mobile100pc > div > div > table > tbody > tr a");

        HashMap<String,Integer> stationMap = new HashMap<>();
        for(org.jsoup.nodes.Element element : elements){
            String stationTitle = element.text();
            String stationUrl = element.attr("href").substring(4);
            stationUrl = stationUrl.substring(0,stationUrl.length()-1);
            stationMap.put(stationTitle,Integer.valueOf(stationUrl));
        }

        return stationMap;
    }
}
