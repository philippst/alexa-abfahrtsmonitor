package de.philippst.alexa.kvb.intent;

import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;

public interface IntentAction {

    /**
     * Creates a {@code SpeechletResponse} for the intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    SpeechletResponse perform(final IntentRequest intent, final Session session, final Context context);

}
