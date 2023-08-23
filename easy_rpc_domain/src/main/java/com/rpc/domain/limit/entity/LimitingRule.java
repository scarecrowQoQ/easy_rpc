package com.rpc.domain.limit.entity;

import com.rpc.domain.limit.handler.SelectLimitKey;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LimitingRule {

    private String strategyName;

    private int QPS;

    private SelectLimitKey selectLimitKey;

    public LimitingRule(String strategyName,int QPS){
        this.QPS = QPS;
    }

}
