package de.philippst.alexa.kvb.utils;

import com.amazon.ask.model.ui.SimpleCard;
import de.philippst.alexa.kvb.model.KvbDisruption;
import de.philippst.alexa.kvb.model.KvbStation;
import de.philippst.alexa.kvb.model.KvbStationDeparture;

import java.util.List;
import java.util.stream.Collectors;

public class CardUiHelper {

    public static SimpleCard getStationCard(KvbStation station){

        StringBuilder stringBuilder = new StringBuilder();
        List<KvbStationDeparture> departures = station.getDepartures();

        if (departures.size() == 0) {
            stringBuilder.append("Aktuell kein Fahrbetrieb an dieser Haltestelle.");
        } else {
            departures.stream().limit(10).forEach( departure ->
                    stringBuilder.append(
                            String.format(
                                    "Linie %s %s in %s Min. \n",
                                    departure.getLine(),
                                    departure.getDestination(),
                                    departure.getMinutes()
                            )
                    )
            );
        }

        stringBuilder.append(station.getDisruptionMessage().stream().collect(Collectors.joining(" \n")));

        return SimpleCard.builder()
                .withTitle(station.getTitle())
                .withContent(stringBuilder.toString())
                .build();
    }


    public static SimpleCard getDisruptionCard(List<KvbDisruption> disruptions){

        return SimpleCard.builder()
                .withTitle("Störungen")
                .withContent(disruptions.stream().map(KvbDisruption::toString).collect(Collectors.joining(" \n")))
                .build();
    }


}
