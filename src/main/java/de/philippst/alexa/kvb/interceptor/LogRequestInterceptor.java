package de.philippst.alexa.kvb.interceptor;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.interceptor.RequestInterceptor;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogRequestInterceptor implements RequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(LogRequestInterceptor.class);

    @Override
    public void process(HandlerInput input) {
        RequestEnvelope requestEnvelope = input.getRequestEnvelope();
        Request request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();

        logger.info("RequestType='{}' RequestLocale='{}' SessionNew='{}'",
                request.getType(),
                request.getLocale(),
                session.getNew()
        );

        if(request.getType().equals("IntentRequest")){
            IntentRequest intentRequest = (IntentRequest) requestEnvelope.getRequest();
            logger.info("Intent='{}' DialogState='{}'",
                    intentRequest.getIntent().getName(),
                    intentRequest.getDialogState().getValue());
        }

    }

}
