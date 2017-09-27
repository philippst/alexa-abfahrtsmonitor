package de.philippst.alexa.kvb.intent;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;
import de.philippst.alexa.kvb.service.StationService;
import de.philippst.alexa.kvb.utils.TextToSpeechHelper;

import java.io.IOException;
import java.util.List;

public class DisruptionIntentAction implements IntentAction {

    @Override
    public SpeechletResponse perform(Intent intent, Session session) {

        List<String> disruptions;

        try {
            if(intent.getName().equals("DisruptionBus")){
                disruptions = StationService.getDisruptionMessagesBus();
            } else {
                disruptions = StationService.getDisruptionMessagesTrain();
            }

            if (disruptions.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String disruptionMessage : disruptions) {
                    stringBuilder.append(" " + TextToSpeechHelper.disruptionSSML(disruptionMessage));
                }
                String textString = stringBuilder.toString();
                SsmlOutputSpeech speech = new SsmlOutputSpeech();
                speech.setSsml("<speak>" + textString + "</speak>");
                SimpleCard disruptionCard = this.getDisruptionCard(disruptions);
                return SpeechletResponse.newTellResponse(speech,disruptionCard);
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

    public static SimpleCard getDisruptionCard(List<String> disruptionMessages){
        SimpleCard simpleCard = new SimpleCard();
        simpleCard.setTitle("Störungen");
        StringBuilder stringBuilder = new StringBuilder();

        for (String disruptionMessage : disruptionMessages) {
            stringBuilder.append(String.format("%s \n", disruptionMessage));
        }

        simpleCard.setContent(stringBuilder.toString());

        return simpleCard;
    }
}