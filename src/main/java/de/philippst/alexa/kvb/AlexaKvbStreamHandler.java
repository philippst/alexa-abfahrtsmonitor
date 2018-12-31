package de.philippst.alexa.kvb;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import de.philippst.alexa.kvb.handler.*;
import de.philippst.alexa.kvb.interceptor.LogRequestInterceptor;
import de.philippst.alexa.kvb.service.StationService;

@SuppressWarnings("unused")
public class AlexaKvbStreamHandler extends SkillStreamHandler {

    private static Skill getSkill(){

        StationService stationService = new StationService();

        return Skills.standard()
                .addRequestInterceptors(
                        new LogRequestInterceptor()
                )
                .addRequestHandlers(
                        new CancelandStopIntentHandler(),
                        new HelpIntentHandler(),
                        new LaunchRequestHandler(),
                        new DepartureIntentHandler(stationService),
                        new DisruptionIntentHandler(stationService),
                        new HearMoreIntentHandler(),
                        new FallbackIntentHandler())
                .withSkillId("amzn1.ask.skill.aa98d101-c56e-42a1-95a1-39e3c4852174")
                .withTableName("AlexaKvb_Attributes")
                .build();
    }

    public AlexaKvbStreamHandler() {
        super(getSkill());
    }

}