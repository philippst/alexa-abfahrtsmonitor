package de.philippst.alexa.kvb.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @Provides
    AmazonDynamoDB provideAmazonDynamoDB(){
        return AmazonDynamoDBClientBuilder.standard().build();
    }

    @Provides
    DynamoDBMapper provideDynamoDBMapper(AmazonDynamoDB amazonDynamoDB){
        return new DynamoDBMapper(amazonDynamoDB);
    }

}
