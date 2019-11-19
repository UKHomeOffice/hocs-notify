package uk.gov.digital.ho.hocs.notify.client.infoclient;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor()
@NoArgsConstructor()
@EqualsAndHashCode
@Getter
public class TeamDto {

    @JsonProperty("displayName")
    private String displayName;


    @JsonProperty("letterName")
    private String letterName;

    @JsonProperty("type")
    private UUID uuid;

    @JsonProperty("active")
    private boolean active;

}

