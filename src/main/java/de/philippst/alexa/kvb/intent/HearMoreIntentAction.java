package de.philippst.alexa.kvb.intent;

import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.SsmlOutputSpeech;
import de.philippst.alexa.kvb.utils.TextToSpeechHelper;

import javax.inject.Inject;
import java.util.List;

public class HearMoreIntentAction implements IntentAction {

    @Inject
    AmazonStopIntentAction amazonStopIntentAction;

    @Override
    public SpeechletResponse perform(IntentRequest intent, Session session, Context context) {
        if (session.getAttributes().containsKey("disruption")) {
            List<String> disruptionMessages = (List<String>) session.getAttribute("disruption");
            StringBuilder stringBuilder = new StringBuilder();

            for (String disruptionMessage : disruptionMessages) {
                stringBuilder.append(" " + TextToSpeechHelper.disruptionSSML(disruptionMessage));
            }
            String textString = stringBuilder.toString();
            SsmlOutputSpeech speech = new SsmlOutputSpeech();
            speech.setSsml("<speak>" + textString + "</speak>");
            return SpeechletResponse.newTellResponse(speech);
        }
        return amazonStopIntentAction.perform(intent,session,context);
    }
}
