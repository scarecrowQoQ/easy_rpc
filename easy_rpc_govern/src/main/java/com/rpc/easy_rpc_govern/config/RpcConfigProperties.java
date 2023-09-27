package com.rpc.easy_rpc_govern.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "rpc")
@Validated
@Data
public class RpcConfigProperties {

    /**
     * 默认序列化方式
     */
    @NonNull
    public String serialization = "hessianSerialization";

    /**
     * 此为客户端(服务提供者)配置
     */
    @Data
    @Component("RpcProvider")
    @ConfigurationProperties(prefix = "rpc.provider")
    public static class RpcProvider {
        /**
         * 服务提供运行主机
         */
        public String host;
        /**
         * 服务提供运行端口
         */
        public int port;
        /**
         * 服务提供实例id
         */
        public String clientId;

    }
    /**
     * 此为消费者配置
     */
    @Data
    @Component("RPCConsumer")
    @ConfigurationProperties(prefix = "rpc.consumer")
    public static class RPCConsumer{
        /**
         * 负载均衡策略,默认为轮询
         */
        public String loadBalance = "polling";
        /**
         * 熔断比率
         */
        public Float k = 1.2f;
        /**
         * 服务拉取时间,默认12秒
         */
        public Integer getServiceTime = 12;
        /**
         * 消费等待时长
         */
        public Long consumeWaitInMs = 2000L;

        public Boolean fuseEnable = false;

        public Boolean loadBalanceEnable = true;

    }

    /**
     * 此为注册中心配置
     */
    @Data
    @Component("RpcRegistry")
    @ConfigurationProperties(prefix = "rpc.registry")
    public static class RpcRegistry {
        /**
         * 注册中心运行ip
         */
        public String host;

        /**
         * 注册中心运行端口
         */
        public Integer port;

        /**
         * 服务过期时间,默认6秒
         */
        public Long serviceSaveTime = 6000L;

        /**
         * 是否开始集群模式
         */
        public Boolean isCluster = false;

        /**
         * 集群的ip配置
         */
        public List<String> cluster;
    }



}
