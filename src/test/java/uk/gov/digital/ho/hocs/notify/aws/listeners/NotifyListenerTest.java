package uk.gov.digital.ho.hocs.notify.aws.listeners;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.digital.ho.hocs.notify.api.dto.TeamAssignChangeCommand;
import uk.gov.digital.ho.hocs.notify.domain.NotifyDomain;
import uk.gov.digital.ho.hocs.notify.domain.NotifyType;
import uk.gov.digital.ho.hocs.notify.domain.exception.EntityCreationException;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NotifyListenerTest {

    @Autowired
    private Gson gson;


    @Mock
    private NotifyDomain notifyDomain;

    @Test
    public void callsNotifyDomainWithValidNotifyCommand() {
        TeamAssignChangeCommand teamAssignChangeCommand = new TeamAssignChangeCommand(UUID.randomUUID(), UUID.randomUUID(), "some ref", UUID.randomUUID(), NotifyType.ALLOCATE_PRIVATE_OFFICE.toString());
        String message = gson.toJson(teamAssignChangeCommand);

        NotifyListener notifyListener = new NotifyListener(gson, notifyDomain);

        notifyListener.onNotifyEvent(message);

        verify(notifyDomain).executeCommand(teamAssignChangeCommand);
        verifyNoMoreInteractions(notifyDomain);
    }

    @Test(expected = NullPointerException.class)
    public void callsAuditServiceWithNullCreateCaseMessage() {
        NotifyListener notifyListener = new NotifyListener(gson, notifyDomain);

        notifyListener.onNotifyEvent(null);
    }

    @Test(expected = EntityCreationException.class)
    public void callsAuditServiceWithInvalidCreateCaseMessage() {
        String incorrectMessage = "{test:1}";
        NotifyListener notifyListener = new NotifyListener(gson, notifyDomain);

        notifyListener.onNotifyEvent(incorrectMessage);
    }

}
