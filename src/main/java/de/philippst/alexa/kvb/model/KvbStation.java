package de.philippst.alexa.kvb.model;

import java.util.List;

public class KvbStation {
    private String title;
    private List<String> disruptionMessage;
    private List<KvbStationDeparture> departures;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getDisruptionMessage() {
        return disruptionMessage;
    }

    public void setDisruptionMessage(List<String> disruptionMessage) {
        this.disruptionMessage = disruptionMessage;
    }

    public List<KvbStationDeparture> getDepartures() {
        return departures;
    }

    public void setDepartures(List<KvbStationDeparture> departures) {
        this.departures = departures;
    }

}
