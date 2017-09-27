package de.philippst.alexa.kvb;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;

import java.util.Set;

public final class KvbAlexaSkillRequestHandler extends SpeechletRequestStreamHandler {

    private static final Set<String> supportedApplicationIds = ImmutableSet.of(
            "amzn1.ask.skill.aa98d101-c56e-42a1-95a1-39e3c4852174");

    public KvbAlexaSkillRequestHandler() {
        super(Guice.createInjector(new KvbAlexaSkillApplicationModule())
                .getInstance(KvbAlexaSkillSpeechlet.class), supportedApplicationIds);
    }
}
