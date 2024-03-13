package uk.gov.digital.ho.hocs.notify.aws.config;

import io.awspring.cloud.sqs.config.SqsBootstrapConfiguration;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

import static software.amazon.awssdk.regions.Region.EU_WEST_2;

@Configuration
@Profile({"sqs"})
public class SqsConfiguration {
    
    @Bean
    public SqsAsyncClient sqsAsyncClient(@Value("${aws.sqs.access.key}") String accessKey,
                                         @Value("${aws.sqs.secret.key}") String secretKey) {
        return SqsAsyncClient.builder()
                .region(EU_WEST_2)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }

}
