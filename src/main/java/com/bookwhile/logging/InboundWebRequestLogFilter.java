package com.bookwhile.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Order(InboundWebRequestLogFilter.INBOUND_REQUEST_LOG_FILTER_ORDER)
@Slf4j
public class InboundWebRequestLogFilter extends GenericFilterBean {

    public static final int INBOUND_REQUEST_LOG_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 1;

    private static final List<String> IGNORED_URL_PATTERNS = Arrays.asList(
        "/app/**/*.{js,html}",
        "/favicon*.*",
        "/i18n/**",
        "/content/**",
        "/swagger-**/**",
        "/v3/api-docs/**",
        "/h2/**",
        "/actuator/**",
        "/health"
    );

    private static final String SENSITIVE_DATA_PATTERN = "(?i)(\"[^\"]*password[^\"]*\"?:\\s*\")([^\"]+)(\")";

    private static final String SENSITIVE_DATA_REPLACEMENT = "$1****$3";

    final RequestLogger requestLogger;

    final LoggingProperties properties;

    private final AsyncLogger asyncLogger;

    public InboundWebRequestLogFilter(RequestLogger requestLogger, LoggingProperties properties,
                                      AsyncLogger asyncLogger) {
        this.requestLogger = requestLogger;
        this.properties = properties;
        this.asyncLogger = asyncLogger;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        // Ignore specific URLs if configured
        if (shouldIgnoreRequest(httpServletRequest)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        // Use Spring's ContentCaching wrappers for request and response
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpServletRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpServletResponse);

        // Start time for logging elapsed time
        final long startTime = System.currentTimeMillis();

        // Continue filter chain
        chain.doFilter(requestWrapper, responseWrapper);

        // End time for logging elapsed time
        final long elapsedTime = System.currentTimeMillis() - startTime;

        // Log request and response
        //        logRequestAndResponse(requestWrapper, responseWrapper, startTime, elapsedTime);
        CompletableFuture.runAsync(() ->
            asyncLogger.logRequestAndResponse(requestWrapper, responseWrapper, startTime, elapsedTime)
        );

        // Ensure the response body is written back
        responseWrapper.copyBodyToResponse();
    }

    private boolean shouldIgnoreRequest(HttpServletRequest request) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();

        String requestUrl = request.getRequestURI();
        return IGNORED_URL_PATTERNS.stream().anyMatch(pattern -> antPathMatcher.match(pattern, requestUrl));
    }

    private void logRequestAndResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
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
