package de.philippst.alexa.kvb.intent;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;

public class WelcomeIntentAction implements IntentAction {
    @Override
    public SpeechletResponse perform(Intent intent, Session session) {
        String helpText = "" +
                "<p>Willkommen im Abfahrtsmonitor! Sage 'Abfahrt' mit einer Haltestelle oder sage " +
                "'Störungen' wenn du Informationen zur Betriebslage erhalten möchtest.</p>";

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
