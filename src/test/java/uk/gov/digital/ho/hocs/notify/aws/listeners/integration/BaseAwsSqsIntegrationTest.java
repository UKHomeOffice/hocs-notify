package uk.gov.digital.ho.hocs.notify.aws.listeners.integration;

import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

import java.util.concurrent.ExecutionException;

@ActiveProfiles("local")
public class BaseAwsSqsIntegrationTest {

    @Autowired
    public SqsAsyncClient sqsClient;

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

    public int getNumberOfMessagesOnQueue() throws ExecutionException, InterruptedException {
        return getValueFromQueue(notifyQueue, QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES);
    }

    public int getNumberOfMessagesNotVisibleOnQueue() throws ExecutionException, InterruptedException {
        return getValueFromQueue(notifyQueue, QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE);
    }

    private int getValueFromQueue(String queue, QueueAttributeName attribute) throws ExecutionException, InterruptedException {
        var queueAttributes = sqsClient.getQueueAttributes
                (GetQueueAttributesRequest.builder().queueUrl(queue).attributeNames(attribute).build()).get() ;
        var messageCount = queueAttributes.attributes().get(attribute);
        return messageCount == null ? 0 : Integer.parseInt(messageCount);
    }
}
