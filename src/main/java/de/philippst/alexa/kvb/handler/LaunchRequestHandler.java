package de.philippst.alexa.kvb.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.requestType;

public class LaunchRequestHandler  implements RequestHandler {

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(requestType(LaunchRequest.class));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {

        String speechText = "" +
                "<p>Willkommen im Abfahrtsmonitor! Sage 'Abfahrt' mit einer Haltestelle oder " +
                "'Störungen Bus' oder 'Störungen Bahn' um Informationen zur Betriebslage zu erhalten.</p>";

        String repromptText = "Sage Abfahrt, Störungen, Hilfe oder Stop.";

        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withReprompt(repromptText)
                .build();
    }

}