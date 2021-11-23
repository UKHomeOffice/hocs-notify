package uk.gov.digital.ho.hocs.notify.client.infoclient;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import uk.gov.digital.ho.hocs.notify.application.RestHelper;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InfoClientTest {

    @Mock
    private RestHelper restHelper;

    private static final String URL = "http://localhost:8085";

    private InfoClient infoClient;


    @Before
    public void before() {

        infoClient = new InfoClient(restHelper, URL);

    }

    @Test
    public void getUser() {

        UUID userId = UUID.randomUUID();
        String expectedUrl = "/user/" + userId.toString();
        UserDto userDto = new UserDto("tom123", "Tom", "Smith", "ts@dummy.com");

        when(restHelper.get(URL, expectedUrl, UserDto.class)).thenReturn(userDto);

        UserDto result = infoClient.getUser(userId);

        verify(restHelper).get(URL, expectedUrl, UserDto.class);
        Assertions.assertThat(result).isEqualTo(userDto);
        verifyNoMoreInteractions(restHelper);

    }

    @Test
    public void getTeam() {

        UUID teamId = UUID.randomUUID();
        String expectedUrl = "/team/" + teamId.toString();
        TeamDto teamDto = new TeamDto("Team NAme", "T", teamId, true);

        when(restHelper.get(URL, expectedUrl, TeamDto.class)).thenReturn(teamDto);

        TeamDto result = infoClient.getTeam(teamId);

        verify(restHelper).get(URL, expectedUrl, TeamDto.class);
        Assertions.assertThat(result).isEqualTo(teamDto);
        verifyNoMoreInteractions(restHelper);

    }

    @Test
    public void getNominatedContacts() {

        UUID teamId = UUID.randomUUID();
        String expectedUrl = "/team/" + teamId.toString() + "/contact";

        Set<NominatedContactDto> nominatedContactDtos = Set.of(new NominatedContactDto(UUID.randomUUID(), UUID.randomUUID(), "Someone"));

        when(restHelper.get(URL, expectedUrl, new ParameterizedTypeReference<Set<NominatedContactDto>>() {
        })).thenReturn(nominatedContactDtos);

        Set<NominatedContactDto> result = infoClient.getNominatedContacts(teamId);

        verify(restHelper).get(URL, expectedUrl, new ParameterizedTypeReference<Set<NominatedContactDto>>() {
        });
        Assertions.assertThat(result).isEqualTo(nominatedContactDtos);
        verifyNoMoreInteractions(restHelper);

    }
}
