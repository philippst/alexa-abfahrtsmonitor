package de.philippst.alexa.kvb.intent;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;

public interface IntentAction {

    /**
     * Creates a {@code SpeechletResponse} for the intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    SpeechletResponse perform(final Intent intent, final Session session);

}
