package com.rpc.easy_rpc_govern.limit;

import com.rpc.easy_rpc_govern.limit.config.LimitConfig;
import com.rpc.easy_rpc_govern.limit.entity.LimitingRule;
import com.rpc.easy_rpc_govern.limit.limitStrategy.LimitStrategy;
import com.rpc.easy_rpc_govern.limit.limitStrategy.slidingWindowStrategy.SlidingWindow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class LimitProcess implements LimitHandler{

    private ConcurrentHashMap<String,LimitStrategy> limitStrategyMap = new ConcurrentHashMap<>();

    @Resource
    LimitConfig limitConfig;






    @Override
    public boolean limitHandle(LimitingRule rule) {
        int Max_QPS = rule.getMaxQPS();
        String strategyName = rule.getStrategyName();
        LimitStrategy limitStrategy = limitStrategyMap.getOrDefault(strategyName,new SlidingWindow(limitConfig));
        float curQPS;
        if (rule.getLimitValue() != null){
            curQPS = limitStrategy.getQPS(rule.getLimitValue());
            if(curQPS<Max_QPS){
                limitStrategy.incrPassCount(rule.getLimitValue());
                limitStrategyMap.put(strategyName,limitStrategy);
                return true;
            }  else {
                log.info("请求拒绝，当前QPS："+curQPS);
                limitStrategy.incrBlockCount(rule.getLimitValue());
                limitStrategyMap.put(strategyName,limitStrategy);
                return false;
            }
        }else {
            curQPS = limitStrategy.getQPS();
            if(curQPS<Max_QPS){
                limitStrategy.incrPassCount();
                limitStrategyMap.put(strategyName,limitStrategy);
                return true;
            }else {
                log.info("请求拒绝，当前QPS："+curQPS);
                limitStrategy.incrBlockCount();
                limitStrategyMap.put(strategyName,limitStrategy);
                return false;
            }
        }

    }
}
