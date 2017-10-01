package de.philippst.alexa.kvb.intent;

import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;

public class AmazonStopIntentAction implements IntentAction{

    protected AmazonStopIntentAction() { }

    @Override
    public SpeechletResponse perform(IntentRequest intent, Session session, Context context) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        return SpeechletResponse.newTellResponse(speech);
    }
}
