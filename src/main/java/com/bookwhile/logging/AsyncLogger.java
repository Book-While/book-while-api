package com.bookwhile.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncLogger {

    private static final String SENSITIVE_DATA_PATTERN = "(?i)(\"[^\"]*password[^\"]*\"?:\\s*\")([^\"]+)(\")";

    private static final String SENSITIVE_DATA_REPLACEMENT = "$1****$3";

    private final RequestLogger requestLogger;

    private final LoggingProperties properties;

    @Async
    public void logRequestAndResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
                                      long startTime, long elapsedTime) {

        try {

            String requestBody = getRequestBody(request);
            String responseBody = getResponseBody(response, request.getRequestURL().toString());

            // Sanitize sensitive fields in the request and response body
            requestBody = sanitizeSensitiveFields(requestBody);
            responseBody = sanitizeSensitiveFields(responseBody);

            // Create log model
            RequestLogModel logModel = new RequestLogModel();
            logModel.setRequestDirection(RequestDirection.INBOUND);

            logModel.setUrl(getFullUrl(request));

            logModel.setHttpMethod(request.getMethod());

            logModel.setRequestHeaders(
                properties.getLogWebRequestHeaders() ? RequestLoggingUtilities.getRequestHeadersAsMap(request)
                    : Collections.emptyMap());

            logModel.setRequestBody(RequestLoggingUtilities.bodyToPrettyJsonString(requestBody));

            logModel.setRequestSize(request.getContentLength());

            logModel.setRequestDate(new Date(startTime));

            logModel.setElapsedTime(elapsedTime);

            logModel.setResponseHeaders(
                properties.getLogWebRequestHeaders() ? RequestLoggingUtilities.getResponseHeadersAsMap(response)
                    : Collections.emptyMap());

            logModel.setResponseBody(RequestLoggingUtilities.bodyToPrettyJsonString(responseBody));

            logModel.setResponseSize(response.getContentAsByteArray().length);

            logModel.setHttpStatusCode(response.getStatus());

            // Log the request and response
            requestLogger.logRequest(logModel);

        } catch (Exception e) {
            log.error("Error while logging request and response", e);
        }
    }

    private String getFullUrl(ContentCachingRequestWrapper request) {
        return request.getRequestURL().append(request.getQueryString() == null ? "" : "?" + request.getQueryString())
            .toString();
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {

        if (!properties.getLogWebRequestBody()) {
            return "";
        }

        byte[] content = request.getContentAsByteArray();
        return content.length > 0 ? new String(content, StandardCharsets.UTF_8) : "";
    }

    private String getResponseBody(ContentCachingResponseWrapper response, String requestUrl) {

        if (!properties.getLogWebResponseBody()
            || CollectionUtils.isEmpty(properties.getExcludeWebResponseBodyUrlList())
            || properties.getExcludeWebResponseBodyUrlList().stream().anyMatch(requestUrl::contains)) {

            return "";
        }

        byte[] content = response.getContentAsByteArray();
        return content.length > 0 ? new String(content, StandardCharsets.UTF_8) : "";
    }

    private String sanitizeSensitiveFields(String body) {

        if (!StringUtils.hasText(body)) {
            return body;
        }

        // Catch the password values
        Pattern pattern = Pattern.compile(SENSITIVE_DATA_PATTERN);
        Matcher matcher = pattern.matcher(body);

        // Replace password values with ****
        return matcher.replaceAll(SENSITIVE_DATA_REPLACEMENT);
    }
}
