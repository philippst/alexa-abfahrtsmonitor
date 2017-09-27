package de.philippst.alexa.kvb;

import com.google.inject.AbstractModule;
import de.philippst.alexa.kvb.intent.IntentModule;
import de.philippst.alexa.kvb.service.ServiceModule;

public class KvbAlexaSkillApplicationModule extends AbstractModule{

    @Override
    protected void configure() {
        install(new IntentModule());
        install(new ServiceModule());
    }
}
