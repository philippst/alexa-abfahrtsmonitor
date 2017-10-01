package de.philippst.alexa.kvb.intent;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

public class IntentHandlerService {
    private final Logger logger = LoggerFactory.getLogger(IntentHandlerService.class);

    private final Map<String, IntentAction> intentActions;

    @Inject
    protected IntentHandlerService(final Map<String, IntentAction> intentActions) {
        this.intentActions = intentActions;
    }

    public SpeechletResponse handle(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {

        Intent intent = requestEnvelope.getRequest().getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        if(intentActions.containsKey(intentName)) {
            logger.info("intent: {}",intentName);
            return intentActions.get(intentName)
                    .perform(requestEnvelope.getRequest(), requestEnvelope.getSession(),requestEnvelope.getContext());
        } else {
            logger.warn("invalid intent: {}", intentName);
            return intentActions.get("AMAZON.HelpIntent")
                    .perform(requestEnvelope.getRequest(), requestEnvelope.getSession(),requestEnvelope.getContext());
        }
    }

    public SpeechletResponse launch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        return intentActions.get("WelcomeIntent").perform(null,requestEnvelope.getSession(),requestEnvelope.getContext());
    }

}
