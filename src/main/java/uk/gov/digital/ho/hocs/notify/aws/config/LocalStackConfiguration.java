package uk.gov.digital.ho.hocs.notify.aws.config;

import io.awspring.cloud.sqs.config.SqsBootstrapConfiguration;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.SneakyThrows;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.net.URI;

import static software.amazon.awssdk.regions.Region.EU_WEST_2;

@Import(SqsBootstrapConfiguration.class)
@Configuration
@Profile({"local"})
public class LocalStackConfiguration {

    @Bean
    public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient) {
        return SqsTemplate.builder().sqsAsyncClient(sqsAsyncClient).build();
    }

    @Bean
    public SqsAsyncClient sqsAsyncClient(@Value("${aws.sqs.config.url}") String awsBaseUrl) {
        return SqsAsyncClient.builder()
                .region(EU_WEST_2)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")
                ))
                .endpointOverride(URI.create(awsBaseUrl))
                .build();
    }

}
