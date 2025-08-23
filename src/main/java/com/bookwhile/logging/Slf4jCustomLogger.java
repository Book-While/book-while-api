package com.bookwhile.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@EnableConfigurationProperties(LoggingProperties.class)
public class Slf4jCustomLogger implements RequestLogger {

    final LoggingProperties properties;

    public Slf4jCustomLogger(LoggingProperties properties) {
        this.properties = properties;
    }

    @Override
    public void logRequest(RequestLogModel requestLogModel) {
        try {
            log.info(getMessage(requestLogModel));
        } catch (Exception e) {
            log.error("Error while request logging", e);
        }
    }

    private String getMessage(RequestLogModel requestLogModel) {
        StringBuilder message = new StringBuilder();
        //TODO: add trace id to log
        String requestHeaders = RequestLoggingUtilities.serializeHttpHeadersToString(
            requestLogModel.getRequestHeaders(), properties.getSensitiveHeaders());

        String responseHeaders = RequestLoggingUtilities.serializeHttpHeadersToString(
            requestLogModel.getResponseHeaders(), properties.getSensitiveHeaders());

        message
            .append("\nRequest Date: ").append(formatDate(requestLogModel.getRequestDate()))
            .append("\nRequest Direction: ").append(requestLogModel.getRequestDirection())
            .append("\nRequested Url: ").append(requestLogModel.getUrl())
            .append("\nHTTP Method: ").append(requestLogModel.getHttpMethod())
            .append("\nRequest Size: ").append(requestLogModel.getRequestSize())
            .append("\nResponse Time: ").append(requestLogModel.getElapsedTime())
            .append("\nResponse Size: ").append(requestLogModel.getResponseSize())
            .append("\nHTTP Status Code: ").append(requestLogModel.getHttpStatusCode())
            .append("\nRequest Headers: ").append(requestHeaders)
            .append("\nRequest Body:\n").append(requestLogModel.getRequestBody())
            .append("\nResponse Headers: ").append(responseHeaders)
            .append("\nResponse Body:\n").append(requestLogModel.getResponseBody());
        return message.toString();
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        return sdf.format(date);
    }
}
