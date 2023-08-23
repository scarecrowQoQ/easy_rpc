package com.rpc.domain.limit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rpc.limit")
@Data
public class LimitConfig {
//    滑动窗口时间长度
    public int windowIntervalInMs = 4000;
//      滑动窗口的样本窗口数量
    public int sampleWindowAmount = 2;
//    限制条件

}
