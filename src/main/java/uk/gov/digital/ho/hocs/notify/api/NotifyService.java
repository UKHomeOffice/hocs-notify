package uk.gov.digital.ho.hocs.notify.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import uk.gov.digital.ho.hocs.notify.application.RequestData;
import uk.gov.digital.ho.hocs.notify.client.infoclient.InfoClient;
import uk.gov.digital.ho.hocs.notify.client.infoclient.NominatedContactDto;
import uk.gov.digital.ho.hocs.notify.client.infoclient.TeamDto;
import uk.gov.digital.ho.hocs.notify.client.infoclient.UserDto;
import uk.gov.digital.ho.hocs.notify.client.notifyclient.NotifyClient;
import uk.gov.digital.ho.hocs.notify.domain.NotifyType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.notify.application.LogEvent.*;

@Service
@Slf4j
public class NotifyService {

    public static final String CASE_REF = "caseRef";
    public static final String TEAM = "team";
    private final InfoClient infoClient;
    private final RequestData requestData;
    private final NotifyClient notifyClient;

    @Autowired
    public NotifyService(InfoClient infoClient,
                         RequestData requestData,
                         NotifyClient notifyClient) {
        this.infoClient = infoClient;
        this.requestData = requestData;
        this.notifyClient = notifyClient;
    }

    public void sendTeamAssignChangeEmail(UUID caseUUID, UUID stageUUID, String caseReference, UUID teamUUID, String allocationType) {
        try {
            if (teamUUID != null) {
                NotifyType notifyType = NotifyType.valueOf(allocationType);
                Set<NominatedContactDto> nominatedContactDtos = infoClient.getNominatedContacts(teamUUID);
                if (!CollectionUtils.isEmpty(nominatedContactDtos)) {
                    TeamDto team = infoClient.getTeam(teamUUID);
                    for (NominatedContactDto contact : nominatedContactDtos) {
                        Map<String, String> personalisation = new HashMap<>();
                        personalisation.put(CASE_REF, caseReference);
                        personalisation.put(TEAM, team.getDisplayName());
                        notifyClient.sendEmail(caseUUID, stageUUID, contact.getEmailAddress(), personalisation, notifyType);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Notify failed to send Case: {} Stage: {} Team: {}, event: {}, error: {}", caseReference, stageUUID, teamUUID, value(EVENT, NOTIFY_EMAIL_FAILED), value(EXCEPTION, e));
        }
    }

    public void sendOfflineQaUserEmail(UUID caseUUID, UUID stageUUID, String caseReference, UUID currentUserUUID, UUID offlineQaUserUUID) {
        try {
            if (currentUserUUID != null) {
                final UserDto offlineQaUser = infoClient.getUser(offlineQaUserUUID);
                final UserDto currentUser = infoClient.getUser(currentUserUUID);
                if (offlineQaUser != null && currentUser != null) {
                    Map<String, String> personalisation = new HashMap<>();
                    personalisation.put(CASE_REF, caseReference);
                    personalisation.put("user", currentUser.displayFormat());
                    notifyClient.sendEmail(caseUUID, stageUUID, offlineQaUser.getEmail(), personalisation, NotifyType.OFFLINE_QA_USER);
                }
            }
        } catch (Exception e) {
            log.warn("Notify failed to send Case: {} Stage: {} CurrentUser: {} OfflineQaUser: {}, event: {}, error: {}", caseReference, stageUUID, currentUserUUID, offlineQaUserUUID, value(EVENT, NOTIFY_EMAIL_FAILED), value(EXCEPTION, e));
        }
    }

    public void sendUserAssignChangeEmail(UUID caseUUID, UUID stageUUID, String caseReference, UUID currentUserUUID, UUID newUserUUID) {
        try {
            if (newUserUUID != null) {
                if (currentUserUUID != null && !newUserUUID.equals(currentUserUUID)) {
                    sendUnAllocateUserEmail(caseUUID, stageUUID, currentUserUUID, caseReference);
                    if (!newUserUUID.equals(requestData.userIdUUID())) {
                        sendAllocateUserEmail(caseUUID, stageUUID, newUserUUID, caseReference);
                    }
                } else {
                    if (!newUserUUID.equals(requestData.userIdUUID())) {
                        sendAllocateUserEmail(caseUUID, stageUUID, newUserUUID, caseReference);
                    }
                }
            } else if (currentUserUUID != null) {
                sendUnAllocateUserEmail(caseUUID, stageUUID, currentUserUUID, caseReference);
            }
        } catch (Exception e) {
            log.warn("Email failed to send Case: {} Stage: {} CurrentUser: {} NewUser:{}, event: {}, error: {}", caseReference, stageUUID, currentUserUUID, newUserUUID, value(EVENT, NOTIFY_EMAIL_FAILED), value(EXCEPTION, e.toString()));
            log.warn(e.toString());
        }
    }

    private void sendAllocateUserEmail(UUID caseUUID, UUID stageUUID, UUID userUUID, String caseReference) {
        UserDto user = infoClient.getUser(userUUID);
        Map<String, String> personalisation = new HashMap<>();
        personalisation.put(CASE_REF, caseReference);
        personalisation.put("user", user.getFirstName());
        notifyClient.sendEmail(caseUUID, stageUUID, user.getEmail(), personalisation, NotifyType.ALLOCATE_INDIVIDUAL);
    }

    private void sendUnAllocateUserEmail(UUID caseUUID, UUID stageUUID, UUID userUUID, String caseReference) {
        UserDto user = infoClient.getUser(userUUID);
        Map<String, String> personalisation = new HashMap<>();
        personalisation.put(CASE_REF, caseReference);
        personalisation.put("user", user.getFirstName());
        notifyClient.sendEmail(caseUUID, stageUUID, user.getEmail(), personalisation, NotifyType.UNALLOCATE_INDIVIDUAL);
    }

    public void sendTeamRenameEmail(UUID teamUUID, String oldDisplayName) {
        if (teamUUID != null) {
            NotifyType notifyType = NotifyType.TEAM_RENAME;
            Set<NominatedContactDto> nominatedContactDtos = infoClient.getNominatedContacts(teamUUID);
            if (!CollectionUtils.isEmpty(nominatedContactDtos)) {
                TeamDto team = infoClient.getTeam(teamUUID);
                for (NominatedContactDto contact : nominatedContactDtos) {
                    Map<String, String> personalisation =
                            Map.of("oldTeamDisplayName", oldDisplayName,
                                    "newTeamDisplayName", team.getDisplayName());
                    notifyClient.sendEmail(contact.getEmailAddress(), personalisation, notifyType);
                }
            }
        }
    }

    public void sendTeamActiveEmail(UUID teamUUID, Boolean currentActiveStatus) {

        Assert.notNull(teamUUID, "teamUUID parameter should not be null");
        Assert.notNull(currentActiveStatus, "currentActiveStatus parameter should not be null");

        Set<NominatedContactDto> nominatedContacts = infoClient.getNominatedContacts(teamUUID);
        if (nominatedContacts.isEmpty()) {
            return;
        }

        TeamDto team = infoClient.getTeam(teamUUID);

        Map<String, String> personalisation =
                Map.of("teamName", team.getDisplayName(),
                        "activeDisplayStatus", convertActiveToDisplayString(currentActiveStatus));

        nominatedContacts.forEach(nominatedContactDto ->
            notifyClient.sendEmail(nominatedContactDto.getEmailAddress(), personalisation, NotifyType.TEAM_ACTIVE)
        );

    }

    private String convertActiveToDisplayString(boolean aBoolean) {
        return aBoolean ? "Active" : "Inactive";
    }
}
