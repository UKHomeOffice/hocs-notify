package uk.gov.digital.ho.hocs.notify.api.dto;

import uk.gov.digital.ho.hocs.notify.api.NotifyService;

public interface Command {

    void execute(NotifyService notifyService);
}
