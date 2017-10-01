package de.philippst.alexa.kvb;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import de.philippst.alexa.kvb.intent.IntentHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class KvbAlexaSkillSpeechlet implements SpeechletV2 {

    private final Logger logger = LoggerFactory.getLogger(KvbAlexaSkillSpeechlet.class);

    private final IntentHandlerService intentHandlerService;

    @Inject
    public KvbAlexaSkillSpeechlet(IntentHandlerService intentHandlerService) {
        this.intentHandlerService = intentHandlerService;
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
        return intentHandlerService.launch(requestEnvelope);
    }

    @Override
    public SpeechletResponse onIntent(final SpeechletRequestEnvelope<IntentRequest> requestEnvelope){
        IntentRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();
        logger.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

        return intentHandlerService.handle(requestEnvelope);
    }

    @Override
    public void onSessionEnded(final SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        SessionEndedRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();

        logger.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
    }

}
