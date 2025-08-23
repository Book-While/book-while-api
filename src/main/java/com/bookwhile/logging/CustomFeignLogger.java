package com.bookwhile.logging;

import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class CustomFeignLogger extends Logger {

    private static final String SENSITIVE_DATA_PATTERN = "(?i)(\"[^\"]*password[^\"]*\"?:\\s*\")([^\"]+)(\")";
    private static final String SENSITIVE_DATA_REPLACEMENT = "$1****$3";

    private final RequestLogger requestLogger;
    private final LoggingProperties properties;

    @Override
    protected void log(String configKey, String format, Object... args) {
        //  Overriding this method is mandatory, and it is empty because we are using are custom log format in logAndRebufferResponse
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        /*
         Overriding this method to do nothing otherwise it calls the super.logRequest
         , and it does unnecessary calculations that we don't need
         Method does nothing because we are logging the request in the logAndRebufferResponse
        */
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) {
        try {

            final long requestDateAsMs = System.currentTimeMillis() - elapsedTime;

            Request request = response.request();

            String requestBody = getRequestBody(request);
            requestBody = sanitizeSensitiveFields(requestBody);

            byte[] responseBodyAsBytes = response.body() != null
                ? Util.toByteArray(response.body().asInputStream()) : new byte[0];

            String responseBody = getResponseBody(responseBodyAsBytes, request.url(), response.status());
            responseBody = sanitizeSensitiveFields(responseBody);

            RequestLogModel requestLogModel = new RequestLogModel();

            requestLogModel.setRequestDirection(RequestDirection.OUTBOUND);

            requestLogModel.setUrl(request.url());

            requestLogModel.setHttpMethod(request.httpMethod().name());

            requestLogModel.setRequestHeaders(
                properties.getLogFeignRequestHeaders() ? request.headers() : Collections.emptyMap());

            requestLogModel.setRequestBody(RequestLoggingUtilities.bodyToPrettyJsonString(requestBody));

            requestLogModel.setRequestSize(request.length());

            requestLogModel.setRequestDate(new Date(requestDateAsMs));

            requestLogModel.setElapsedTime(elapsedTime);

            requestLogModel.setResponseHeaders(
                properties.getLogFeignResponseHeaders() ? response.headers() : Collections.emptyMap());

            requestLogModel.setResponseBody(RequestLoggingUtilities.bodyToPrettyJsonString(responseBody));

            requestLogModel.setResponseSize(requestBody.length());

            requestLogModel.setHttpStatusCode(response.status());

            requestLogger.logRequest(requestLogModel);

            return response.toBuilder()
                .body(responseBodyAsBytes)
                .build();
        } catch (Exception e) {
            log.error("Exception occurred while logging FeignClient request", e);
            return response;
        }
    }

    private String getRequestBody(Request request) {

        byte[] requestBodyAsBytes = request.body();

        if (!properties.getLogFeignRequestBody() || requestBodyAsBytes == null) {
            return "";
        }

        return requestBodyAsBytes.length > 0 ? new String(requestBodyAsBytes, StandardCharsets.UTF_8) : "";
    }

    private String getResponseBody(byte[] responseBodyAsBytes, String requestUrl, int responseStatus) {

        if (responseStatus == 204 || responseStatus == 205) {
            return "";
        }

        if (!properties.getLogFeignResponseBody()
            || CollectionUtils.isEmpty(properties.getExcludeFeignResponseBodyUrlList())
            || properties.getExcludeFeignResponseBodyUrlList().stream().anyMatch(requestUrl::contains)) {

            return "";
        }

        return responseBodyAsBytes.length > 0 ? new String(responseBodyAsBytes, StandardCharsets.UTF_8) : "";
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