package de.philippst.alexa.kvb.intent;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.interfaces.display.directive.RenderTemplateDirective;
import com.amazon.speech.speechlet.interfaces.display.element.PlainText;
import com.amazon.speech.speechlet.interfaces.display.element.RichText;
import com.amazon.speech.speechlet.interfaces.display.template.BodyTemplate1;
import com.amazon.speech.speechlet.interfaces.display.template.ListTemplate1;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.google.common.base.Joiner;
import de.philippst.alexa.kvb.service.StationService;
import de.philippst.alexa.kvb.utils.AlexaSkillKitHelper;
import de.philippst.alexa.kvb.utils.TextToSpeechHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DisruptionIntentAction implements IntentAction {

    private final Logger logger = LoggerFactory.getLogger(DepartureIntentAction.class);

    private StationService stationService;

    @Inject
    public DisruptionIntentAction(StationService stationService) {
        this.stationService = stationService;
    }

    @Override
    public SpeechletResponse perform(final IntentRequest intentRequest, final Session session, final Context context) {

        Intent intent = intentRequest.getIntent();
        List<String> disruptions;

        try {
            if(intent.getName().equals("DisruptionBus")){
                disruptions = this.stationService.getGlobalDisruptionMessages(true);
            } else {
                disruptions = this.stationService.getGlobalDisruptionMessages(false);
            }

            logger.debug("disruptions {}: {}",intent.getName(),disruptions.size());

            if (disruptions.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();

                disruptions.forEach(disruptionMessage ->
                    stringBuilder.append(" ").append(TextToSpeechHelper.disruptionSSML(disruptionMessage))
                );

                String textString = stringBuilder.toString();
                SsmlOutputSpeech speech = new SsmlOutputSpeech();
                speech.setSsml("<speak>" + textString + "</speak>");
                SimpleCard disruptionCard = this.getDisruptionCard(disruptions);

                SpeechletResponse speechletResponse = SpeechletResponse.newTellResponse(speech,disruptionCard);
                if(AlexaSkillKitHelper.isDisplaySupported(context)) {
                    speechletResponse.setDirectives(Collections.singletonList(this.getDisruptionRenderTemplate(disruptions)));
                }
                return speechletResponse;
            } else {
                PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                speech.setText("Es liegen aktuell keine Störungen vor.");
                return SpeechletResponse.newTellResponse(speech);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    private SimpleCard getDisruptionCard(List<String> disruptionMessages){
        SimpleCard simpleCard = new SimpleCard();
        simpleCard.setTitle("Störungen");
        simpleCard.setContent(Joiner.on(" \n").join(disruptionMessages));
        return simpleCard;
    }

    private RenderTemplateDirective getDisruptionRenderTemplate(List<String> disruptionMessages){

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<font size=\"3\">")
                .append(Joiner.on("<br /><br />").join(disruptionMessages))
                .append("</font>");

        RichText richText = new RichText();
        richText.setText(stringBuilder.toString());

        BodyTemplate1.TextContent textContent = new BodyTemplate1.TextContent();
        textContent.setPrimaryText(richText);
        BodyTemplate1 template = new BodyTemplate1();
        template.setTitle("Betriebsstörungen");
        template.setTextContent(textContent);

        RenderTemplateDirective render = new RenderTemplateDirective();
        render.setTemplate(template);

        return render;
    }
}
