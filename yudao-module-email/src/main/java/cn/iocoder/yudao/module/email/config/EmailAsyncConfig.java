package cn.iocoder.yudao.module.email.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 邮件模块异步任务配置
 *
 * @author 方总牛逼
 */
@Configuration
@EnableAsync
@Slf4j
public class EmailAsyncConfig {

    @Bean("emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(2);
        // 最大线程数
        executor.setMaxPoolSize(5);
        // 队列容量
        executor.setQueueCapacity(100);
        // 线程名前缀
        executor.setThreadNamePrefix("email-async-");
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程空闲时间
        executor.setKeepAliveSeconds(60);
        // 等待任务完成时间
        executor.setAwaitTerminationSeconds(60);
        // 关闭时等待任务完成
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        executor.initialize();
        log.info("邮件异步任务线程池初始化完成");
        return executor;
    }
} 