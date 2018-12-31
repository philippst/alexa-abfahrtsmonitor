package de.philippst.alexa.kvb.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class HelpIntentHandler implements RequestHandler {

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.HelpIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {

        String speechText = "" +
                "<p>Mit dem Wort \"Abfahrt\" erhältst du die nächsten Abfahrtszeiten an einer Haltestelle. " +
                "Ich merke mir die letzte Haltestelle, damit du diese nicht jedes Mal erneut sagen mussst. </p> " +
                "<p>Wenn du mich nach Störungen fragst, erfährst du die allgemeine Betriebslage der Bahnen und Busse " +
                "rund um Köln.</p> " +
                "<p>Sage Stopp oder Hilfe, wenn du nicht mehr weiter weist.</p> " +
                "<p>Also, wie kann ich dir helfen?</p>";

        String repromptText = "Sage Abfahrt, Störungen, Hilfe oder Stopp.";

        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withReprompt(repromptText)
                .build();
    }
}