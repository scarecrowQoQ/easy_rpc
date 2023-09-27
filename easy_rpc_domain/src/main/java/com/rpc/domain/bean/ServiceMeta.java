package com.rpc.domain.bean;

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
    /**
     * 活跃数即连接数（每次上传后重置为0）
     */
    private int actives;
    /**
     * 服务分组
     */
    private String group;


}
