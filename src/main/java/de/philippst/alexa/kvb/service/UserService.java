package de.philippst.alexa.kvb.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import de.philippst.alexa.kvb.model.AlexaKvbUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;

public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private DynamoDBMapper dynamoDBMapper;

    @Inject
    public UserService(AmazonDynamoDB amazonDynamoDB) {
        this.dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);;
    }

    public void updateUserStation(String userId, Integer stationId) {
        if (stationId == null) return;

        AlexaKvbUser alexaKvbUser = new AlexaKvbUser();
        alexaKvbUser.setUserId(userId);
        alexaKvbUser.setStationId(stationId);
        alexaKvbUser.setLastUpdated(new Date());

        this.dynamoDBMapper.save(alexaKvbUser);
    }

    public Integer getUserStation(String userId) {
        AlexaKvbUser alexaKvbUser = this.dynamoDBMapper.load(AlexaKvbUser.class, userId);
        Integer stationId = (alexaKvbUser == null) ? null : alexaKvbUser.getStationId();
        logger.info("search station for user: {} => {}", userId, stationId);
        return stationId;
    }

}
