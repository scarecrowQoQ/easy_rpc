package com.rpc.easy_rpc_govern.limit.entity;

import com.rpc.easy_rpc_govern.limit.limitAnnotation.LimitingStrategy;
import com.rpc.easy_rpc_govern.limit.limitEnum.BlockStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;

@Data
@AllArgsConstructor
public class LimitingRule {

    private String strategyName;

    Class<?> fallBack;

    BlockStrategy BlockStrategy;

    private String limitKey;

    private Object limitValue;

    private int maxQPS;

    public LimitingRule(LimitingStrategy limitingStrategy,Object[] args){
        this.maxQPS = limitingStrategy.QPS();
        this.strategyName = limitingStrategy.strategyName();
        this.BlockStrategy = limitingStrategy.BlockStrategy();
        this.fallBack = limitingStrategy.fallBack();
        this.limitKey = limitingStrategy.limitKey();
        String limitKey = limitingStrategy.limitKey();
        if(!limitKey.equals("")){
            for (Object arg : args) {
                for (Field declaredField : arg.getClass().getDeclaredFields()) {
                    if(declaredField.getName().equals(limitKey)){
                        try {
//                            设置可访问性
                            declaredField.setAccessible(true);
//                            拿到值
                            Object value = declaredField.get(arg);
                            this.setLimitValue(value);
                        } catch (Throwable e ){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
