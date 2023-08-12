package com.rpc.domain.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 此类为注册服务实体类，包含一个远程服务的全部消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceMeta implements Serializable {
    /**
     * 服务名
     */
    private String serviceName;
    /**
     * 服务实例的beanName
     */
    private String beanName;
    /**
     * 服务者的ip
     */
    private String serviceHost;
    /**
     * 服务者的端口
     */
    private int servicePort;
    /**
     * 上传日期
     */
    private Long updateTime;
}
