package de.philippst.alexa.kvb.utils;

import java.util.HashMap;
import java.util.Map;

public class TextToSpeechHelper {

    public static String disruptionSSML(String disruptionMessage){
        disruptionMessage = disruptionMessage.replace(" der (H) ", " der Haltestelle ")
            .replace(" Die (H) ", " Die Haltestelle ")
            .replace(" und (H) ", " und Haltestelle ")
            .replace("(H)", "")
            .replace(" Str. ", " Straße ")
            .replace("pl. ", "platz ")
            .replace("Bad Godesb.", "Bad Godesberg")
            .replace("Wir werden die verspäteten Bahnen schnellstmöglich wieder nach Fahrplan für Sie einsetzen *", "")
            .replace(" *", "<break strength=\"medium\" />")
            .replace(" - ", "<break strength=\"medium\" />");

        disruptionMessage = disruptionMessage.replaceAll("(\\d{1,2}:\\d{1,2})[h]",
                "<say-as interpret-as=\"time\">$1</say-as>");

        return disruptionMessage;
    }

    public static String stationSSML(String stationTitle){

        stationTitle = stationTitle.toLowerCase();

        Map<String, String> xSampemMap = new HashMap<>();
        xSampemMap.put("subbelrather", "suplrat6^");
        xSampemMap.put("vischering", "fISE6_^iNG");
        xSampemMap.put("kalscheuren","ka:lSOYrEn");

        for (Map.Entry<String, String> entry : xSampemMap.entrySet()) {
            stationTitle = stationTitle.replaceAll(entry.getKey(),
                    String.format("<phoneme alphabet='x-sampa' ph='%s'>%s</phoneme>",
                            entry.getValue(),
                            entry.getKey()
                    )
            );
        }

        stationTitle = stationTitle
                .replace("str.","straße")
                .replace(" bf"," bahnhof");

        return stationTitle;
    }

}
