package de.philippst.alexa.kvb.intent;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.slu.entityresolution.Resolution;
import com.amazon.speech.slu.entityresolution.StatusCode;
import com.amazon.speech.slu.entityresolution.Value;
import com.amazon.speech.slu.entityresolution.ValueWrapper;
import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.interfaces.display.directive.RenderTemplateDirective;
import com.amazon.speech.speechlet.interfaces.display.element.PlainText;
import com.amazon.speech.speechlet.interfaces.display.element.RichText;
import com.amazon.speech.speechlet.interfaces.display.template.ListTemplate1;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;
import de.philippst.alexa.kvb.exception.KvbException;
import de.philippst.alexa.kvb.exception.StationResolutionException;
import de.philippst.alexa.kvb.model.KvbStation;
import de.philippst.alexa.kvb.model.KvbStationDeparture;
import de.philippst.alexa.kvb.service.StationService;
import de.philippst.alexa.kvb.service.UserService;
import de.philippst.alexa.kvb.utils.AlexaSkillKitHelper;
import de.philippst.alexa.kvb.utils.TextToSpeechHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class DepartureIntentAction implements IntentAction {

    private static final Logger logger = LoggerFactory.getLogger(DepartureIntentAction.class);

    private StationService stationService;
    private UserService userService;

    @Inject
    public DepartureIntentAction(StationService stationService, UserService userService) {
        this.stationService = stationService;
        this.userService = userService;
    }

    @Override
    public SpeechletResponse perform(final IntentRequest intentRequest, final Session session, final Context context) {
        Intent intent = intentRequest.getIntent();
        Slot slotStation = intent.getSlot("Station");
        Integer stationId = null;

        if (slotStation != null) {
            try {
                stationId = this.stationSlotResolution(slotStation);
                userService.updateUserStation(session.getUser().getUserId(), stationId);
            } catch (StationResolutionException e) {
                return this.stationNotFoundResponse();
            }
        }

        if (stationId == null) stationId = userService.getUserStation(session.getUser().getUserId());

        if (stationId == null) return this.askStation();

        Duration departureDuration = null;
        Slot slotDuration = intent.getSlot("DepartureDuration");
        if(slotDuration != null && slotDuration.getValue() != null){
            Duration minDuration = Duration.parse(slotDuration.getValue());
            if(minDuration.toMinutes() > 0 && minDuration.toMinutes() < 200)
                departureDuration = minDuration;
        }

        logger.info("Processing stationId: {} duration: {}", stationId,departureDuration);
        return this.stationDepartureResponse(stationId, session, context, departureDuration);
    }

    private SpeechletResponse stationNotFoundResponse(){
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText("Es tut mir leid, ich habe die Haltestelle nicht verstanden. Bitte nutze möglichst die " +
                "vollständige Bezeichnung und keine Abkürzungen. Wie heißt die Haltestelle?");

        // Create reprompt
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Bitte nenne mir den Namen der Haltstelle oder sage 'Abbrechen'.");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(speech, reprompt);
    }

    private SpeechletResponse stationDepartureResponse(Integer stationId, final Session session, Context context,
                                                       Duration minDuration) {
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

                        stringBuilder.append(String.format("Linie %s nach %s ", departure.getLine(), departure.getDestination()));

                        if (departure.getDuration().toMinutes() == 0) stringBuilder.append("Abfahrt sofort.");
                        if (departure.getDuration().toMinutes() == 1) stringBuilder.append("in einer Minute.");
                        if (departure.getDuration().toMinutes() > 1)
                            stringBuilder.append(String.format("in %s Minuten.", departure.getDuration().toMinutes()));
                        stringBuilder.append("<break strength=\"strong\" />");
                    }
                }
            }

            if (station.getDisruptionMessage().size() > 0) {
                session.setAttribute("disruption", station.getDisruptionMessage());
                stringBuilder.append("<break strength=\"strong\" />Es liegen Fahrplanunregelmäßigkeiten vor. " +
                        "Möchtest du diese hören?");
            }

            String textString = stringBuilder.toString();
            SsmlOutputSpeech speech = new SsmlOutputSpeech();
            speech.setSsml("<speak>" + textString + "</speak>");

            SimpleCard stationCard = this.getStationCard(station);

            SpeechletResponse speechletResponse;

            if (station.getDisruptionMessage().size() > 0) {
                PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
                repromptSpeech.setText("Möchtest du die Fahrplanunregelmäßigkeiten hören?");
                Reprompt reprompt = new Reprompt();
                reprompt.setOutputSpeech(repromptSpeech);
                speechletResponse = SpeechletResponse.newAskResponse(speech,reprompt,stationCard);
            } else {
                speechletResponse = SpeechletResponse.newTellResponse(speech,stationCard);
            }
            if(AlexaSkillKitHelper.isDisplaySupported(context)) {
                speechletResponse.setDirectives(Collections.singletonList(this.getStationRenderTemplate(station)));
            }
            return speechletResponse;

        } catch (KvbException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SimpleCard getStationCard(KvbStation station){
        SimpleCard simpleCard = new SimpleCard();
        simpleCard.setTitle(station.getTitle());
        StringBuilder stringBuilder = new StringBuilder();

        List<KvbStationDeparture> departures = station.getDepartures();

        if (departures.size() == 0) {
            stringBuilder.append("Aktuell kein Fahrbetrieb an dieser Haltestelle.");
        } else {
            departures.stream().limit(10).forEach( departure ->
                    stringBuilder.append(
                            String.format(
                                    "Linie %s %s in %s Min. \n",
                                    departure.getLine(),
                                    departure.getDestination(),
                                    departure.getMinutes()
                            )
                    )
            );
        }

        stringBuilder.append(station.getDisruptionMessage().stream().collect(Collectors.joining(" \n")));
        simpleCard.setContent(stringBuilder.toString());

        return simpleCard;
    }

    private SpeechletResponse askStation() {
        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText("An welcher Haltestelle?");

        // Create reprompt
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Bitte nenne den Namen der Haltstelle.");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(speech, reprompt);
    }

    private RenderTemplateDirective getStationRenderTemplate(KvbStation station){

        List<ListTemplate1.ListItem> listItems = new ArrayList<>();

        for (KvbStationDeparture departure : station.getDepartures()) {
            ListTemplate1.ListItem item = new ListTemplate1.ListItem();

            PlainText destinationText = new PlainText();
            destinationText.setText(departure.getDestination());

            RichText lineText = new RichText();
            lineText.setText("<b>Linie "+departure.getLine()+"</b>");

            PlainText minutesText = new PlainText();
            minutesText.setText(String.valueOf(departure.getMinutes())+" Min");

            ListTemplate1.ListItem.TextContent text = new ListTemplate1.ListItem.TextContent();
            text.setPrimaryText(lineText);
            text.setSecondaryText(destinationText);
            text.setTertiaryText(minutesText);

            item.setTextContent(text);
            listItems.add(item);
        }

        ListTemplate1 template = new ListTemplate1();
        template.setTitle("Abfahrt "+station.getTitle());
        template.setListItems(listItems);

        RenderTemplateDirective render = new RenderTemplateDirective();
        render.setTemplate(template);

        return render;
    }

    public Integer stationSlotResolution(Slot stationSlot) throws StationResolutionException {
        if(stationSlot.getValue() == null){
            logger.error("Station slot is null.");
            throw new StationResolutionException(stationSlot.getValue());
        }

        Resolution res = stationSlot.getResolutions().getResolutionAtIndex(0);
        if(res.getStatus().getCode() == StatusCode.ER_SUCCESS_MATCH){
            ValueWrapper valueWrapper = res.getValueWrapperAtIndex(0);
            Value value = valueWrapper.getValue();
            logger.info("Station resolution passed. ID: {} NAME: {} LISTEN: {}",
                    value.getId(),
                    value.getName(),
                    stationSlot.getValue());
            return Integer.valueOf(value.getId());
        } else {
            logger.warn("Station resolution failed. STATUS: {} LISTEN: {}",res.getStatus(),stationSlot.getValue());
            throw new StationResolutionException(stationSlot.getValue());
        }
    }
}
