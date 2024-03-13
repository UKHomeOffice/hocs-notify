package uk.gov.digital.ho.hocs.notify.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.notify.api.NotifyService;
import uk.gov.digital.ho.hocs.notify.api.dto.Command;
import uk.gov.digital.ho.hocs.notify.domain.exception.EntityCreationException;

@Service
@Slf4j
public class NotifyDomain {

    private final NotifyService notifyService;

    @Autowired
    public NotifyDomain(NotifyService notifyService)
    {
        this.notifyService = notifyService;
    }

    public void executeCommand(Command command) {
        if(command != null) {
            command.execute(notifyService);
        } else {
            throw new EntityCreationException("Failed to Parse Json to valid Notify Command");
        }
    }

}
