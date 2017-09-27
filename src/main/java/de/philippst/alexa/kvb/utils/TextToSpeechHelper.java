package de.philippst.alexa.kvb.utils;

public class TextToSpeechHelper {

    public static String disruptionSSML(String disruptionMessage){
        disruptionMessage = disruptionMessage.replace(" der (H) ", " der Haltestelle ")
            .replace(" Die (H) ", " Die Haltestelle ")
            .replace(" und (H) ", " und Haltestelle ")
            .replace("(H)", "")
            .replace(" Str. ", " Straße ")
            .replace("Bad Godesb.", "Bad Godesberg")
            .replace(" *", "<break strength=\"medium\" />")
            .replace(" - ", "<break strength=\"medium\" />");

        disruptionMessage = disruptionMessage.replaceAll("(\\d{1,2}:\\d{1,2})[h]",
                "<say-as interpret-as=\"time\">$1</say-as>");

        return disruptionMessage;
    }

}
