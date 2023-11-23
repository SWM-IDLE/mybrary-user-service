package kr.mybrary.userservice.global.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
@Slf4j
@Getter
public class AwsSnsConfig {

    @Value("${spring.cloud.aws.sns.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.sns.credentials.secret-key}")
    private String secretKey;

    @Value("${spring.cloud.aws.sns.region.static}")
    private String region;

    @Value("${spring.cloud.aws.sns.topic.arn}")
    private String topicArn;

    @Bean
    public SnsClient getSnsClient() {
        return SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                )).build();
    }
}