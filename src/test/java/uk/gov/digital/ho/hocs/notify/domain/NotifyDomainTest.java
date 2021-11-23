package uk.gov.digital.ho.hocs.notify.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.digital.ho.hocs.notify.api.NotifyService;
import uk.gov.digital.ho.hocs.notify.api.dto.Command;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotifyDomainTest {


    @Mock
    private Command mockCommand;

    @Mock
    private NotifyService notifyService;

    private NotifyDomain notifyDomain;

    @BeforeEach
    void setup() {
        notifyDomain = new NotifyDomain(notifyService);
    }

    @Test
    void shouldUseCollaboratorsInExecuteCommandMethod() {

        notifyDomain.executeCommand(mockCommand);

        verify(mockCommand).execute(notifyService);
    }

}