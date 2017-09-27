package de.philippst.alexa.kvb.exception;

public class KvbException extends Exception{

    public KvbException() { super(); }
    public KvbException(String message) { super(message); }
    public KvbException(String message, Throwable cause) { super(message, cause); }
    public KvbException(Throwable cause) { super(cause); }

}
