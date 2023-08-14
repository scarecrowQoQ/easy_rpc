package com.rpc.domain.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * 消费者请求体
 */
@Data
@AllArgsConstructor

public class ConsumeRequest implements Serializable {

    /**
     * 本次请求标识ID
     */
    private String requestId;
    /**
     * 请求服务名
     */
    private String serviceName;
    /**
     * 服务提供方的beanName
     */
    private String beanName;
    /**
     * 方法名，默认匹配相同方法名
     */
    private String methodName;
    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;
    /**
     * 入参
     */
    private Object[] parameters;

    public ConsumeRequest(){
        long time = System.currentTimeMillis();
        int random = (int) (Math.random() * Integer.MAX_VALUE);
        UUID uuid = new UUID(time, random);
        this.requestId = uuid.toString();
    }
}
