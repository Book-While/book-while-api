package com.bookwhile.logging;

import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Data
public class RequestLogModel {

    private String httpMethod;
    private String url;
    private Date requestDate;
    private long elapsedTime;
    private long requestSize;
    private String requestBody;
    private Map<String, Collection<String>> requestHeaders;
    private long responseSize;
    private String responseBody;
    private Map<String, Collection<String>> responseHeaders;
    private Integer httpStatusCode;
    private RequestDirection requestDirection;

}