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

import static uk.gov.digital.ho.hocs.notify.api.dto.TeamAssignChangeCommand.TEAM_ASSIGN_CHANGE_COMMAND;

@Getter
@JsonTypeName(TEAM_ASSIGN_CHANGE_COMMAND)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class TeamAssignChangeCommand extends NotifyCommand {

    static final String TEAM_ASSIGN_CHANGE_COMMAND = "team_assign_change";

    @SerializedName("caseReference")
    private String caseReference;

    @SerializedName("teamUUID")
    private UUID teamUUID;

    @SerializedName("allocationType")
    private String allocationType;

    @JsonCreator
    public TeamAssignChangeCommand(@JsonProperty("caseUUID") UUID caseUUID,
                                   @JsonProperty("stageUUID") UUID stageUUID,
                                   @JsonProperty("caseReference") String caseReference,
                                   @JsonProperty("teamUUID") UUID teamUUID,
                                   @JsonProperty("allocationType") String allocationType ) {
        super(TEAM_ASSIGN_CHANGE_COMMAND, UUID.randomUUID(), caseUUID, stageUUID);
        this.caseReference = caseReference;
        this.teamUUID = teamUUID;
        this.allocationType = allocationType;
    }

    @Override
    public void execute(NotifyService notifyService) {
        notifyService.sendTeamAssignChangeEmail(super.caseUUID, super.stageUUID, caseReference, teamUUID, allocationType);
    }
}
