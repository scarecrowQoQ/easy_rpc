package com.rpc.domain.limit.limitAnnotation;

import com.rpc.domain.limit.limitEnum.BlockStrategy;
import lombok.Data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LimitingStrategy {
//    限制策略名
    String strategyName();
//    降级处理类
    Class<?> fallBack() default void.class;
//    拒绝策略,默认直接拒绝
    BlockStrategy BlockStrategy() default BlockStrategy.IMMEDIATE_REFUSE;

    String limitKey() default "";
}
