package com.rpc.domain.bean;

import com.rpc.domain.annotation.RpcConsumer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消费者请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumeRequest implements Serializable {

    /**
     * 请求服务名
     */
    private String serviceName;


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
    private Object[] args;



}
