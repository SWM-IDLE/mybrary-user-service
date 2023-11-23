package kr.mybrary.userservice.global.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import kr.mybrary.userservice.global.config.AwsSnsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class SnsEventPublisher {

    private final static String MESSAGE_SUBJECT = "USER SERVICE REQUEST";
    private final AwsSnsConfig awsConfig;
    private final ObjectMapper objectMapper;

    public void publishToSns(Map<String, Object> messageData, SnsTopic topic) {

        String message;
        try {
            message = objectMapper.writeValueAsString(messageData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert message data to JSON", e);
        }

        String topicArn = awsConfig.getTopicArn() + topic.getTopicName();
        PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(topicArn)
                .subject(MESSAGE_SUBJECT)
                .message(message)
                .build();

        SnsClient snsClient = awsConfig.getSnsClient();
        snsClient.publish(publishRequest);
        log.info("Publishing message to SNS MESSAGE: {}", message);
    }
}
