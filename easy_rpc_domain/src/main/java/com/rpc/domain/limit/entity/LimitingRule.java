package com.rpc.domain.limit.entity;

import com.rpc.domain.limit.limitAnnotation.LimitingStrategy;
import com.rpc.domain.limit.limitEnum.BlockStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LimitingRule {

    private String strategyName;

    Class<?> fallBack;

    BlockStrategy BlockStrategy;

    private String limitKey;

    private Object limitValue;

    private int maxQPS;

    public LimitingRule(LimitingStrategy limitingStrategy){
        this.maxQPS = limitingStrategy.QPS();
        this.strategyName = limitingStrategy.strategyName();
        this.BlockStrategy = limitingStrategy.BlockStrategy();
        this.fallBack = limitingStrategy.fallBack();
        this.limitKey = limitingStrategy.limitKey();
    }

}
