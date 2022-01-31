package uk.gov.digital.ho.hocs.notify.aws.listeners;

import com.google.gson.Gson;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.notify.api.dto.NotifyCommand;
import uk.gov.digital.ho.hocs.notify.domain.NotifyDomain;

@Service
public class NotifyListener {

    private final Gson gson;
    private final NotifyDomain notifyDomain;

    public NotifyListener(Gson gson,
                          NotifyDomain notifyDomain) {
        this.gson = gson;
        this.notifyDomain = notifyDomain;
    }

    @SqsListener(value = "${aws.sqs.notify.url}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void onNotifyEvent(String message) {
        NotifyCommand command = gson.fromJson(message, NotifyCommand.class);
        notifyDomain.executeCommand(command);
    }

}
