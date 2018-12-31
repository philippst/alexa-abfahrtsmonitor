package de.philippst.alexa.kvb.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.slu.entityresolution.Resolution;
import com.amazon.ask.model.slu.entityresolution.StatusCode;
import com.amazon.ask.model.slu.entityresolution.Value;
import com.amazon.ask.response.ResponseBuilder;
import de.philippst.alexa.kvb.exception.KvbException;
import de.philippst.alexa.kvb.model.KvbStation;
import de.philippst.alexa.kvb.model.KvbStationDeparture;
import de.philippst.alexa.kvb.service.StationService;
import de.philippst.alexa.kvb.utils.CardUiHelper;
import de.philippst.alexa.kvb.utils.TextToSpeechHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.amazon.ask.request.Predicates.intentName;

public class DepartureIntentHandler implements RequestHandler {

    private final Logger logger = LoggerFactory.getLogger(DepartureIntentHandler.class);

    private StationService stationService;

    public DepartureIntentHandler(StationService stationService) {
        this.stationService = stationService;
    }

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(
                intentName("GetStationAbfahrtsdaten")
        );
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();

        Intent intent = intentRequest.getIntent();
        Map<String, Slot> slots = intent.getSlots();

        Integer stationId;
        Slot stationSlot = slots.get("Station");

        if(stationSlot.getValue() != null){
            logger.info("Received station slot: {}",stationSlot.getValue());
            Optional<Integer> optionalStationSlotId = this.stationSlotResolution(stationSlot);
            if(optionalStationSlotId.isPresent()){
                stationId = optionalStationSlotId.get();
                this.savePersistentStationId(input,stationId);
            } else {
                logger.info("Resolution of station slot failed: {}",stationSlot.getValue());
                return this.askForStation(input);
            }
        } else {
            logger.info("Received no station slot.");
            Optional<Integer> optionalPersistentStationId = this.getPersistentStationId(input);
            if(optionalPersistentStationId.isPresent()) {
                stationId = optionalPersistentStationId.get();
                logger.info("Use persistent stationId: {} userId: {}",
                        stationId, input.getRequestEnvelope().getSession().getUser().getUserId()
                );
            } else {
                return this.askForStation(input);
            }
        }

        /* Check if optional duration slot is available */
        Duration departureDuration = null;
        Slot slotDuration = slots.get("DepartureDuration");
        if(slotDuration != null && slotDuration.getValue() != null){
            Duration minDuration = Duration.parse(slotDuration.getValue());
            if(minDuration.toMinutes() > 0 && minDuration.toMinutes() < 200)
                departureDuration = minDuration;
        }

        logger.info("DepartureResponse for stationId: {} duration: {}", stationId,departureDuration);
        return this.stationDepartureResponse(input,stationId,departureDuration);
    }

    private Optional<Integer> getPersistentStationId(HandlerInput input){
        Map<String, Object> persistentAttribues = input.getAttributesManager().getPersistentAttributes();
        BigDecimal persistetValue = (BigDecimal) persistentAttribues.get("stationId");
        if(persistetValue != null) return Optional.of(persistetValue.intValue());
        return Optional.empty();
    }

    private void savePersistentStationId(HandlerInput input, Integer stationId){
        Map<String, Object> persistentAttribues = input.getAttributesManager().getPersistentAttributes();
        persistentAttribues.put("stationId",stationId);
        input.getAttributesManager().setPersistentAttributes(persistentAttribues);
        input.getAttributesManager().savePersistentAttributes();
        logger.info("Updated persistent user attribute stationId: {} userId: {}",
                stationId, input.getRequestEnvelope().getSession().getUser().getUserId()
        );
    }

    private Optional<Response> askForStation(HandlerInput input) {
        ResponseBuilder responseBuilder = input.getResponseBuilder();
        IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
        Intent currentIntent = intentRequest.getIntent();
        return responseBuilder.addDelegateDirective(currentIntent).build();
    }

    private Optional<Integer> stationSlotResolution(Slot stationSlot) {
        if(stationSlot.getValue() == null){
            logger.info("Station slot is null.");
            return Optional.empty();
        }

        Resolution resolution = stationSlot.getResolutions().getResolutionsPerAuthority().get(0);
        if(resolution.getStatus().getCode() == StatusCode.ER_SUCCESS_MATCH){
            Value value = resolution.getValues().get(0).getValue();
            logger.info("Station resolution passed: id={} name='{}' listen='{}'",
                    value.getId(),
                    value.getName(),
                    stationSlot.getValue());
            return Optional.of(Integer.valueOf(value.getId()));
        } else {
            logger.warn("Station resolution failed for '{}'.",stationSlot.getValue());
            return Optional.empty();
        }
    }


    private Optional<Response> stationDepartureResponse(HandlerInput input, Integer stationId, Duration minDuration) {
        logger.info("getStationDepartureResponse id: {}", stationId);

        try {
            KvbStation station = this.stationService.getStationDeparture(stationId);
            List<KvbStationDeparture> departures = station.getDepartures();

            StringBuilder stringBuilder = new StringBuilder();

            String stationTitleSSML = TextToSpeechHelper.stationSSML(station.getTitle());

            if (departures.size() == 0) {
                stringBuilder.append(String.format("Ab %s aktuell kein Fahrbetrieb. ", stationTitleSSML));
            } else {

                if(minDuration != null){
                    departures.removeIf(p -> p.getDuration().compareTo(minDuration) < 0);
                }
                departures = departures.stream().limit(4).collect(Collectors.toList());

                if(departures.size() == 0){
                    stringBuilder.append(String.format("Ab %s keine passenden Abfahrten. ", stationTitleSSML));
                } else {
                    stringBuilder.append(String.format("Ab %s: ", stationTitleSSML));
                    for (KvbStationDeparture departure : departures) {

                        stringBuilder.append(
                                String.format("Linie %s nach %s ", departure.getLine(), TextToSpeechHelper.stationSSML(departure.getDestination()))
                        );

                        if (departure.getDuration().toMinutes() == 0) stringBuilder.append("Abfahrt sofort.");
                        if (departure.getDuration().toMinutes() == 1) stringBuilder.append("in einer Minute.");
                        if (departure.getDuration().toMinutes() > 1)
                            stringBuilder.append(String.format("in %s Minuten.", departure.getDuration().toMinutes()));
                        stringBuilder.append(" <break strength=\"strong\" />");
                    }
                }
            }

            if (station.getDisruptionMessage().size() > 0) {

                Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
                sessionAttributes.put("disruption",station.getDisruptionMessage());
                input.getAttributesManager().setSessionAttributes(sessionAttributes);

                stringBuilder.append("<break strength=\"strong\" />Es liegen Fahrplanunregelmäßigkeiten vor. " +
                        "Möchtest du diese hören?");
            }

            String textString = stringBuilder.toString();

            ResponseBuilder responseBuilder = input.getResponseBuilder()
                    .withSpeech(textString)
                    .withCard(CardUiHelper.getStationCard(station));

            if (station.getDisruptionMessage().size() > 0) {
                responseBuilder.withReprompt("Möchtest du die Fahrplanunregelmäßigkeiten hören?");
            }

            return responseBuilder.build();

        } catch (KvbException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
