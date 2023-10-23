package com.rpc.easy_rpc_registry.cluster.bean;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
public class VoteResponse implements Serializable {
    private String voteId;

    //  响应者
    private String responder;

    //  选期id
    private int termId;

    //  发起时间
    private Date createTime;

    private Boolean vote;

    public VoteResponse(String responder, int termId, String voteId, Boolean vote){
        this.responder = responder;
        this.termId = termId;
        this.vote = vote;
        this.createTime = new Date();
        this.voteId = voteId;
    }
}
