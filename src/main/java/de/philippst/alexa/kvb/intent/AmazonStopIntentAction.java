package de.philippst.alexa.kvb.intent;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;

public class AmazonStopIntentAction implements IntentAction{

    protected AmazonStopIntentAction() { }

    @Override
    public SpeechletResponse perform(Intent intent, Session session) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        return SpeechletResponse.newTellResponse(speech);
    }
}
