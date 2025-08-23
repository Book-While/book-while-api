package com.bookwhile.config;

import com.bookwhile.exception.GlobalAsyncExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@AutoConfiguration(after = TaskExecutionAutoConfiguration.class)
@ConditionalOnClass(ThreadPoolTaskExecutor.class)
@EnableAsync
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.async.enabled", havingValue = "true", matchIfMissing = true)
public class AsyncAutoConfiguration implements AsyncConfigurer {

    /**
     * Creates a {@link ThreadPoolTaskExecutor} bean named "taskExecutor".
     * <p>
     * This bean is only created if a {@link TaskExecutionProperties} bean is present in the context.
     * </p>
     *
     * @param taskExecutionProperties the task execution properties
     * @return a configured {@link Executor} instance
     */
    @Lazy
    @Bean(name = "taskExecutor")
    @ConditionalOnBean(TaskExecutionProperties.class)
    public Executor taskExecutor(TaskExecutionProperties taskExecutionProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        TaskExecutionProperties.Pool pool = taskExecutionProperties.getPool();
        executor.setCorePoolSize(pool.getCoreSize());
        executor.setMaxPoolSize(pool.getMaxSize());
        executor.setQueueCapacity(pool.getQueueCapacity());
        executor.setKeepAliveSeconds((int) pool.getKeepAlive().getSeconds());
        executor.setThreadNamePrefix(taskExecutionProperties.getThreadNamePrefix());

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.setWaitForTasksToCompleteOnShutdown(taskExecutionProperties.getShutdown().isAwaitTermination());
        executor.setAwaitTerminationSeconds((int) taskExecutionProperties.getShutdown().getAwaitTerminationPeriod().getSeconds());
        executor.initialize();
        return executor;
    }

    /**
     * Returns an {@link AsyncUncaughtExceptionHandler} instance that handles uncaught exceptions thrown by asynchronous methods.
     *
     * @return a configured {@link AsyncUncaughtExceptionHandler} instance
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new GlobalAsyncExceptionHandler();
    }
}
