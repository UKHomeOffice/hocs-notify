package uk.gov.digital.ho.hocs.notify.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.hocs.notify.api.dto.TeamAssignChangeCommand;
import uk.gov.digital.ho.hocs.notify.domain.NotifyDomain;
import uk.gov.digital.ho.hocs.notify.domain.NotifyType;
import uk.gov.digital.ho.hocs.notify.domain.exception.EntityCreationException;
import uk.gov.digital.ho.hocs.notify.routes.NotifyConsumer;

import java.text.SimpleDateFormat;
import java.util.UUID;

import static org.mockito.Mockito.*;

//https://issues.apache.org/jira/browse/CAMEL-11807
@RunWith(MockitoJUnitRunner.class)
public class NotifyConsumerTest extends CamelTestSupport {

    private String notifyQueue = "direct:notify-queue";
    private String dlq = "mock:notify-queue-dlq";
    private ObjectMapper mapper;

    private UUID caseUUID;
    private UUID stageUUID;

    @Mock
    private NotifyDomain mockNotifyDomain;

    @Before
    public void setUpTest(){
        mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        context.setStreamCaching(true);
        caseUUID = UUID.randomUUID();
        stageUUID = UUID.randomUUID();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new NotifyConsumer(mockNotifyDomain, notifyQueue, dlq, 0,0,0);
    }

    @Test
    public void shouldCallNotifyDomain() throws JsonProcessingException {

        TeamAssignChangeCommand teamAssignChangeCommand = new TeamAssignChangeCommand(caseUUID, stageUUID, "some ref", UUID.randomUUID(), NotifyType.ALLOCATE_PRIVATE_OFFICE.toString());
        String json = mapper.writeValueAsString(teamAssignChangeCommand);
        template.sendBody(notifyQueue, json);
        verify(mockNotifyDomain, times(1)).executeCommand(teamAssignChangeCommand);
        verifyNoMoreInteractions(mockNotifyDomain);
    }

    @Test
    public void shouldNotProcessMessageWhenMarshallingFails() throws JsonProcessingException, InterruptedException {
        getMockEndpoint(dlq).setExpectedCount(1);
        String json = mapper.writeValueAsString("{invalid:invalid}");
        template.sendBody(notifyQueue, json);
        verify(mockNotifyDomain, never()).executeCommand(any());
        getMockEndpoint(dlq).assertIsSatisfied();
    }

    @Test
    public void shouldTransferToDLQOnFailure() throws JsonProcessingException, InterruptedException {

        TeamAssignChangeCommand teamAssignChangeCommand = new TeamAssignChangeCommand(caseUUID, stageUUID, "some ref", UUID.randomUUID(), NotifyType.ALLOCATE_PRIVATE_OFFICE.toString());

        doThrow(EntityCreationException.class)
                .when(mockNotifyDomain).executeCommand(teamAssignChangeCommand);
        getMockEndpoint(dlq).setExpectedCount(1);
        String json = mapper.writeValueAsString(teamAssignChangeCommand);
        template.sendBody(notifyQueue, json);
        getMockEndpoint(dlq).assertIsSatisfied();
    }

}