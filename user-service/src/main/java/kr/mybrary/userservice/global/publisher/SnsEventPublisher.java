package kr.mybrary.userservice.global.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import kr.mybrary.userservice.global.config.AwsSnsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Service
@RequiredArgsConstructor
public class SnsEventPublisher {

    private final AwsSnsConfig awsConfig;
    private final ObjectMapper objectMapper;

    public PublishResponse publishToSns(Map<String, Object> messageData, SnsTopic topic) {

        String messageJson;

        try {
            messageJson = objectMapper.writeValueAsString(messageData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert message data to JSON", e);
        }

        PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(awsConfig.getTopicArn() + topic.getTopicName())
                .subject("userService request")
                .message(messageJson)
                .build();

        SnsClient snsClient = awsConfig.getSnsClient();
        return snsClient.publish(publishRequest);
    }

}
