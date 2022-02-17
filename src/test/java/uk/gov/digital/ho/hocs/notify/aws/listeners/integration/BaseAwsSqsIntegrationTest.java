package uk.gov.digital.ho.hocs.notify.aws.listeners.integration;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
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
    public AmazonSQSAsync amazonSQSAsync;

    @Value("${aws.sqs.notify.url}")
    public String notifyQueue;

    @Before
    public void setup() {
        amazonSQSAsync.purgeQueue(new PurgeQueueRequest(notifyQueue));
    }

    @After
    public void teardown() {
        amazonSQSAsync.purgeQueue(new PurgeQueueRequest(notifyQueue));
    }

    public int getNumberOfMessagesOnQueue() {
        return getValueFromQueue(notifyQueue, APPROXIMATE_NUMBER_OF_MESSAGES);
    }

    public int getNumberOfMessagesNotVisibleOnQueue() {
        return getValueFromQueue(notifyQueue, APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE);
    }

    private int getValueFromQueue(String queue, String attribute) {
        var queueAttributes = amazonSQSAsync.getQueueAttributes(queue, List.of(attribute));
        var messageCount = queueAttributes.getAttributes().get(attribute);
        return messageCount == null ? 0 : Integer.parseInt(messageCount);
    }


}
