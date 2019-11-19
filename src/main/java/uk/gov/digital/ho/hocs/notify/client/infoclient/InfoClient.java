package uk.gov.digital.ho.hocs.notify.client.infoclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.hocs.notify.application.RestHelper;

import java.util.Set;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.notify.application.LogEvent.*;

@Slf4j
@Component
public class InfoClient {

    private final RestHelper restHelper;
    private final String serviceBaseURL;

    @Autowired
    public InfoClient(RestHelper restHelper,
                      @Value("${hocs.info-service}") String infoService) {
        this.restHelper = restHelper;
        this.serviceBaseURL = infoService;
    }

    public Set<NominatedContactDto> getNominatedPeople(UUID teamUUID) {
        Set<NominatedContactDto> response = restHelper.get(serviceBaseURL, String.format("/team/%s/contact", teamUUID), new ParameterizedTypeReference<Set<NominatedContactDto>>() {});
        log.info("Got {} contacts for Team {}", response.size(), value(EVENT, INFO_CLIENT_GET_CONTACTS_SUCCESS));
        return response;
    }

    public UserDto getUser(UUID userUUID) {
        UserDto userDto = restHelper.get(serviceBaseURL, String.format("/user/%s", userUUID), UserDto.class);
        log.info("Got User UserUUID {}", userUUID, value(EVENT, INFO_CLIENT_GET_USER_SUCCESS));
        return userDto;
    }
}