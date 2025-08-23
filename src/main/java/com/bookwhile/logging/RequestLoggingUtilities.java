package com.bookwhile.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public final class RequestLoggingUtilities {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Formats json request-response body.
     */
    public static String bodyToPrettyJsonString(String body) {
        try {
            if (!StringUtils.hasText(body)) {
                return "";
            }

            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
                .writeValueAsString(OBJECT_MAPPER.readValue(body, Object.class));
        } catch (Exception e) {
            return body;
        }
    }

    /**
     * Transforms http request header to map for the given {@link HttpServletRequest}.
     */
    public static Map<String, Collection<String>> getRequestHeadersAsMap(HttpServletRequest req) {

        Enumeration<String> headerNames = req.getHeaderNames();

        Map<String, Collection<String>> headersMap = new HashMap<>();
        if (headerNames == null) {
            return headersMap;
        }

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headersMap.put(headerName, Collections.list(req.getHeaders(headerName)));
        }

        return headersMap;
    }

    /**
     * Transforms http response header to map for the given {@link HttpServletResponse}.
     */
    public static Map<String, Collection<String>> getResponseHeadersAsMap(HttpServletResponse response) {
        Collection<String> headerNames = response.getHeaderNames();

        if (headerNames == null || headerNames.isEmpty()) {
            return new HashMap<>();
        }

        return headerNames.stream()
            .distinct()
            .collect(Collectors.toMap(h -> h, response::getHeaders));
    }

    /**
     * Serializes given http header map. Given sensitive headers will be removed before
     * serialization.
     *
     * @param headers          map containing http headers
     * @param sensitiveHeaders header keys to be removed before serialization
     * @return serialized header list
     */
    public static String serializeHttpHeadersToString(Map<String, Collection<String>> headers,
                                                      Collection<String> sensitiveHeaders) {
        StringJoiner joiner = new StringJoiner(", ");
        for (Map.Entry<String, Collection<String>> h : headers.entrySet()) {
            if (sensitiveHeaders == null || sensitiveHeaders.stream().noneMatch(s -> s.equalsIgnoreCase(h.getKey()))) {
                if (h.getValue() != null && !h.getValue().isEmpty()) {
                    String string = h.getKey() + ":\"" + String.join(", ", h.getValue()) + "\"";
                    joiner.add(string);
                }
            }
        }
        return joiner.toString();
    }
}
