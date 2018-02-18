package de.philippst.alexa.kvb.intent;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module
public class IntentModule {

    @Provides @IntoMap
    @StringKey("AMAZON.HelpIntent")
    IntentAction providesAmazonHelpIntentAction(AmazonHelpIntentAction amazonHelpIntentAction) {
        return amazonHelpIntentAction;
    }

    @Provides @IntoMap
    @StringKey("AMAZON.StopIntent")
    IntentAction providesAmazonStopIntentAction(AmazonStopIntentAction amazonStopIntentAction) {
        return amazonStopIntentAction;
    }

    @Provides @IntoMap
    @StringKey("HearMoreIntent")
    IntentAction providesHearMoreIntentAction(HearMoreIntentAction hearMoreIntentAction) {
        return hearMoreIntentAction;
    }

    @Provides @IntoMap
    @StringKey("DisruptionIntent")
    IntentAction providesDisruptionIntentAction(DisruptionIntentAction disruptionIntentAction) {
        return disruptionIntentAction;
    }

    @Provides @IntoMap
    @StringKey("DisruptionTrain")
    IntentAction providesDisruptionTrainAction(DisruptionIntentAction disruptionIntentAction) {
        return disruptionIntentAction;
    }

    @Provides @IntoMap
    @StringKey("DisruptionBus")
    IntentAction providesDisruptionBusAction(DisruptionIntentAction disruptionIntentAction) {
        return disruptionIntentAction;
    }

    @Provides @IntoMap
    @StringKey("GetStationAbfahrtsdaten")
    IntentAction providesDepartureIntentAction(DepartureIntentAction departureIntentAction) {
        return departureIntentAction;
    }

    @Provides @IntoMap
    @StringKey("WelcomeIntent")
    IntentAction providesWelcomeIntentAction(WelcomeIntentAction welcomeIntent) {
        return welcomeIntent;
    }

}
