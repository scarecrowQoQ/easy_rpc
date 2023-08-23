package com.rpc.domain.limit;

import com.rpc.domain.limit.entity.LimitingRule;
import com.rpc.domain.limit.handler.SelectLimitKey;
import com.rpc.domain.limit.limitStrategy.LimitStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LimitProcess {

    @Resource
    private LimitStrategy limitStrategy;

    public boolean isPass(LimitingRule rule,Object key){
        int Max_QPS = rule.getQPS();
        String strategyName = rule.getStrategyName();
        float curQPS = limitStrategy.getQPS(key);
        if(curQPS<Max_QPS){
            limitStrategy.incrPassCount(key);
            return true;
        }else {
            limitStrategy.incrBlockCount(key);
            return false;
        }
    }


    public boolean isPass(LimitingRule rule){
        int Max_QPS = rule.getQPS();
        String strategyName = rule.getStrategyName();
        SelectLimitKey selectLimitKey = rule.getSelectLimitKey();
        float curQPS = limitStrategy.getQPS();
        if(curQPS<=Max_QPS){
            limitStrategy.incrPassCount();
            return true;
        }else {
            limitStrategy.incrBlockCount();
            return false;
        }
    }

    public float getCurQPS(){
        return limitStrategy.getQPS();
    }
}
