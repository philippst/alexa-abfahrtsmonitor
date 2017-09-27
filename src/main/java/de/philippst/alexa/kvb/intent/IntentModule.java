package de.philippst.alexa.kvb.intent;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

public class IntentModule extends AbstractModule {
    @Override
    protected void configure() {

        MapBinder<String, IntentAction> mapBinder = MapBinder.newMapBinder(binder(), String.class, IntentAction.class);

        // Amazon intents
        mapBinder.addBinding("AMAZON.HelpIntent").to(AmazonHelpIntentAction.class);
        mapBinder.addBinding("AMAZON.StartOverIntent").to(AmazonHelpIntentAction.class);
        mapBinder.addBinding("AMAZON.StopIntent").to(AmazonStopIntentAction.class);
        mapBinder.addBinding("AMAZON.CancelIntent").to(AmazonStopIntentAction.class);

        mapBinder.addBinding("AMAZON.YesIntent").to(HearMoreIntentAction.class);
        mapBinder.addBinding("AMAZON.NoIntent").to(AmazonStopIntentAction.class);

        // custom intents
        mapBinder.addBinding("DisruptionTrain").to(DisruptionIntentAction.class);
        mapBinder.addBinding("DisruptionBus").to(DisruptionIntentAction.class);
        mapBinder.addBinding("GetStationAbfahrtsdaten").to(DepartureIntentAction.class);
        mapBinder.addBinding("WelcomeIntent").to(WelcomeIntentAction.class);

    }
}
