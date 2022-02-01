package uk.gov.digital.ho.hocs.notify.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.hocs.notify.api.NotifyService;

import java.util.UUID;

import static uk.gov.digital.ho.hocs.notify.api.dto.UserAssignChangeCommand.USER_ASSIGN_CHANGE_COMMAND;

@Getter
@JsonTypeName(USER_ASSIGN_CHANGE_COMMAND)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class UserAssignChangeCommand extends NotifyCommand {

    static final String USER_ASSIGN_CHANGE_COMMAND = "user_assign_change";

    @SerializedName("caseReference")
    private String caseReference;

    @SerializedName("currentUserUUID")
    private UUID currentUserUUID;

    @SerializedName("newUserUUID")
    private UUID newUserUUID;

    @JsonCreator
    public UserAssignChangeCommand(@JsonProperty("caseUUID") UUID caseUUID,
                                   @JsonProperty("stageUUID") UUID stageUUID,
                                   @JsonProperty("caseReference") String caseReference,
                                   @JsonProperty("currentUserUUID") UUID currentUserUUID,
                                   @JsonProperty("newUserUUID") UUID newUserUUID) {
        super(USER_ASSIGN_CHANGE_COMMAND, UUID.randomUUID(), caseUUID, stageUUID);
        this.caseReference = caseReference;
        this.currentUserUUID = currentUserUUID;
        this.newUserUUID = newUserUUID;
    }

    @Override
    public void execute(NotifyService notifyService) {
        notifyService.sendUserAssignChangeEmail(super.caseUUID, super.stageUUID, caseReference, currentUserUUID, newUserUUID);
    }
}
