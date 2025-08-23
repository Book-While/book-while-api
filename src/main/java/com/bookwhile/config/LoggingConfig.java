package com.bookwhile.config;


import com.bookwhile.logging.AsyncLogger;
import com.bookwhile.logging.InboundWebRequestLogFilter;
import com.bookwhile.logging.LoggingProperties;
import com.bookwhile.logging.MdcFilter;
import com.bookwhile.logging.RequestLogger;
import com.bookwhile.logging.Slf4jCustomLogger;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class LoggingConfig {

    @Bean
    public MdcFilter mdcFilter() {
        return new MdcFilter();
    }

    /**
     * Configures and registers {@link MdcFilter}.
     */
    @Bean
    public FilterRegistrationBean<MdcFilter> mdcFilterRegistrationBean(MdcFilter filter) {
        FilterRegistrationBean<MdcFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        return registration;
    }

    @Bean
    public LoggingProperties requestLoggingProperties() {
        return new LoggingProperties();
    }

    @ConditionalOnMissingBean(RequestLogger.class)
    @Bean
    public RequestLogger requestLogger(LoggingProperties properties) {
        return new Slf4jCustomLogger(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web", name = "enabled", havingValue = "true", matchIfMissing = true)
    public InboundWebRequestLogFilter inboundRequestLogFilter(RequestLogger requestLogger, LoggingProperties properties,
                                                              AsyncLogger asyncLogger) {
        return new InboundWebRequestLogFilter(requestLogger, properties, asyncLogger);
    }

    /**
     * Configures and registers {@link InboundWebRequestLogFilter}
     * unless "logging.inboundRequests.enabled" property stated otherwise.
     */
    @Bean
    @ConditionalOnProperty(prefix = "logging.web", name = "enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<InboundWebRequestLogFilter> requestLogFilterFilterRegistrationBean(
        InboundWebRequestLogFilter filter) {

        FilterRegistrationBean<InboundWebRequestLogFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(InboundWebRequestLogFilter.INBOUND_REQUEST_LOG_FILTER_ORDER);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
        return registration;
    }
}
