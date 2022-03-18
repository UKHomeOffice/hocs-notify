package uk.gov.digital.ho.hocs.notify.application;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

@Component
public class RequestData implements HandlerInterceptor {

    static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    static final String USER_ID_HEADER = "X-Auth-UserId";
    static final String GROUP_HEADER = "X-Auth-Groups";

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.equals("");
    }

    public void parseMessageHeaders(Map<String,String> headers) {
        if(headers.containsKey(CORRELATION_ID_HEADER)) {
            MDC.put(CORRELATION_ID_HEADER, headers.get(CORRELATION_ID_HEADER));
        }

        if(headers.containsKey(USER_ID_HEADER)) {
            MDC.put(USER_ID_HEADER, headers.get(USER_ID_HEADER));
        }

        if(headers.containsKey(GROUP_HEADER)) {
            MDC.put(GROUP_HEADER, headers.get(GROUP_HEADER));
        }
    }

    public void clear(){
        MDC.clear();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        MDC.clear();
        MDC.put(CORRELATION_ID_HEADER, initialiseCorrelationId(request));
        MDC.put(USER_ID_HEADER, request.getHeader(USER_ID_HEADER));
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

    private String initialiseGroups(HttpServletRequest request) {
        String groups = request.getHeader(GROUP_HEADER);
        return !isNullOrEmpty(groups) ? groups : "/QU5PTllNT1VTCg==";
    }

    public String correlationId() {
        return MDC.get(CORRELATION_ID_HEADER);
    }

    public String userId() {
        return MDC.get(USER_ID_HEADER);
    }

    public UUID userIdUUID() {
        String userId = MDC.get(USER_ID_HEADER);
        if(userId == null) {
            return null;
        }
        return UUID.fromString(userId);
    }

    public String groups() {
        return MDC.get(GROUP_HEADER);
    }

}
