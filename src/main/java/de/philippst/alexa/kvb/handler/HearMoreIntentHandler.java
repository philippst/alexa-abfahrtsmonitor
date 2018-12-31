package de.philippst.alexa.kvb.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import de.philippst.alexa.kvb.utils.TextToSpeechHelper;

import java.util.List;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class HearMoreIntentHandler implements RequestHandler {

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(
                intentName("AMAZON.YesIntent")
        );
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {

        @SuppressWarnings("unchecked")
        List<String> disruptionMessages = (List<String>) input.getAttributesManager().getSessionAttributes().get("disruption");
        if(disruptionMessages == null || disruptionMessages.size() == 0) return input.getResponseBuilder().build();

        StringBuilder stringBuilder = new StringBuilder();

        for (String disruptionMessage : disruptionMessages) {
            stringBuilder.append(" ").append(TextToSpeechHelper.disruptionSSML(disruptionMessage));
        }

        return input.getResponseBuilder()
                .withSpeech(stringBuilder.toString())
                .build();
    }
}