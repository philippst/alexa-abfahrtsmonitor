package de.philippst.alexa.kvb.intent;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
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
import com.google.common.base.Joiner;
import de.philippst.alexa.kvb.exception.KvbException;
import de.philippst.alexa.kvb.model.KvbStation;
import de.philippst.alexa.kvb.model.KvbStationDeparture;
import de.philippst.alexa.kvb.service.StationService;
import de.philippst.alexa.kvb.service.UserService;
import de.philippst.alexa.kvb.utils.AlexaSkillKitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

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
        if (slotStation != null && slotStation.getValue() != null) {
            stationId = stationService.stationProcessor(slotStation.getValue());
            if (stationId == null) {
                return this.stationNotFoundResponse();
            } else {
                userService.updateUserStation(session.getUser().getUserId(), stationId);
            }
        }

        if (stationId == null) stationId = userService.getUserStation(session.getUser().getUserId());

        if (stationId == null) return this.askStation();

        logger.info("Processing stationId: {}", stationId);
        return this.stationDepartureResponse(stationId, session, context);
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

    private SpeechletResponse stationDepartureResponse(Integer stationId, final Session session, Context context) {
        logger.info("getStationDepartureResponse id: {}", stationId);

        try {
            KvbStation station = this.stationService.getStationDeparture(stationId);
            List<KvbStationDeparture> departures = station.getDepartures();

            StringBuilder stringBuilder = new StringBuilder();

            if (departures.size() == 0) {
                stringBuilder.append(String.format("Ab %s aktuell kein Fahrbetrieb. ", station.getTitle()));
            } else {
                stringBuilder.append(String.format("Ab %s: ", station.getTitle()));
                List<KvbStationDeparture> limitDepartures = departures.subList(0, 4 > departures.size() ? departures.size() : 4);
                for (KvbStationDeparture departure : limitDepartures) {
                    stringBuilder.append(String.format("Linie %s nach %s ", departure.getLine(), departure.getDestination()));
                    if (departure.getMinutes() == 0) stringBuilder.append("Abfahrt sofort.");
                    if (departure.getMinutes() == 1) stringBuilder.append("in einer Minute.");
                    if (departure.getMinutes() > 1)
                        stringBuilder.append(String.format("in %s Minuten.", departure.getMinutes()));
                    stringBuilder.append("<break strength=\"strong\" />");
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

        stringBuilder.append(Joiner.on(" \n").join(station.getDisruptionMessage()));
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


}
