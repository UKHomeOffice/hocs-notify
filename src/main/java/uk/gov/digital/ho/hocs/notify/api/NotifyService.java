package uk.gov.digital.ho.hocs.notify.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.notify.application.RequestData;
import uk.gov.digital.ho.hocs.notify.client.infoclient.InfoClient;
import uk.gov.digital.ho.hocs.notify.client.infoclient.NominatedContactDto;
import uk.gov.digital.ho.hocs.notify.client.infoclient.UserDto;
import uk.gov.digital.ho.hocs.notify.client.notifyclient.NotifyClient;
import uk.gov.digital.ho.hocs.notify.domain.NotifyType;

import java.util.Set;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.notify.application.LogEvent.*;

@Service
@Slf4j
public class NotifyService {

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
                Set<NominatedContactDto> nominatedContactDtos = infoClient.getNominatedPeople(teamUUID);
                for (NominatedContactDto contact : nominatedContactDtos) {
                    notifyClient.sendEmail(caseUUID, stageUUID, contact.getEmailAddress(), "Team", caseReference, notifyType);
                }
            }
        } catch (Exception e) {
            log.warn("Notify failed to send Case: {} Stage: {} Team: {}", caseReference, stageUUID, teamUUID, value(EVENT, NOTIFY_EMAIL_FAILED), value(EXCEPTION, e));
        }
    }

    public void sendOfflineQaUserEmail(UUID caseUUID, UUID stageUUID, String caseReference, UUID currentUserUUID, UUID offlineQaUserUUID) {
        try {
            if (currentUserUUID != null) {
                final UserDto offlineQaUser = infoClient.getUser(offlineQaUserUUID);
                final UserDto currentUser = infoClient.getUser(currentUserUUID);
                if (offlineQaUser != null && currentUser != null) {
                    notifyClient.sendEmail(caseUUID, stageUUID, offlineQaUser.getEmail(), currentUser.displayFormat(), caseReference, NotifyType.OFFLINE_QA_USER);
                }
            }
        } catch (Exception e) {
            log.warn("Notify failed to send Case: {} Stage: {} CurrentUser: {} OfflineQaUser: {}", caseReference, stageUUID, currentUserUUID, offlineQaUserUUID, value(EVENT, NOTIFY_EMAIL_FAILED), value(EXCEPTION, e));
        }
    }

    public void sendUserAssignChangeEmail(UUID caseUUID, UUID stageUUID, String caseReference, UUID currentUserUUID, UUID newUserUUID) {
        try {
            if (newUserUUID != null) {
                if (currentUserUUID != null && !newUserUUID.equals(currentUserUUID)) {
                    sendUnAllocateUserEmail(caseUUID, stageUUID, currentUserUUID, caseReference);
                    if(!newUserUUID.equals(requestData.userIdUUID())) {
                        sendAllocateUserEmail(caseUUID, stageUUID, newUserUUID, caseReference);
                    }
                } else {
                    if(!newUserUUID.equals(requestData.userIdUUID())) {
                        sendAllocateUserEmail(caseUUID, stageUUID, newUserUUID, caseReference);
                    }
                }
            } else if (currentUserUUID != null) {
                sendUnAllocateUserEmail(caseUUID, stageUUID, currentUserUUID, caseReference);
            }
        } catch (Exception e) {
            log.warn("Email failed to send Case: {} Stage: {} CurrentUser: {} NewUser:{}", caseReference, stageUUID, currentUserUUID, newUserUUID, value(EVENT, NOTIFY_EMAIL_FAILED), value(EXCEPTION, e.toString()));
            log.warn(e.toString());
        }
    }

    private void sendAllocateUserEmail(UUID caseUUID, UUID stageUUID, UUID userUUID, String caseReference) {
        UserDto user = infoClient.getUser(userUUID);
        notifyClient.sendEmail(caseUUID, stageUUID, user.getEmail(), user.getFirstName(), caseReference, NotifyType.ALLOCATE_INDIVIDUAL);
    }

    private void sendUnAllocateUserEmail(UUID caseUUID, UUID stageUUID, UUID userUUID, String caseReference) {
        UserDto user = infoClient.getUser(userUUID);
        notifyClient.sendEmail(caseUUID, stageUUID, user.getEmail(), user.getFirstName(), caseReference, NotifyType.UNALLOCATE_INDIVIDUAL);
    }
}
