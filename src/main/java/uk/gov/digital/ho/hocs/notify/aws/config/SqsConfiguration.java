package uk.gov.digital.ho.hocs.notify.aws.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import io.awspring.cloud.sqs.config.SqsBootstrapConfiguration;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Import(SqsBootstrapConfiguration.class)
@Configuration
@Profile({"sqs"})
public class SqsConfiguration {

    @Primary
    @Bean
    public AmazonSQSAsync awsSqsClient(@Value("${aws.sqs.access.key}") String accessKey,
                                       @Value("${aws.sqs.secret.key}") String secretKey,
                                       @Value("${aws.sqs.region}") String region) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        return AmazonSQSAsyncClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    @Bean
    public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory() {
        return SqsMessageListenerContainerFactory
                .builder()
                .sqsAsyncClient(sqsAsyncClient())
                .build();
    }

    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder().build();
    }
}
