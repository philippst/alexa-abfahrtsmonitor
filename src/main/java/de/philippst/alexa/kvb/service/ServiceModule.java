package de.philippst.alexa.kvb.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ServiceModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    AmazonDynamoDB provideAmazonDynamoDB(){
        return AmazonDynamoDBClientBuilder.standard().build();
    }
}
