package de.philippst.alexa.kvb.model;

import java.time.Duration;

public class KvbStationDeparture {
    private String line;
    private String destination;
    private Integer minutes;
    private Duration duration;

    public KvbStationDeparture(String line, String destination, Integer minutes){
        this.line = line;
        this.destination = destination;
        this.minutes = minutes;
        this.duration = Duration.ofMinutes(this.minutes);
    }

    public KvbStationDeparture(String line, String destination, String minutes){

        this.line = line.replace("\u00a0", "");
        this.destination = destination.replace("\u00a0", "");
        String myMinutes = minutes.replace("\u00a0", "").replace("Min","");
        if(myMinutes.equals("Sofort")){
            this.minutes = 0;
        } else {
            this.minutes = Integer.valueOf(myMinutes.trim());
        }
        this.duration = Duration.ofMinutes(this.minutes);
    }

    public String longString(){
        return "Linie "+this.line+" nach "+this.destination+" in "+this.minutes+" min";
    }

    public String toString(){
        return this.minutes+"min";
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
