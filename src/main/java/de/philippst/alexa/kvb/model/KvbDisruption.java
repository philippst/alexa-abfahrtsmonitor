package de.philippst.alexa.kvb.model;

import java.util.List;

public class KvbDisruption {
    public List<String> line;
    public String message;

    public List<String> getLine() {
        return line;
    }

    public String getLineAsString(){
        return String.join(", ",line);
    }

    public void setLine(List<String> line) {
        this.line = line;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString(){
        return "Linie "+getLineAsString()+" * "+getMessage();
    }

}
