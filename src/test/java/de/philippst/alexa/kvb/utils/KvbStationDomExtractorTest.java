package de.philippst.alexa.kvb.utils;

import de.philippst.alexa.kvb.exception.KvbException;
import de.philippst.alexa.kvb.model.KvbStationDeparture;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class KvbStationDomExtractorTest {
    private File getResourceFile(String name) {
        String fileName = getClass().getResource("/kvb-dom/"+name).getFile().replace("%20"," ");
        return new File(fileName);
    }

    @Test
    public void getStationTitle() throws Exception {
        Document dom = Jsoup.parse(getResourceFile("station-departures.html"),"utf-8");
        String stationTitel = KvbStationDomExtractor.getStationTitle(dom);
        assertEquals("Leyendeckerstr.",stationTitel);
    }

    @Test(expected=KvbException.class)
    public void getStationTitleEmpty() throws Exception {
        Document dom = Jsoup.parse(getResourceFile("invalid-station.html"),"utf-8");
        KvbStationDomExtractor.getStationTitle(dom);
    }

    @Test
    public void getDisruptionMessageEmpty() throws Exception {
        Document dom = Jsoup.parse(getResourceFile("station-departures.html"),"utf-8");
        List<String> stationDisruptionMessage = KvbStationDomExtractor.getDisruptionMessage(dom);
        assertEquals(0,stationDisruptionMessage.size());
    }

    @Test
    public void getDisruptionMessageSingle() throws Exception {
        Document dom = Jsoup.parse(getResourceFile("station-departures-disruption.html"),"utf-8");
        List<String> stationDisruptionMessage = KvbStationDomExtractor.getDisruptionMessage(dom);
        List<String> actual = Arrays.asList("Linie 18 * Folgende Fahrt entfällt * (H) Klettenbergpark 14:09h *");
        assertEquals(actual,stationDisruptionMessage);
    }

    @Test
    public void getDisruptionMessageSeveral() throws Exception {
        Document dom = Jsoup.parse(getResourceFile("station-departures-disruptions.html"),"utf-8");
        List<String> stationDisruptionMessage = KvbStationDomExtractor.getDisruptionMessage(dom);
        List<String> actual = Arrays.asList(
                "Linie 3 * Folgende Fahrt entfällt * (H) Thielenbruch 19:06h *",
                "Linie 18 * Folgende Fahrt entfällt * (H) Thielenbruch 15:08h *");
        assertEquals(actual,stationDisruptionMessage);
    }

    @Test
    public void getDepartures() throws Exception {
        Document dom = Jsoup.parse(getResourceFile("station-departures.html"),"utf-8");
        List<KvbStationDeparture> departures = KvbStationDomExtractor.getDepartures(dom);
        assertEquals(4,departures.size());
    }

    @Test
    public void getDeparturesNone() throws Exception {
        Document dom = Jsoup.parse(getResourceFile("invalid-station.html"),"utf-8");
        List<KvbStationDeparture> departures = KvbStationDomExtractor.getDepartures(dom);
        assertEquals(0,departures.size());
    }

    @Test
    public void getGlobalDisruptionsTrainNone() throws Exception {
        Document dom = Jsoup.parse(getResourceFile("disruptions-train-none.html"),"utf-8");
        List<String> disruptionMessages = KvbStationDomExtractor.getGlobalDisruptionMessage(dom);
        assertEquals(0,disruptionMessages.size());
    }

    @Test
    public void getGlobalDisruptionsBusOne() throws Exception {
        Document dom = Jsoup.parse(getResourceFile("disruptions-bus-one.html"),"utf-8");
        List<String> disruptionMessages = KvbStationDomExtractor.getGlobalDisruptionMessage(dom);
        assertEquals(1,disruptionMessages.size());
        List<String> actual = Arrays.asList("Linie 159 * Straßensperrung im Bereich Buchforststr. und Eythstr. *");
        assertEquals(actual,disruptionMessages);
    }

    @Test
    public void getGlobalDisruptionsBusSeveral() throws Exception {
        Document dom = Jsoup.parse(getResourceFile("disruptions-bus-several.html"),"utf-8");
        List<String> disruptionMessages = KvbStationDomExtractor.getGlobalDisruptionMessage(dom);
        assertEquals(2,disruptionMessages.size());
    }

    @Test
    public void getGlobalDisruptionsTrainOne() throws Exception {
        Document dom = Jsoup.parse(getResourceFile("disruptions-train-one.html"),"utf-8");
        List<String> disruptionMessages = KvbStationDomExtractor.getGlobalDisruptionMessage(dom);
        assertEquals(1,disruptionMessages.size());
        List<String> actual = Arrays.asList(
                "Linie 9 * Falschparker auf der Zülpicher Str. * Die Bahnen sind zurzeit an der Weiterfahrt gehindert *");
        assertEquals(actual,disruptionMessages);
    }

}