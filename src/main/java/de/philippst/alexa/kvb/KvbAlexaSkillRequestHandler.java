package de.philippst.alexa.kvb;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public final class KvbAlexaSkillRequestHandler extends SpeechletRequestStreamHandler {

    private static final Set<String> supportedApplicationIds;
    static {
        supportedApplicationIds = new HashSet<>();
        supportedApplicationIds.add("amzn1.ask.skill.aa98d101-c56e-42a1-95a1-39e3c4852174");
    }

    public KvbAlexaSkillRequestHandler() {
        super(
                DaggerAppComponent.create().alexaSpeechlet(),
                supportedApplicationIds
        );
    }
}
