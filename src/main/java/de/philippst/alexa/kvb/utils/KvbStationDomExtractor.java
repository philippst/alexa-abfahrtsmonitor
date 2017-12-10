package de.philippst.alexa.kvb.utils;

import de.philippst.alexa.kvb.exception.KvbException;
import de.philippst.alexa.kvb.model.KvbDisruption;
import de.philippst.alexa.kvb.model.KvbStationDeparture;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class KvbStationDomExtractor {

    /**
     * Suche Namen der Haltestelle in DOM
     */
    public static String getStationTitle(Document dom) throws KvbException {
        String text = dom.select("body > div:nth-child(6) > div > h1 > span.red-text").text();
        if(text.trim().isEmpty()) throw new KvbException("KVB station id invalid");
        return text;
    }

    /**
     * Suche Text zu Betriebsstörungen in DOM
     */
    public static List<String> getDisruptionMessage(Document dom){
        Elements disruptionRows = dom.select("body > div:nth-child(6) > div > table").first().select("table > tbody" +
                " > tr");

        List<String> disruptionStrings = new ArrayList<>();
        for(Element disurptionRow : disruptionRows){
            String disruptionString = disurptionRow.text().replace("\u00a0", "").trim();
            if(disruptionString.equals("Derzeit liegen an dieser Haltestelle keine Störungen vor.")) continue;
            disruptionStrings.add(disruptionString);
        }
        return disruptionStrings;
    }

    /**
     * Suche Text zu globalen Betriebsstörungen
     */
    public static List<KvbDisruption> getGlobalDisruptionMessage(Document dom){
        Elements rows = dom.select("body > div.container.section > div > div > div > div > table > tbody > tr");
        List<KvbDisruption> disruptions = new ArrayList<>();
        for(Element disruptionRow : rows){
            disruptionRow = disruptionRow.child(0);

            KvbDisruption kvbDisruption = new KvbDisruption();
            kvbDisruption.setMessage(disruptionRow.ownText());
            Elements lines = disruptionRow.select("td > ul > li");
            List<String> lineStrings = new ArrayList<>();
            for(Element line : lines){
                lineStrings.add(line.text().trim());
            }
            kvbDisruption.setLine(lineStrings);
            disruptions.add(kvbDisruption);
        }
        return disruptions;
    }

    /**
     * Suche Abfahrtsdaten zu Haltestelle in DOM
     */
    public static List<KvbStationDeparture> getDepartures(Document dom){
        Elements elements = dom.select("#qr_ergebnis > tbody > tr");
        List<KvbStationDeparture> departures = new ArrayList<>();
        for(Element row : elements){
            String lineNumber = row.select("td").get(0).text().trim();
            String lineDestination = row.select("td").get(1).text();
            String lineDeparture = row.select("td").get(2).text().trim();
            departures.add(new KvbStationDeparture(lineNumber,lineDestination,lineDeparture));
        }
        return departures;
    }

}
