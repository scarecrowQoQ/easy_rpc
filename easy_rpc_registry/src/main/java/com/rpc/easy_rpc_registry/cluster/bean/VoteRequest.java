package com.rpc.easy_rpc_registry.cluster.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VoteRequest implements Serializable {
    //  拉票的发起人
    private String initiator;

    //  选期id
    private int termId;

    //  发起时间
    private Date createTime;
}
