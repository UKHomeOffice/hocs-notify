package uk.gov.digital.ho.hocs.notify.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.digital.ho.hocs.notify.application.RequestData;
import uk.gov.digital.ho.hocs.notify.client.infoclient.InfoClient;
import uk.gov.digital.ho.hocs.notify.client.infoclient.NominatedContactDto;
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
    void shouldAlwaysSendEmailUnAllocate() {

        UUID currentUserUUID = UUID.fromString("33333333-0000-0000-0000-000000000000");
        UUID newUserUUID = null;

        when(infoClient.getUser(currentUserUUID)).thenReturn(new UserDto("any", "name", "any", "notify"));

        notifyService.sendUserAssignChangeEmail(caseUUID, stageUUID, caseRef, currentUserUUID, newUserUUID);

        verify(infoClient).getUser(currentUserUUID);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "notify", "name", caseRef, NotifyType.UNALLOCATE_INDIVIDUAL);


        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
        verifyZeroInteractions(requestData);
    }

    /* Don't send an email if user A allocated an unallocated case to user A */
    @Test
    void shouldNotSendSelfEmailUnAllocated() {

        UUID currentUserUUID = null;
        UUID newUserUUID = UUID.fromString("11111111-0000-0000-0000-000000000000");

        when(requestData.userIdUUID()).thenReturn(UUID.fromString("11111111-0000-0000-0000-000000000000"));

        notifyService.sendUserAssignChangeEmail(caseUUID, stageUUID, caseRef, currentUserUUID, newUserUUID);

        verify(requestData).userIdUUID();

        verifyZeroInteractions(infoClient);
        verifyZeroInteractions(notifyClient);
        verifyNoMoreInteractions(requestData);
    }

    /* Send user A an email if user B allocated an unallocated case to user A */
    @Test
    void shouldSendOtherEmailUnAllocated() {

        UUID currentUserUUID = null;
        UUID newUserUUID = UUID.fromString("11111111-0000-0000-0000-000000000000");

        when(infoClient.getUser(newUserUUID)).thenReturn(new UserDto("any", "name", "any", "notify"));
        when(requestData.userIdUUID()).thenReturn(UUID.fromString("22222222-0000-0000-0000-000000000000"));

        notifyService.sendUserAssignChangeEmail(caseUUID, stageUUID, caseRef, currentUserUUID, newUserUUID);

        verify(requestData).userIdUUID();
        verify(infoClient).getUser(newUserUUID);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "notify", "name", caseRef, NotifyType.ALLOCATE_INDIVIDUAL);

        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
        verifyNoMoreInteractions(requestData);
    }

    /* Send email to Offline QA User */
    @Test
    void shouldSendOfflineQaEmailUnAllocated() {

        UUID offlineQaUserUUID = UUID.fromString("11111111-0000-0000-0000-000000000000");
        UUID currentUserUUID = UUID.fromString("22222222-0000-0000-0000-000000000000");

        final UserDto currentUser = new UserDto("any", "name", "any", "notify");
        when(infoClient.getUser(currentUserUUID)).thenReturn(currentUser);
        when(infoClient.getUser(offlineQaUserUUID)).thenReturn(new UserDto("other", "person", "than", "other"));

        notifyService.sendOfflineQaUserEmail(caseUUID, stageUUID, caseRef, currentUserUUID, offlineQaUserUUID);

        verify(infoClient).getUser(currentUserUUID);
        verify(infoClient).getUser(offlineQaUserUUID);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "other", currentUser.displayFormat(), caseRef, NotifyType.OFFLINE_QA_USER);

        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
    }

    /* No Sending of  email to Offline QA User */
    @Test
    void shouldNotSendOfflineQaEmailUnAllocated() {

        UUID currentUserUUID = null;
        UUID offlineQaUserUUID = UUID.fromString("11111111-0000-0000-0000-000000000000");

        notifyService.sendOfflineQaUserEmail(caseUUID, stageUUID, caseRef, currentUserUUID, offlineQaUserUUID);

        verifyZeroInteractions(infoClient);
        verifyZeroInteractions(notifyClient);
    }

    /* Send only user B an email is user A allocates user B's case to User A instead */
    @Test
    void shouldNotSendSelfEmailAllocated() {

        UUID currentUserUUID = UUID.fromString("33333333-0000-0000-0000-000000000000");
        UUID newUserUUID = UUID.fromString("11111111-0000-0000-0000-000000000000");

        when(infoClient.getUser(currentUserUUID)).thenReturn(new UserDto("any", "name", "any", "notify"));
        when(requestData.userIdUUID()).thenReturn(UUID.fromString("11111111-0000-0000-0000-000000000000"));

        notifyService.sendUserAssignChangeEmail(caseUUID, stageUUID, caseRef, currentUserUUID, newUserUUID);

        verify(requestData).userIdUUID();
        verify(infoClient).getUser(currentUserUUID);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "notify", "name", caseRef, NotifyType.UNALLOCATE_INDIVIDUAL);


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

        verify(requestData).userIdUUID();
        verify(infoClient).getUser(newUserUUID);
        verify(infoClient).getUser(currentUserUUID);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "notify", "name", caseRef, NotifyType.ALLOCATE_INDIVIDUAL);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "notify", "name", caseRef, NotifyType.UNALLOCATE_INDIVIDUAL);


        verifyNoMoreInteractions(infoClient);
        verifyZeroInteractions(notifyClient);
        verifyNoMoreInteractions(requestData);
    }

    /* Send Team email when there is a teamUUID */
    @Test
    void shouldSendTeamEmail() {

        UUID teamUUID = UUID.randomUUID();
        Set<NominatedContactDto> nominatedContactDtos = Set.of(new NominatedContactDto(UUID.randomUUID(), UUID.randomUUID(), "Someone"));

        when(infoClient.getNominatedPeople(teamUUID)).thenReturn(nominatedContactDtos);

        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, teamUUID, NotifyType.DISPATCH_REJECT.toString());

        verify(infoClient).getNominatedPeople(teamUUID);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "Someone", "Team", caseRef, NotifyType.DISPATCH_REJECT);

        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
    }

    /* Send Multiple Team email when there are more than one nominated people */
    @Test
    void shouldSendMultipleTeamEmail() {

        UUID teamUUID = UUID.randomUUID();
        Set<NominatedContactDto> nominatedContactDtos = Set.of(new NominatedContactDto(UUID.randomUUID(), UUID.randomUUID(), "Someone"),
                new NominatedContactDto(UUID.randomUUID(), UUID.randomUUID(), "Another"));


        when(infoClient.getNominatedPeople(teamUUID)).thenReturn(nominatedContactDtos);


        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, teamUUID, NotifyType.DISPATCH_REJECT.toString());

        verify(infoClient).getNominatedPeople(teamUUID);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "Someone", "Team", caseRef, NotifyType.DISPATCH_REJECT);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "Another", "Team", caseRef, NotifyType.DISPATCH_REJECT);

        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
    }

    /* Send Team email when there are no nominated people */
    @Test
    void shouldSendNoTeamEmail() {

        UUID teamUUID = UUID.randomUUID();

        when(infoClient.getNominatedPeople(teamUUID)).thenReturn(new HashSet<>(0));

        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, teamUUID, NotifyType.DISPATCH_REJECT.toString());

        verify(infoClient).getNominatedPeople(teamUUID);

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
    void shouldNotSendTeamEmailNotifyTypeInvalid() {

        UUID teamUUID = UUID.randomUUID();

        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, teamUUID, "invalid");

        verifyZeroInteractions(infoClient);
        verifyZeroInteractions(notifyClient);
    }

    /* Send No Team email when there is null NotifyType */
    @Test
    void shouldNotSendTeamEmailNotifyTypeNull() {

        UUID teamUUID = UUID.randomUUID();

        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, teamUUID, null);

        verifyZeroInteractions(infoClient);
        verifyZeroInteractions(notifyClient);
    }
}