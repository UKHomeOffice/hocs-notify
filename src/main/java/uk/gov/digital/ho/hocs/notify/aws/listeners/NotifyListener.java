package uk.gov.digital.ho.hocs.notify.aws.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.notify.api.dto.NotifyCommand;
import uk.gov.digital.ho.hocs.notify.application.RequestData;
import uk.gov.digital.ho.hocs.notify.domain.NotifyDomain;

import java.util.Map;

@Service
public class NotifyListener {

    private final ObjectMapper objectMapper;
    private final NotifyDomain notifyDomain;
    private final RequestData requestData;

    public NotifyListener(ObjectMapper objectMapper,
                          NotifyDomain notifyDomain,
                          RequestData requestData) {
        this.objectMapper = objectMapper;
        this.notifyDomain = notifyDomain;
        this.requestData = requestData;
    }

    @SqsListener(value="${aws.sqs.notify.url}")
    public void onNotifyEvent(
            String message,
            @Headers Map<String,String> headers
    ) throws JsonProcessingException {
        try {
            requestData.parseMessageHeaders(headers);
            NotifyCommand command = objectMapper.readValue(message, NotifyCommand.class);
            notifyDomain.executeCommand(command);
        } finally {
            requestData.clear();
        }
    }

}
