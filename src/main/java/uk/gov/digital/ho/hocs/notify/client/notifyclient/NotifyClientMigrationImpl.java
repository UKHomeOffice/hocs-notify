package uk.gov.digital.ho.hocs.notify.client.notifyclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.notify.application.MigrationEnvCondition;
import uk.gov.digital.ho.hocs.notify.domain.NotifyType;

import java.util.Map;
import java.util.UUID;


@Service
@Slf4j
@Conditional(value = {MigrationEnvCondition.class})
public class NotifyClientMigrationImpl implements NotifyClient {


    @Autowired
    public NotifyClientMigrationImpl() {
        log.warn("NotifyClientMigrationImpl initialised");
    }

    @Override
    public void sendEmail(UUID caseUUID, UUID stageUUID, String emailAddress, Map<String, String> personalisation, NotifyType notifyType) {
        log.info("Suppressed sending notify due to migration profile, emailAddress {}, template ID {}", emailAddress, notifyType.getDisplayValue());
    }
}
