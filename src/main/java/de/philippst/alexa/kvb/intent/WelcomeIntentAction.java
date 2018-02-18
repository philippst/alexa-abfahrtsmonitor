package de.philippst.alexa.kvb.intent;

import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;

import javax.inject.Inject;

public class WelcomeIntentAction implements IntentAction {

    @Inject
    public WelcomeIntentAction() {
    }

    @Override
    public SpeechletResponse perform(IntentRequest intent, Session session, Context context) {
        String helpText = "" +
                "<p>Willkommen im Abfahrtsmonitor! Sage 'Abfahrt' mit einer Haltestelle oder sage " +
                "'Störungen' wenn du Informationen zur Betriebslage erhalten möchtest.</p>";

        SsmlOutputSpeech speech = new SsmlOutputSpeech();
        speech.setSsml("<speak>" + helpText + "</speak>");

        // Create reprompt
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Sage Abfahrt, Störungen, Hilfe oder Stop.");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(speech, reprompt);
    }
}
