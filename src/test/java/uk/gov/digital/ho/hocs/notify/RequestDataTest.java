package uk.gov.digital.ho.hocs.notify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.digital.ho.hocs.notify.application.RequestData;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestDataTest
{
    @Mock
    private HttpServletRequest mockHttpServletRequest;
    @Mock
    private HttpServletResponse mockHttpServletResponse;
    @Mock
    private Object mockHandler;

    private RequestData requestData = new RequestData();

    @AfterEach
    public void after() {
        requestData.clear();
    }

    @Test
    public void shouldDefaultRequestData() {
        requestData.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler);

        assertThat(requestData.correlationId()).isNotNull();
    }

    @Test
    public void shouldUseCorrelationIdFromRequest() {
        when(mockHttpServletRequest.getHeader("X-Correlation-Id")).thenReturn("some correlation id");

        requestData.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler);

        assertThat(requestData.correlationId()).isEqualTo("some correlation id");
    }

    @Test
    public void shouldUseUserIdFromRequest() {
        when(mockHttpServletRequest.getHeader("X-Correlation-Id")).thenReturn("some correlation id");
        UUID userUUID = UUID.randomUUID();
        when(mockHttpServletRequest.getHeader("X-Auth-UserId")).thenReturn(userUUID.toString());

        requestData.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler);

        assertThat(requestData.userId()).isEqualTo(userUUID.toString());
    }

    @Test
    public void shouldParseMessageHeadersFromMapUserId() {

        Map<String,String> headers = new HashMap<>();
        UUID userId = UUID.randomUUID();
        headers.put("X-Auth-UserId", userId.toString());

        requestData.parseMessageHeaders(headers);

        assertThat(requestData.userId()).isEqualTo(userId.toString());
    }

    @Test
    public void shouldParseMessageHeadersFromMapCorrelation() {

        Map<String,String> headers = new HashMap<>();
        UUID correlationId = UUID.randomUUID();
        headers.put("X-Correlation-Id", correlationId.toString());

        requestData.parseMessageHeaders(headers);

        assertThat(requestData.correlationId()).isEqualTo(correlationId.toString());
    }

    @Test
    public void shouldParseMessageHeadersFromMapGroups() {

        Map<String,String> headers = new HashMap<>();
        String groups = "Some Groups";
        headers.put("X-Auth-Groups", groups);

        requestData.parseMessageHeaders(headers);

        assertThat(requestData.groups()).isEqualTo(groups);
    }


    @Test
    public void shouldGetUserUUIDNull() {
        assertThat(requestData.userId()).isNull();
    }
}
