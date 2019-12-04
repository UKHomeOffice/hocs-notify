package uk.gov.digital.ho.hocs.notify.client.notifyclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.notify.application.NonMigrationEnvCondition;
import uk.gov.digital.ho.hocs.notify.domain.NotifyType;
import uk.gov.service.notify.NotificationClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.notify.application.LogEvent.*;

@Service
@Slf4j
@Conditional(value = {NonMigrationEnvCondition.class})
public class NotifyClientImpl implements NotifyClient {

    private final NotificationClient notificationClient;
    private final String url;

    @Autowired
    public NotifyClientImpl(@Value("${notify.apiKey}") String apiKey,
                            @Value("${hocs.url}") String url) {
        this.notificationClient = new NotificationClient(apiKey);
        this.url = url;
    }

    @Override
    public void sendEmail(UUID caseUUID, UUID stageUUID, String emailAddress, Map<String, String> personalisation, NotifyType notifyType) {
        String link = String.format("%s/case/%s/stage/%s", url, caseUUID, stageUUID);
        Map<String, String> personalisationToSend = new HashMap<>(personalisation);
        personalisationToSend.put("link", link);

        sendEmail(notifyType, emailAddress, personalisationToSend);
    }

    private void sendEmail(NotifyType notifyType, String emailAddress, Map<String, String> personalisation) {
        log.info("Sending notify to {}, template ID {}", emailAddress, notifyType.getDisplayValue());

        try {
            notificationClient.sendEmail(notifyType.getDisplayValue(), emailAddress, personalisation, null);
        } catch (Exception e) {
            log.warn("Didn't send Email to {}, event {}, exception {}", emailAddress, value(EVENT, NOTIFY_EMAIL_FAILED), value(EXCEPTION, e));
        }
    }
}
