package uk.gov.digital.ho.hocs.notify.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.digital.ho.hocs.notify.application.RequestData;
import uk.gov.digital.ho.hocs.notify.client.infoclient.InfoClient;
import uk.gov.digital.ho.hocs.notify.client.infoclient.UserDto;
import uk.gov.digital.ho.hocs.notify.client.notifyclient.NotifyClient;
import uk.gov.digital.ho.hocs.notify.domain.NotifyType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotifyServiceTest {

    @Mock
    private RequestData requestData;

    @Mock
    private InfoClient infoClient;

    @Mock
    private NotifyClient notifyClient;

    private NotifyService notifyService;

    @BeforeEach
    void setup() {
        notifyService = new NotifyService(infoClient, requestData, notifyClient);
    }

    private UUID caseUUID = UUID.randomUUID();
    private UUID stageUUID = UUID.randomUUID();

    private String caseRef = "";

    /* Send user A an email if their case is unallocated by anyone */
    @Test
    void ShouldAlwaysSendEmailUnAllocate() {

        UUID currentUserUUID = UUID.fromString("33333333-0000-0000-0000-000000000000");
        UUID newUserUUID = null;

        when(infoClient.getUser(currentUserUUID)).thenReturn(new UserDto("any", "name", "any", "notify"));

        notifyService.sendUserAssignChangeEmail(caseUUID, stageUUID, caseRef, currentUserUUID, newUserUUID);

        verify(infoClient, times(1)).getUser(currentUserUUID);
        verify(notifyClient, times(1)).sendEmail(caseUUID, stageUUID, "notify", "name", caseRef, NotifyType.UNALLOCATE_INDIVIDUAL );


        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
        verifyZeroInteractions(requestData);
    }

    /* Don't send an email if user A allocated an unallocated case to user A */
    @Test
    void ShouldNotSendSelfEmailUnAllocated() {

        UUID currentUserUUID = null;
        UUID newUserUUID = UUID.fromString("11111111-0000-0000-0000-000000000000");

        when(requestData.userIdUUID()).thenReturn(UUID.fromString("11111111-0000-0000-0000-000000000000"));

        notifyService.sendUserAssignChangeEmail(caseUUID, stageUUID, caseRef, currentUserUUID, newUserUUID);

        verify(requestData, times(1)).userIdUUID();

        verifyZeroInteractions(infoClient);
        verifyZeroInteractions(notifyClient);
        verifyNoMoreInteractions(requestData);
    }

    /* Send user A an email if user B allocated an unallocated case to user A */
    @Test
    void ShouldSendOtherEmailUnAllocated() {

        UUID currentUserUUID = null;
        UUID newUserUUID = UUID.fromString("11111111-0000-0000-0000-000000000000");

        when(infoClient.getUser(newUserUUID)).thenReturn(new UserDto("any", "name", "any", "notify"));
        when(requestData.userIdUUID()).thenReturn(UUID.fromString("22222222-0000-0000-0000-000000000000"));

        notifyService.sendUserAssignChangeEmail(caseUUID, stageUUID, caseRef, currentUserUUID, newUserUUID);

        verify(requestData, times(1)).userIdUUID();
        verify(infoClient, times(1)).getUser(newUserUUID);
        verify(notifyClient, times(1)).sendEmail(caseUUID, stageUUID, "notify", "name", caseRef, NotifyType.ALLOCATE_INDIVIDUAL );

        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
        verifyNoMoreInteractions(requestData);
    }

    /* Send only user B an email is user A allocates user B's case to User A instead */
    @Test
    void ShouldNotSendSelfEmailAllocated() {

        UUID currentUserUUID = UUID.fromString("33333333-0000-0000-0000-000000000000");
        UUID newUserUUID = UUID.fromString("11111111-0000-0000-0000-000000000000");

        when(infoClient.getUser(currentUserUUID)).thenReturn(new UserDto("any", "name", "any", "notify"));
        when(requestData.userIdUUID()).thenReturn(UUID.fromString("11111111-0000-0000-0000-000000000000"));

        notifyService.sendUserAssignChangeEmail(caseUUID, stageUUID, caseRef, currentUserUUID, newUserUUID);

        verify(requestData, times(1)).userIdUUID();
        verify(infoClient, times(1)).getUser(currentUserUUID);
        verify(notifyClient, times(1)).sendEmail(caseUUID, stageUUID, "notify", "name", caseRef, NotifyType.UNALLOCATE_INDIVIDUAL );


        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
        verifyNoMoreInteractions(requestData);
    }

    /* Send an email to user A and user B if I User C allocates a case from user A to user B */
    @Test
    void ShouldSendOtherEmailAllocated() {

        UUID currentUserUUID = UUID.fromString("33333333-0000-0000-0000-000000000000");
        UUID newUserUUID = UUID.fromString("11111111-0000-0000-0000-000000000000");

        when(infoClient.getUser(currentUserUUID)).thenReturn(new UserDto("any", "name", "any", "notify"));
        when(infoClient.getUser(newUserUUID)).thenReturn(new UserDto("any", "name", "any", "notify"));
        when(requestData.userIdUUID()).thenReturn(UUID.fromString("22222222-0000-0000-0000-000000000000"));

        notifyService.sendUserAssignChangeEmail(caseUUID, stageUUID, caseRef, currentUserUUID, newUserUUID);

        verify(requestData, times(1)).userIdUUID();
        verify(infoClient, times(1)).getUser(newUserUUID);
        verify(infoClient, times(1)).getUser(currentUserUUID);
        verify(notifyClient, times(1)).sendEmail(caseUUID, stageUUID, "notify", "name", caseRef, NotifyType.ALLOCATE_INDIVIDUAL );
        verify(notifyClient, times(1)).sendEmail(caseUUID, stageUUID, "notify", "name", caseRef, NotifyType.UNALLOCATE_INDIVIDUAL );


        verifyNoMoreInteractions(infoClient);
        verifyZeroInteractions(notifyClient);
        verifyNoMoreInteractions(requestData);
    }

    /* Send Team email when there is a teamUUID */
    @Test
    void ShouldSendTeamEmail() {

        UUID teamUUID = UUID.randomUUID();

        when(infoClient.getNominatedPeople(teamUUID)).thenReturn(Set.of("Someone"));

        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, teamUUID, NotifyType.DISPATCH_REJECT.toString());

        verify(infoClient, times(1)).getNominatedPeople(teamUUID);
        verify(notifyClient, times(1)).sendEmail(caseUUID, stageUUID, "Someone", "Team", caseRef, NotifyType.DISPATCH_REJECT );

        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
    }

    /* Send Multiple Team email when there are more than one nominated people */
    @Test
    void ShouldSendMultipleTeamEmail() {

        UUID teamUUID = UUID.randomUUID();

        when(infoClient.getNominatedPeople(teamUUID)).thenReturn(Set.of("Someone", "Another"));


        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, teamUUID, NotifyType.DISPATCH_REJECT.toString());

        verify(infoClient, times(1)).getNominatedPeople(teamUUID);
        verify(notifyClient, times(1)).sendEmail(caseUUID, stageUUID, "Someone", "Team", caseRef, NotifyType.DISPATCH_REJECT );
        verify(notifyClient, times(1)).sendEmail(caseUUID, stageUUID, "Another", "Team", caseRef, NotifyType.DISPATCH_REJECT );

        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
    }

    /* Send Team email when there are no nominated people */
    @Test
    void ShouldSendNoTeamEmail() {

        UUID teamUUID = UUID.randomUUID();

        when(infoClient.getNominatedPeople(teamUUID)).thenReturn(new HashSet<>(0));

        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, teamUUID, NotifyType.DISPATCH_REJECT.toString());

        verify(infoClient, times(1)).getNominatedPeople(teamUUID);

        verifyNoMoreInteractions(infoClient);
        verifyZeroInteractions(notifyClient);
    }

    /* Send No Team email when there is no teamUUID */
    @Test
    void ShouldNotSendTeamEmail() {

        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, null, NotifyType.DISPATCH_REJECT.toString());

        verifyZeroInteractions(infoClient);
        verifyZeroInteractions(notifyClient);
    }

    /* Send No Team email when there is an invalid NotifyType */
    @Test
    void ShouldNotSendTeamEmailNotifyTypeInvalid() {

        UUID teamUUID = UUID.randomUUID();

        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, teamUUID,"invalid");

        verifyZeroInteractions(infoClient);
        verifyZeroInteractions(notifyClient);
    }

    /* Send No Team email when there is null NotifyType */
    @Test
    void ShouldNotSendTeamEmailNotifyTypeNull() {

        UUID teamUUID = UUID.randomUUID();

        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, teamUUID,null);

        verifyZeroInteractions(infoClient);
        verifyZeroInteractions(notifyClient);
    }
}