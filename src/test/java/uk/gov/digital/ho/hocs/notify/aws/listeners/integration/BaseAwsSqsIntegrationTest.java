package uk.gov.digital.ho.hocs.notify.aws.listeners.integration;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("local")
public class BaseAwsSqsIntegrationTest {

    private static final String APPROXIMATE_NUMBER_OF_MESSAGES = "ApproximateNumberOfMessages";
    private static final String APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE = "ApproximateNumberOfMessagesNotVisible";

    @Autowired
    public  SqsClient sqsClient;

    @Value("${aws.sqs.notify.url}")
    public String notifyQueue;

    @Before
    public void setup() {
        sqsClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(notifyQueue).build());
    }

    @After
    public void teardown() {
        sqsClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(notifyQueue).build());
    }

    public int getNumberOfMessagesOnQueue() {
        return getValueFromQueue(notifyQueue, APPROXIMATE_NUMBER_OF_MESSAGES);
    }

    public int getNumberOfMessagesNotVisibleOnQueue() {
        return getValueFromQueue(notifyQueue, APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE);
    }

    private int getValueFromQueue(String queue, String attribute) {
        var queueAttributes = sqsClient.getQueueAttributes
                (GetQueueAttributesRequest.builder().queueUrl(queue).attributeNamesWithStrings(attribute).build());
        var messageCount = queueAttributes.attributes().get(attribute);
        return messageCount == null ? 0 : Integer.parseInt(messageCount);
    }
}
