package de.philippst.alexa.kvb.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import de.philippst.alexa.kvb.model.KvbDisruption;
import de.philippst.alexa.kvb.service.StationService;
import de.philippst.alexa.kvb.utils.CardUiHelper;
import de.philippst.alexa.kvb.utils.TextToSpeechHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class DisruptionIntentHandler implements RequestHandler {

    private final Logger logger = LoggerFactory.getLogger(DisruptionIntentHandler.class);

    private StationService stationService;

    public DisruptionIntentHandler(StationService stationService) {
        this.stationService = stationService;
    }

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(
                intentName("DisruptionIntent")
                .or(intentName("DisruptionTrain")
                .or(intentName("DisruptionBus")))
        );
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        List<KvbDisruption> disruptions;

        try {
            if(input.matches(intentName("DisruptionBus"))){
                disruptions = this.stationService.getGlobalDisruptionMessages(true);
            } else {
                disruptions = this.stationService.getGlobalDisruptionMessages(false);
            }

            logger.debug("disruptions: {}",disruptions.size());

            if (disruptions.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();

                disruptions.forEach(disruptionMessage ->
                        stringBuilder.append(" ").append(TextToSpeechHelper.disruptionSSML(disruptionMessage.toString()))
                );

                return input.getResponseBuilder().withSpeech(stringBuilder.toString()).build();
            } else {
                String noDisruptionsText = "Es liegen aktuell keine Störungen vor.";
                return input.getResponseBuilder()
                        .withSpeech(noDisruptionsText)
                        .withCard(CardUiHelper.getDisruptionCard(disruptions))
                        .build();
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        // @ToDo: Throw exception
        return null;
    }
}
