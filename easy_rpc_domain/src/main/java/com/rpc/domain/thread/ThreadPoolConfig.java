package com.rpc.domain.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {
    /**
     * 核心线程
     *
     */
    private static final int CORE_POOL_SIZE = 3;
    /**
     * 最大线程数
     * 默认的最大线程数是Integer.MAX_VALUE 即2<sup>31</sup>-1
     */
    private static final int MAX_POOL_SIZE = 20;
    /**
     * 缓冲队列数
     * 默认的缓冲队列数是Integer.MAX_VALUE 即2<sup>31</sup>-1
     */
    private static final int QUEUE_CAPACITY = 100;

    /**
     * 允许线程空闲时间
     */
    private static final int KEEP_ALIVE_SECONDS = 30;

    /**
     * 线程池前缀名
     */
    private static final String THREAD_NAME_PREFIX = "Task_Service_Async_";

    /**
     * allowCoreThreadTimeOut为true则线程池数量最后销毁到0个
     * allowCoreThreadTimeOut为false
     * 销毁机制：超过核心线程数时，而且（超过最大值或者timeout过），就会销毁。
     * 默认是false
     */
    private boolean allowCoreThreadTimeOut = false;

    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        taskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        taskExecutor.setQueueCapacity(QUEUE_CAPACITY);
        taskExecutor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        taskExecutor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        taskExecutor.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //线程池初始化
        taskExecutor.initialize();
        return taskExecutor;
    }
}
