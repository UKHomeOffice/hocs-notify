package uk.gov.digital.ho.hocs.notify.aws.config;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;

import java.time.Duration;

@Configuration
@Profile({"sqs"})
public class SqsConfiguration {

    @Bean
    public SqsAsyncClient sqsAsyncClient(@Value("${aws.sqs.access.key}") String accessKey,
                                         @Value("${aws.sqs.secret.key}") String secretKey,
                                         @Value("${aws.sqs.region}") Region region) {

        NettyNioAsyncHttpClient.Builder httpClient = NettyNioAsyncHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(120));

        return SqsAsyncClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .httpClientBuilder(httpClient)
                .build();
    }

}
