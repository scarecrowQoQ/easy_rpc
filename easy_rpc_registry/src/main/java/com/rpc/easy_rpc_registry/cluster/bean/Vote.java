package com.rpc.easy_rpc_registry.cluster.bean;

import lombok.Data;

import java.io.Serializable;
import java.sql.Date;
@Data
public class Vote implements Serializable {
//  拉票的发起人
    private String initiator;
//  响应者
    private String responder;
//  选期id
    private int termIndex;
//  响应时间
    private Date createTime;
}
