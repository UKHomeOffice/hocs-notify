package uk.gov.digital.ho.hocs.notify.aws.listeners.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import uk.gov.digital.ho.hocs.notify.api.dto.TeamAssignChangeCommand;
import uk.gov.digital.ho.hocs.notify.domain.NotifyDomain;
import uk.gov.digital.ho.hocs.notify.domain.NotifyType;

import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CreateNotifyTest extends BaseAwsSqsIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    public NotifyDomain notifyDomain;

    @Test
    public void consumeMessageFromQueue() throws JsonProcessingException {
        TeamAssignChangeCommand teamAssignChangeCommand = new TeamAssignChangeCommand(UUID.randomUUID(), UUID.randomUUID(), "some ref", UUID.randomUUID(), NotifyType.ALLOCATE_PRIVATE_OFFICE.toString());
        String message = objectMapper.writeValueAsString(teamAssignChangeCommand);

        sqsClient.sendMessage(SendMessageRequest.builder().queueUrl(notifyQueue).messageBody(message).build());

        await().until(() -> getNumberOfMessagesOnQueue() == 0);
        verify(notifyDomain, timeout(100)).executeCommand(any());
    }

    @Test
    public void consumeMessageFromQueue_exceptionMakesMessageNotVisible() throws JsonProcessingException {
        TeamAssignChangeCommand teamAssignChangeCommand = new TeamAssignChangeCommand(UUID.randomUUID(), UUID.randomUUID(), "some ref", UUID.randomUUID(), NotifyType.ALLOCATE_PRIVATE_OFFICE.toString());
        String message = objectMapper.writeValueAsString(teamAssignChangeCommand);

        doThrow(new NullPointerException("TEST")).when(notifyDomain).executeCommand(teamAssignChangeCommand);

        sqsClient.sendMessage(SendMessageRequest.builder().queueUrl(notifyQueue).messageBody(message).build());

        await().until(() -> getNumberOfMessagesOnQueue() == 0);
        await().until(() -> getNumberOfMessagesNotVisibleOnQueue() == 1);

        verify(notifyDomain).executeCommand(teamAssignChangeCommand);
    }

}
