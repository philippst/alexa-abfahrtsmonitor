package de.philippst.alexa.kvb;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.*;
import de.philippst.alexa.kvb.intent.IntentAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

public class KvbAlexaSkillSpeechlet implements SpeechletV2 {

    private final Logger logger = LoggerFactory.getLogger(KvbAlexaSkillSpeechlet.class);

    private Map<String, IntentAction> intentActions;

    @Inject
    public KvbAlexaSkillSpeechlet(Map<String, IntentAction> intentActions) {
        this.intentActions = intentActions;
    }

    @Override
    public void onSessionStarted(final SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope){
        logger.info("onSessionStarted requestId={}, sessionId={}",
                requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
    }

    @Override
    public SpeechletResponse onLaunch(final SpeechletRequestEnvelope<LaunchRequest> requestEnvelope){
        LaunchRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();
        logger.info("onLaunch requestId={}, sessionId={}",request.getRequestId(),session.getSessionId());

        return this.intentActions.get("WelcomeIntent")
                .perform(null,requestEnvelope.getSession(),requestEnvelope.getContext());

    }

    @Override
    public SpeechletResponse onIntent(final SpeechletRequestEnvelope<IntentRequest> requestEnvelope){
        IntentRequest request = requestEnvelope.getRequest();
        logger.info("onIntent={}", request.getIntent().getName());

        String intentName = requestEnvelope.getRequest().getIntent().getName();

        switch(intentName){
            case "AMAZON.StartOverIntent":
                intentName = "AMAZON.HelpIntent"; break;
            case "AMAZON.CancelIntent":
            case "AMAZON.NoIntent":
                intentName = "AMAZON.StopIntent"; break;
            case "AMAZON.YesIntent":
                intentName = "HearMoreIntent"; break;
        }

        return this.intentActions.get(intentName)
                .perform(requestEnvelope.getRequest(), requestEnvelope.getSession(),requestEnvelope.getContext());

    }

    @Override
    public void onSessionEnded(final SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        SessionEndedRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();

        logger.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
    }

}
