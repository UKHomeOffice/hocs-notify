package uk.gov.digital.ho.hocs.notify.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.sqs.SqsConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.hocs.notify.api.dto.NotifyCommand;
import uk.gov.digital.ho.hocs.notify.domain.NotifyDomain;

@Profile({"sqs","local"})
@Component
public class NotifyConsumer extends RouteBuilder {

    private final String fromQueue;
    private String dlq;
    private final int maximumRedeliveries;
    private final int redeliveryDelay;
    private final int backOffMultiplier;
    private final NotifyDomain notifyDomain;

    @Autowired
    public NotifyConsumer(
            NotifyDomain notifyDomain,
            @Value("${notify.queue}") String notifyQueue,
            @Value("${notify.queue.dlq}") String dlq,
            @Value("${notify.queue.maximumRedeliveries}") int maximumRedeliveries,
            @Value("${notify.queue.redeliveryDelay}") int redeliveryDelay,
            @Value("${notify.queue.backOffMultiplier}") int backOffMultiplier) {
        this.notifyDomain = notifyDomain;
        this.fromQueue = notifyQueue;
        this.dlq = dlq;
        this.maximumRedeliveries = maximumRedeliveries;
        this.redeliveryDelay = redeliveryDelay;
        this.backOffMultiplier = backOffMultiplier;
    }

    @Override
    public void configure() {
        errorHandler(deadLetterChannel(dlq)
                .loggingLevel(LoggingLevel.ERROR)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .useOriginalMessage()
                .maximumRedeliveries(maximumRedeliveries)
                .redeliveryDelay(redeliveryDelay)
                .backOffMultiplier(backOffMultiplier)
                .asyncDelayedRedelivery()
                .logRetryStackTrace(false)
                .onPrepareFailure(exchange -> {
                    exchange.getIn().setHeader("FailureMessage", exchange.getProperty(Exchange.EXCEPTION_CAUGHT,
                            Exception.class).getMessage());
                    exchange.getIn().setHeader(SqsConstants.RECEIPT_HANDLE, exchangeProperty(SqsConstants.RECEIPT_HANDLE));
                }));

        from(fromQueue).routeId("notify-queue")
                .setProperty(SqsConstants.RECEIPT_HANDLE, header(SqsConstants.RECEIPT_HANDLE))
                .log(LoggingLevel.INFO,"Notify Command Received")
                .log(LoggingLevel.DEBUG,"Body: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, NotifyCommand.class)
                .log(LoggingLevel.DEBUG,"Notify Command Unmarshalled")
                .bean(notifyDomain, "executeCommand")
                .log(LoggingLevel.INFO,"Command processed")
                .setHeader(SqsConstants.RECEIPT_HANDLE, exchangeProperty(SqsConstants.RECEIPT_HANDLE));
    }
}