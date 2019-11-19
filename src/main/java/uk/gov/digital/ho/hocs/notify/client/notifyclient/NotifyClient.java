package uk.gov.digital.ho.hocs.notify.client.notifyclient;

import uk.gov.digital.ho.hocs.notify.domain.NotifyType;

import java.util.Map;
import java.util.UUID;


public interface NotifyClient {

    void sendEmail(UUID caseUUID, UUID stageUUID, String emailAddress, Map<String, String> personalisation, NotifyType notifyType);

}
