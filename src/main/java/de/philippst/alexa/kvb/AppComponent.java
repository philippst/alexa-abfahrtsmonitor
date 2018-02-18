package de.philippst.alexa.kvb;

import dagger.Component;
import de.philippst.alexa.kvb.intent.IntentModule;
import de.philippst.alexa.kvb.service.ServiceModule;

@Component(modules = {ServiceModule.class, IntentModule.class})
public interface AppComponent {
    KvbAlexaSkillSpeechlet alexaSpeechlet();
}
