package uk.gov.digital.ho.hocs.notify.application;

import org.apache.camel.Processor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class RequestData implements HandlerInterceptor {

    static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    static final String USER_ID_HEADER = "X-Auth-UserId";
    static final String GROUP_HEADER = "X-Auth-Groups";
    static final String CAMEL_CORRELATION_ID_HEADER = "correlationId";

    private static final String ANONYMOUS = "anonymous";

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.equals("");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        MDC.clear();
        MDC.put(CORRELATION_ID_HEADER, initialiseCorrelationId(request));
        MDC.put(USER_ID_HEADER, initialiseUserId(request));
        MDC.put(GROUP_HEADER, initialiseGroups(request));

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        MDC.clear();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        response.setHeader(USER_ID_HEADER, userId());
        response.setHeader(CORRELATION_ID_HEADER, correlationId());
        MDC.clear();
    }

    private String initialiseCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        return !isNullOrEmpty(correlationId) ? correlationId : UUID.randomUUID().toString();
    }

    private String initialiseUserId(HttpServletRequest request) {
        String userId = request.getHeader(USER_ID_HEADER);
        return !isNullOrEmpty(userId) ? userId : ANONYMOUS;
    }

    private String initialiseGroups(HttpServletRequest request) {
        String groups = request.getHeader(GROUP_HEADER);
        return !isNullOrEmpty(groups) ? groups : "/QU5PTllNT1VTCg==";
    }

    public static Processor transferHeadersToMDC() {
        return ex -> {
            MDC.put(CORRELATION_ID_HEADER, ex.getIn().getHeader(CORRELATION_ID_HEADER, String.class));
            MDC.put(CAMEL_CORRELATION_ID_HEADER, ex.getIn().getHeader(CORRELATION_ID_HEADER, String.class));
            MDC.put(USER_ID_HEADER, ex.getIn().getHeader(USER_ID_HEADER, String.class));
        };
    }

    public String correlationId() {
        return MDC.get(CORRELATION_ID_HEADER);
    }

    public String userId() {
        return MDC.get(USER_ID_HEADER);
    }

    public UUID userIdUUID() {
        return UUID.fromString(MDC.get(USER_ID_HEADER));
    }

    public String groups() {
        return MDC.get(GROUP_HEADER);
    }


}
