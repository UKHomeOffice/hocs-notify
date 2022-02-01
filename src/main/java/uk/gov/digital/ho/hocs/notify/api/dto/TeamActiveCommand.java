package uk.gov.digital.ho.hocs.notify.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import uk.gov.digital.ho.hocs.notify.api.NotifyService;

import java.util.UUID;

@Getter
@JsonTypeName(TeamActiveCommand.TEAM_ACTIVE_COMMAND)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class TeamActiveCommand extends NotifyCommand {

    static final String TEAM_ACTIVE_COMMAND = "team_active";

    @SerializedName("teamUUID")
    private UUID teamUUID;

    @SerializedName("currentActiveStatus")
    private Boolean currentActiveStatus;

    @JsonCreator
    public TeamActiveCommand(@JsonProperty ( "teamUUID" ) @NonNull UUID teamUUID,
                             @JsonProperty ( "currentActiveStatus" ) @NonNull Boolean currentActiveStatus) {
        super(TEAM_ACTIVE_COMMAND, UUID.randomUUID(), null, null);
        this.teamUUID = teamUUID;
        this.currentActiveStatus = currentActiveStatus;
    }

    @Override
    public void execute(NotifyService notifyService) {
        notifyService.sendTeamActiveEmail(teamUUID, currentActiveStatus);
    }
}
