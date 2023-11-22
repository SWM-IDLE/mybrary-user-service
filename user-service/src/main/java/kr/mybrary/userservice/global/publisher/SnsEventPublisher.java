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

    private final static String MESSAGE_SUBJECT = "USER SERVICE REQUEST";
    private final AwsSnsConfig awsConfig;
    private final ObjectMapper objectMapper;

    public void publishToSns(Map<String, Object> messageData, SnsTopic topic) {

        try {
            PublishRequest publishRequest = PublishRequest.builder()
                    .topicArn(awsConfig.getTopicArn() + topic.getTopicName())
                    .subject(MESSAGE_SUBJECT)
                    .message(objectMapper.writeValueAsString(messageData))
                    .build();

            SnsClient snsClient = awsConfig.getSnsClient();
            snsClient.publish(publishRequest);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert message data to JSON", e);
        }
    }
}
