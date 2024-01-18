package uk.gov.digital.ho.hocs.notify.aws.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import io.awspring.cloud.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"local"})
public class LocalStackConfiguration {

    @Primary
    @Bean
    public AmazonSQSAsync awsSqsClient(
            @Value("${aws.sqs.config.url}") String awsBaseUrl) {
        return AmazonSQSAsyncClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials("test", "test")))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsBaseUrl, "eu-west-2"))
                .build();
    }

    @Primary
    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSqs) {
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();

        factory.setAmazonSqs(amazonSqs);
        factory.setMaxNumberOfMessages(10);

        return factory;
    }

}
