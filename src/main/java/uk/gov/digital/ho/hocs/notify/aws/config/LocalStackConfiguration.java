package uk.gov.digital.ho.hocs.notify.aws.config;

import io.awspring.cloud.sqs.config.SqsBootstrapConfiguration;
import lombok.SneakyThrows;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

import static software.amazon.awssdk.regions.Region.EU_WEST_2;

@Configuration
@Profile({"local"})
public class LocalStackConfiguration {

    @SneakyThrows
    @Primary
    @Bean
    public SqsClient awsSqsClient(
            @Value("${aws.sqs.config.url}") String awsBaseUrl) {

        return  SqsClient.builder()
                .region(EU_WEST_2)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .endpointOverride(new URI(awsBaseUrl))
                .build();
    }

}
