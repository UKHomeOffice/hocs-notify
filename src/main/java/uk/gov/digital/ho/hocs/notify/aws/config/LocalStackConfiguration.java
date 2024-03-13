package uk.gov.digital.ho.hocs.notify.aws.config;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.net.URI;

@Configuration
@Profile({"local"})
public class LocalStackConfiguration {

    @Bean
    public SqsAsyncClient sqsAsyncClient(@Value("${aws.sqs.config.url}") String awsBaseUrl, @Value("${aws.sqs.region}") Region region) {
        return SqsAsyncClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")
                ))
                .endpointOverride(URI.create(awsBaseUrl))
                .build();
    }

}
