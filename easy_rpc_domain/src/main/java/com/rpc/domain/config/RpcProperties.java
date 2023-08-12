package com.rpc.domain.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
@Configuration
@ConfigurationProperties(prefix = "rpc")
@Validated
public class RpcProperties {

    /**
     * 默认序列化方式
     */
    @NonNull
    public String serialization = "HessianSerialization";


    /**
     * 此为客户端(服务提供者)配置
     */
    @Data
    @Component
    @ConfigurationProperties(prefix = "rpc.provider")
    public static class provider{
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
     * 此为注册中心配置
     */
    @Data
    @Component
    @ConfigurationProperties(prefix = "rpc.server")
    public static class RPCServer{
        /**
         * 注册中心运行ip
         */
        public String host;

        /**
         * 注册中心运行端口
         */
        public int port;

        /**
         * 服务过期时间,默认12秒
         */
        public int serviceSaveTime = 12;

    }

}
