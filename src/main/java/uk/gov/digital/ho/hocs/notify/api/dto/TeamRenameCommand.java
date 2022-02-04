package uk.gov.digital.ho.hocs.notify.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NonNull;
import uk.gov.digital.ho.hocs.notify.api.NotifyService;

import java.util.UUID;

import static uk.gov.digital.ho.hocs.notify.api.dto.TeamRenameCommand.TEAM_RENAME_COMMAND;

@Getter
@JsonTypeName(TEAM_RENAME_COMMAND)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamRenameCommand extends NotifyCommand {

    static final String TEAM_RENAME_COMMAND = "team_rename";

    private UUID teamUUID;

    private String oldDisplayName;

    @JsonCreator
    public TeamRenameCommand(@JsonProperty ( "teamUUID" ) @NonNull UUID teamUUID,
                             @JsonProperty ( "oldDisplayName" ) @NonNull String oldDisplayName) {
        super(TEAM_RENAME_COMMAND, UUID.randomUUID(), null, null);
        this.teamUUID = teamUUID;
        this.oldDisplayName = oldDisplayName;
    }

    @Override
    public void execute(NotifyService notifyService) {
        notifyService.sendTeamRenameEmail(teamUUID, oldDisplayName);
    }
}
