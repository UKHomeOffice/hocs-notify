package uk.gov.digital.ho.hocs.notify.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;
@EqualsAndHashCode(of="uuid")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "command"
)
@JsonSubTypes({ // Keep this list alphabetical
        @JsonSubTypes.Type(value = OfflineQaUserCommand.class),
        @JsonSubTypes.Type(value = TeamActiveCommand.class),
        @JsonSubTypes.Type(value = TeamAssignChangeCommand.class),
        @JsonSubTypes.Type(value = TeamRenameCommand.class),
        @JsonSubTypes.Type(value = UserAssignChangeCommand.class)
})
@Getter
public abstract class NotifyCommand implements Command {

    protected String command;

    protected UUID caseUUID;

    protected UUID stageUUID;

    protected UUID uuid;

    @JsonCreator
    public NotifyCommand(@JsonProperty("command") String command, UUID uuid, UUID caseUUID, UUID stageUUID) {
        this.uuid = uuid;
        this.command = command;
        this.caseUUID = caseUUID;
        this.stageUUID = stageUUID;
    }

}
