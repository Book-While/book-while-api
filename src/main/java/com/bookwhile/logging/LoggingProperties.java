package com.bookwhile.logging;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "logging")
public class LoggingProperties {

    /**
     * List of sensitive request-response headers to be removed from logs.
     */
    private List<String> sensitiveHeaders;

    private Boolean logWebRequestHeaders = true;

    private Boolean logWebResponseHeaders = true;

    private Boolean logWebRequestBody = true;

    private Boolean logWebResponseBody = true;

    private Boolean logFeignRequestHeaders = true;

    private Boolean logFeignResponseHeaders = true;

    private Boolean logFeignRequestBody = true;

    private Boolean logFeignResponseBody = true;

    private Boolean logSoapRequestBody = true;

    private Boolean logSoapResponseBody = true;

    private List<String> excludeWebResponseBodyUrlList;

    private List<String> excludeFeignResponseBodyUrlList;

    private List<String> excludeSoapUrlList;
}
