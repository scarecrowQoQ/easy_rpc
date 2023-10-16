package com.rpc.easy_rpc_registry.cluster.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
public class VoteRequest implements Serializable {
    // 标识id
    private String voteId;

    //  拉票的发起人
    private String initiator;

    //  选期id
    private int termId;

    //  发起时间
    private Date createTime;

    public VoteRequest(String initiator, int termId){
        this.initiator = initiator;
        this.termId = termId;
        this.createTime = new Date();
        long time = System.currentTimeMillis();
        int random = (int) (Math.random() * Integer.MAX_VALUE);
        UUID uuid = new UUID(time, random);
        this.voteId = uuid.toString();

    }
}
