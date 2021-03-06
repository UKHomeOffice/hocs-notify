package uk.gov.digital.ho.hocs.notify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.digital.ho.hocs.notify.application.RequestData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    private RequestData requestData;

    @BeforeEach
    public void setup() {
        requestData = new RequestData();
    }

    @Test
    public void shouldDefaultRequestData() {
        requestData.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler);

        assertThat(requestData.correlationId()).isNotNull();
        assertThat(requestData.userId()).isEqualTo("anonymous");
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
        when(mockHttpServletRequest.getHeader("X-Auth-UserId")).thenReturn("some user id");

        requestData.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler);

        assertThat(requestData.userId()).isEqualTo("some user id");
    }
}
