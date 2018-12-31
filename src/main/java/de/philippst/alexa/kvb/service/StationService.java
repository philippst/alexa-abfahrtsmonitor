package de.philippst.alexa.kvb.service;

import de.philippst.alexa.kvb.exception.KvbException;
import de.philippst.alexa.kvb.model.KvbDisruption;
import de.philippst.alexa.kvb.model.KvbStation;
import de.philippst.alexa.kvb.utils.KvbStationDomExtractor;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class StationService {

    private final Logger logger = LoggerFactory.getLogger(StationService.class);

    private static final int KVB_REQUEST_TIMEOUT = 1000*3;

    public KvbStation getStationDeparture(int stationId) throws KvbException, URISyntaxException, IOException {
        if(stationId == 0) throw new KvbException("Invalid kvb station id");

        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kvb.koeln")
                .setPath("/qr/"+stationId).build();

        logger.info("Request KVB Station: {}",uri);

        Document dom = Jsoup.parse(uri.toURL(),KVB_REQUEST_TIMEOUT);

        KvbStation kvbStation = new KvbStation();
        kvbStation.setTitle(KvbStationDomExtractor.getStationTitle(dom));
        kvbStation.setDisruptionMessage(KvbStationDomExtractor.getDisruptionMessage(dom));
        kvbStation.setDepartures(KvbStationDomExtractor.getDepartures(dom));
        return kvbStation;
    }

    public List<KvbDisruption> getGlobalDisruptionMessages(boolean bus) throws IOException, URISyntaxException {

        String path = (bus) ? "/fahrtinfo/betriebslage/bus/" : "/fahrtinfo/betriebslage/bahn/";

        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("kvb.koeln")
                .setPath(path).build();

        logger.info("Request KVB Disruptions: {}",uri.toURL());

        Document dom = Jsoup.parse(uri.toURL(),KVB_REQUEST_TIMEOUT);
        return KvbStationDomExtractor.getGlobalDisruptionMessage(dom);

    }
}
