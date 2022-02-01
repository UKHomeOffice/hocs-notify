package uk.gov.digital.ho.hocs.notify.aws.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.notify.api.dto.NotifyCommand;
import uk.gov.digital.ho.hocs.notify.domain.NotifyDomain;

@Service
public class NotifyListener {

    private final ObjectMapper objectMapper;
    private final NotifyDomain notifyDomain;

    public NotifyListener(ObjectMapper objectMapper,
                          NotifyDomain notifyDomain) {
        this.objectMapper = objectMapper;
        this.notifyDomain = notifyDomain;
    }

    @SqsListener(value = "${aws.sqs.notify.url}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void onNotifyEvent(String message) throws JsonProcessingException {

        NotifyCommand command = objectMapper.readValue(message, NotifyCommand.class);
        notifyDomain.executeCommand(command);
    }

}
