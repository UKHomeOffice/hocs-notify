package uk.gov.digital.ho.hocs.notify.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

import uk.gov.digital.ho.hocs.notify.application.RequestData;
import uk.gov.digital.ho.hocs.notify.client.infoclient.InfoClient;
import uk.gov.digital.ho.hocs.notify.client.infoclient.NominatedContactDto;
import uk.gov.digital.ho.hocs.notify.client.infoclient.TeamDto;
import uk.gov.digital.ho.hocs.notify.client.infoclient.UserDto;
import uk.gov.digital.ho.hocs.notify.client.notifyclient.NotifyClient;
import uk.gov.digital.ho.hocs.notify.domain.NotifyType;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("caseRef", caseRef);
        personalisation.put("user", "name");

        when(infoClient.getUser(currentUserUUID)).thenReturn(new UserDto("any", "name", "any", "notify"));

        notifyService.sendUserAssignChangeEmail(caseUUID, stageUUID, caseRef, currentUserUUID, newUserUUID);

        verify(infoClient).getUser(currentUserUUID);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "notify", personalisation, NotifyType.UNALLOCATE_INDIVIDUAL);


        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
        verifyNoInteractions(requestData);
    }

    /* Don't send an email if user A allocated an unallocated case to user A */
    @Test
    void shouldNotSendSelfEmailUnAllocated() {

        UUID currentUserUUID = null;
        UUID newUserUUID = UUID.fromString("11111111-0000-0000-0000-000000000000");

        when(requestData.userId()).thenReturn(newUserUUID.toString());

        notifyService.sendUserAssignChangeEmail(caseUUID, stageUUID, caseRef, currentUserUUID, newUserUUID);

        verify(requestData).userId();

        verifyNoInteractions(infoClient);
        verifyNoInteractions(notifyClient);
        verifyNoMoreInteractions(requestData);
    }

    /* Send user A an email if user B allocated an unallocated case to user A */
    @Test
    void shouldSendOtherEmailUnAllocated() {

        UUID currentUserUUID = null;
        UUID newUserUUID = UUID.fromString("11111111-0000-0000-0000-000000000000");

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("caseRef", caseRef);
        personalisation.put("user", "name");

        when(infoClient.getUser(newUserUUID)).thenReturn(new UserDto("any", "name", "any", "notify"));
        when(requestData.userId()).thenReturn("22222222-0000-0000-0000-000000000000");

        notifyService.sendUserAssignChangeEmail(caseUUID, stageUUID, caseRef, currentUserUUID, newUserUUID);

        verify(requestData).userId();
        verify(infoClient).getUser(newUserUUID);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "notify", personalisation, NotifyType.ALLOCATE_INDIVIDUAL);

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

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("caseRef", caseRef);
        personalisation.put("user", currentUser.displayFormat());

        notifyService.sendOfflineQaUserEmail(caseUUID, stageUUID, caseRef, currentUserUUID, offlineQaUserUUID);

        verify(infoClient).getUser(currentUserUUID);
        verify(infoClient).getUser(offlineQaUserUUID);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "other", personalisation, NotifyType.OFFLINE_QA_USER);

        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
    }

    /* No Sending of  email to Offline QA User */
    @Test
    void shouldNotSendOfflineQaEmailUnAllocated() {

        UUID currentUserUUID = null;
        UUID offlineQaUserUUID = UUID.fromString("11111111-0000-0000-0000-000000000000");

        notifyService.sendOfflineQaUserEmail(caseUUID, stageUUID, caseRef, currentUserUUID, offlineQaUserUUID);

        verifyNoInteractions(infoClient);
        verifyNoInteractions(notifyClient);
    }

    /* Send only user B an email is user A allocates user B's case to User A instead */
    @Test
    void shouldNotSendSelfEmailAllocated() {

        UUID currentUserUUID = UUID.fromString("33333333-0000-0000-0000-000000000000");
        UUID newUserUUID = UUID.fromString("11111111-0000-0000-0000-000000000000");

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("caseRef", caseRef);
        personalisation.put("user", "name");

        when(infoClient.getUser(currentUserUUID)).thenReturn(new UserDto("any", "name", "any", "notify"));
        when(requestData.userId()).thenReturn(newUserUUID.toString());

        notifyService.sendUserAssignChangeEmail(caseUUID, stageUUID, caseRef, currentUserUUID, newUserUUID);

        verify(requestData).userId();
        verify(infoClient).getUser(currentUserUUID);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "notify", personalisation, NotifyType.UNALLOCATE_INDIVIDUAL);


        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
        verifyNoMoreInteractions(requestData);
    }

    /* Send an email to user A and user B if I User C allocates a case from user A to user B */
    @Test
    void ShouldSendOtherEmailAllocated() {

        UUID currentUserUUID = UUID.fromString("33333333-0000-0000-0000-000000000000");
        UUID newUserUUID = UUID.fromString("11111111-0000-0000-0000-000000000000");

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("caseRef", caseRef);
        personalisation.put("user", "name");

        when(infoClient.getUser(currentUserUUID)).thenReturn(new UserDto("any", "name", "any", "notify"));
        when(infoClient.getUser(newUserUUID)).thenReturn(new UserDto("any", "name", "any", "notify"));
        when(requestData.userId()).thenReturn("22222222-0000-0000-0000-000000000000");

        notifyService.sendUserAssignChangeEmail(caseUUID, stageUUID, caseRef, currentUserUUID, newUserUUID);

        verify(requestData).userId();
        verify(infoClient).getUser(newUserUUID);
        verify(infoClient).getUser(currentUserUUID);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "notify", personalisation, NotifyType.ALLOCATE_INDIVIDUAL);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "notify", personalisation, NotifyType.UNALLOCATE_INDIVIDUAL);


        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
        verifyNoMoreInteractions(requestData);
    }

    /* Send Team email when there is a teamUUID */
    @Test
    void shouldSendTeamEmail() {

        UUID teamUUID = UUID.randomUUID();
        Set<NominatedContactDto> nominatedContactDtos = Set.of(new NominatedContactDto(UUID.randomUUID(), UUID.randomUUID(), "Someone"));
        String teamName = "Team Name";
        TeamDto teamDto = new TeamDto(teamName, "a", UUID.randomUUID(), false);
        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("caseRef", caseRef);
        personalisation.put("team", teamName);

        when(infoClient.getNominatedContacts(teamUUID)).thenReturn(nominatedContactDtos);
        when(infoClient.getTeam(teamUUID)).thenReturn(teamDto);

        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, teamUUID, NotifyType.DISPATCH_REJECT.toString());

        verify(infoClient).getNominatedContacts(teamUUID);
        verify(infoClient).getTeam(teamUUID);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "Someone", personalisation, NotifyType.DISPATCH_REJECT);

        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
    }

    /* Send Multiple Team email when there are more than one nominated people */
    @Test
    void shouldSendMultipleTeamEmail() {

        UUID teamUUID = UUID.randomUUID();
        String teamName = "Team Name";
        TeamDto teamDto = new TeamDto(teamName, "a", UUID.randomUUID(), false);

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("caseRef", caseRef);
        personalisation.put("team", teamName);

        Set<NominatedContactDto> nominatedContactDtos = Set.of(new NominatedContactDto(UUID.randomUUID(), UUID.randomUUID(), "Someone"),
                new NominatedContactDto(UUID.randomUUID(), UUID.randomUUID(), "Another"));


        when(infoClient.getNominatedContacts(teamUUID)).thenReturn(nominatedContactDtos);
        when(infoClient.getTeam(teamUUID)).thenReturn(teamDto);

        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, teamUUID, NotifyType.DISPATCH_REJECT.toString());

        verify(infoClient).getNominatedContacts(teamUUID);
        verify(infoClient).getTeam(teamUUID);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "Someone", personalisation, NotifyType.DISPATCH_REJECT);
        verify(notifyClient).sendEmail(caseUUID, stageUUID, "Another", personalisation, NotifyType.DISPATCH_REJECT);

        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
    }

    /* Send Team email when there are no nominated people */
    @Test
    void shouldSendNoTeamEmail() {

        UUID teamUUID = UUID.randomUUID();

        when(infoClient.getNominatedContacts(teamUUID)).thenReturn(new HashSet<>(0));

        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, teamUUID, NotifyType.DISPATCH_REJECT.toString());

        verify(infoClient).getNominatedContacts(teamUUID);

        verifyNoMoreInteractions(infoClient);
        verifyNoInteractions(notifyClient);
    }

    /* Send No Team email when there is no teamUUID */
    @Test
    void ShouldNotSendTeamEmail() {

        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, null, NotifyType.DISPATCH_REJECT.toString());

        verifyNoInteractions(infoClient);
        verifyNoInteractions(notifyClient);
    }

    /* Send No Team email when there is an invalid NotifyType */
    @Test
    void shouldNotSendTeamEmailNotifyTypeInvalid() {

        UUID teamUUID = UUID.randomUUID();

        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, teamUUID, "invalid");

        verifyNoInteractions(infoClient);
        verifyNoInteractions(notifyClient);
    }

    /* Send No Team email when there is null NotifyType */
    @Test
    void shouldNotSendTeamEmailNotifyTypeNull() {

        UUID teamUUID = UUID.randomUUID();

        notifyService.sendTeamAssignChangeEmail(caseUUID, stageUUID, caseRef, teamUUID, null);

        verifyNoInteractions(infoClient);
        verifyNoInteractions(notifyClient);
    }

    @Test
    void shouldSendTeamRenameEmail() {
        NominatedContactDto nominatedContactDto =
                new NominatedContactDto(UUID.randomUUID(), UUID.randomUUID(), "test@example.com");
        TeamDto teamDto =
                new TeamDto("TEST_NAME", "", UUID.randomUUID(), true);
        UUID teamUuid = UUID.randomUUID();
        String oldTeamName = "TEST";

        when(infoClient.getNominatedContacts(teamUuid))
                .thenReturn(Collections.singleton(nominatedContactDto));
        when(infoClient.getTeam(teamUuid)).thenReturn(teamDto);

        Map<String, String> personalisation = Map.of(
                "oldTeamDisplayName", oldTeamName,
                "newTeamDisplayName", teamDto.getDisplayName());

        notifyService.sendTeamRenameEmail(teamUuid, oldTeamName);

        verify(infoClient).getNominatedContacts(teamUuid);
        verify(infoClient).getTeam(teamUuid);
        verify(notifyClient).sendEmail("test@example.com", personalisation, NotifyType.TEAM_RENAME);

        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
    }

    @Test
    void shouldSendTeamActiveEmail() {
        sendActiveEmail(true);
    }

    @Test
    void shouldSendTeamInActiveEmail() {
        sendActiveEmail(false);
    }


    private void sendActiveEmail(boolean activeStatus) {

        // given
        Set<NominatedContactDto> nominatedContactDtos = Set.of(new NominatedContactDto(UUID.randomUUID(), UUID.randomUUID(), "test@example.com"),
                new NominatedContactDto(UUID.randomUUID(), UUID.randomUUID(), "test2@example.com"));

        TeamDto teamDto =
                new TeamDto("TEST_NAME", "", UUID.randomUUID(), true);
        UUID teamUuid = UUID.randomUUID();

        when(infoClient.getNominatedContacts(teamUuid))
                .thenReturn(nominatedContactDtos);
        when(infoClient.getTeam(teamUuid)).thenReturn(teamDto);

        // when
        notifyService.sendTeamActiveEmail(teamUuid, activeStatus);

        // then
        verify(infoClient).getNominatedContacts(teamUuid);
        verify(infoClient).getTeam(teamUuid);
        ArgumentCaptor<Map<String, String>> templateFieldCapture = ArgumentCaptor.forClass(Map.class);

        // first invocation
        verify(notifyClient).sendEmail(eq("test@example.com" ), templateFieldCapture.capture(), eq(NotifyType.TEAM_ACTIVE));
        Map<String, String> templateFieldsCalled = templateFieldCapture.getValue();
        assertThat(templateFieldsCalled).containsAllEntriesOf(Map.of(
                "teamName", "TEST_NAME",
                "activeStatus", activeStatus ? "active" : "inactive",
                "availableStatus", activeStatus ? "available" : "unavailable"));

        // second invocation
        verify(notifyClient).sendEmail(eq("test2@example.com" ), any(), eq(NotifyType.TEAM_ACTIVE));

        verifyNoMoreInteractions(infoClient);
        verifyNoMoreInteractions(notifyClient);
    }

    @Test
    void shouldNotSendEmail_whenNoNominatedContacts() {
        UUID teamUuid = UUID.randomUUID();
        String oldTeamName = "TEST";

        when(infoClient.getNominatedContacts(teamUuid))
                .thenReturn(Collections.emptySet());

        notifyService.sendTeamRenameEmail(teamUuid, oldTeamName);

        verify(infoClient).getNominatedContacts(teamUuid);

        verifyNoMoreInteractions(infoClient);
        verifyNoInteractions(notifyClient);
    }

    @Test
    void shouldNotSendTeamRenameEmail_whenNoTeamUuid() {
        String oldTeamName = "TEST";

        notifyService.sendTeamRenameEmail(null, oldTeamName);

        verifyNoInteractions(infoClient);
        verifyNoInteractions(notifyClient);
    }
}
