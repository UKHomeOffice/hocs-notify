package uk.gov.digital.ho.hocs.notify.client.infoclient;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NominatedContactDto {

    @JsonProperty("uuid")
    private UUID uuid;

    @JsonProperty("teamUUID")
    private UUID teamUUID;

    @JsonProperty("emailAddress")
    private String emailAddress;

}
