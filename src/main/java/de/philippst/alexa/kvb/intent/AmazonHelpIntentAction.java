package de.philippst.alexa.kvb.intent;

import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;

import javax.inject.Inject;

public class AmazonHelpIntentAction implements IntentAction {

    @Inject
    protected AmazonHelpIntentAction() {
    }

    @Override
    public SpeechletResponse perform(IntentRequest intent, Session session, Context context) {
        String helpText = "" +
                "<p>Mit dem Wort \"Abfahrt\" erhältst du die nächsten Abfahrtszeiten an einer Haltestelle. " +
                "Ich merke mir die letzte Haltestelle, damit du diese nicht jedes Mal erneut sagen mussst. </p> " +
                "<p>Wenn du mich nach Störungen fragst, erfährst du die allgemeine Betriebslage der Bahnen und Busse " +
                "rund um Köln.</p> " +
                "<p>Sage Stopp oder Hilfe, wenn du nicht mehr weiter weist.</p> " +
                "<p>Also, wie kann ich dir helfen?</p>";

        SsmlOutputSpeech speech = new SsmlOutputSpeech();
        speech.setSsml("<speak>" + helpText + "</speak>");

        // Create reprompt
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Sage Abfahrt, Störungen, Hilfe oder Stopp.");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(speech, reprompt);
    }
}
