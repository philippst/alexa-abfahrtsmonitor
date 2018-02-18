package de.philippst.alexa.kvb.exception;

public class StationResolutionException extends Exception{

    public StationResolutionException() { super(); }
    public StationResolutionException(String message) { super(message); }
    public StationResolutionException(String message, Throwable cause) { super(message, cause); }
    public StationResolutionException(Throwable cause) { super(cause); }

}
