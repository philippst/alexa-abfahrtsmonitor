package de.philippst.alexa.kvb;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import de.philippst.alexa.kvb.intent.IntentHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class KvbAlexaSkillSpeechlet implements Speechlet {

    private static final Logger logger = LoggerFactory.getLogger(KvbAlexaSkillSpeechlet.class);

    private final IntentHandlerService intentHandlerService;

    @Inject
    public KvbAlexaSkillSpeechlet(IntentHandlerService intentHandlerService) {
        this.intentHandlerService = intentHandlerService;
    }

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        logger.info("onSessionStarted requestId=" + request.getRequestId() +
                ", sessionId=" + session.getSessionId());
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        logger.info("request launch event");
        return intentHandlerService.handle("WelcomeIntent", null, session);
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
        logger.info("onIntent requestId=" + request.getRequestId() + ", sessionId=" + session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        return intentHandlerService.handle(intentName, intent, session);
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
        logger.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
    }
}
