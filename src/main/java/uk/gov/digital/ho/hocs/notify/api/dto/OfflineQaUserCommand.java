package uk.gov.digital.ho.hocs.notify.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import uk.gov.digital.ho.hocs.notify.api.NotifyService;

import java.util.UUID;

import static uk.gov.digital.ho.hocs.notify.api.dto.OfflineQaUserCommand.OFFLINE_QA_USER_COMMAND;

@Getter
@JsonTypeName ( OFFLINE_QA_USER_COMMAND )
@JsonIgnoreProperties ( ignoreUnknown = true )
public class OfflineQaUserCommand extends NotifyCommand {

    static final String OFFLINE_QA_USER_COMMAND = "offline_qa_user";

    private String caseReference;

    private UUID currentUserUUID;

    private UUID offlineQaUserUUID;

    @JsonCreator
    public OfflineQaUserCommand(@JsonProperty ( "caseUUID" ) UUID caseUUID,
                                @JsonProperty ( "stageUUID" ) UUID stageUUID,
                                @JsonProperty ( "caseReference" ) String caseReference,
                                @JsonProperty ( "currentUserUUID" ) UUID currentUserUUID,
                                @JsonProperty ( "offlineQaUserUUID" ) UUID offlineQaUserUUID) {
        super(OFFLINE_QA_USER_COMMAND, UUID.randomUUID(), caseUUID, stageUUID);
        this.caseReference = caseReference;
        this.currentUserUUID = currentUserUUID;
        this.offlineQaUserUUID = offlineQaUserUUID;
    }

    @Override
    public void execute(NotifyService notifyService) {
        notifyService.sendOfflineQaUserEmail(super.caseUUID, super.stageUUID, caseReference, currentUserUUID, offlineQaUserUUID);
    }
}
