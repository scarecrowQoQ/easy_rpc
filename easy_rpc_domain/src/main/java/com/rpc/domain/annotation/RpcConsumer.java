package com.rpc.domain.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * 此注解用于标注需要进行代理成远程调用对象的属性
 * 除了serviceName以外的参数都是特殊于此服务的属性，如果没有设置，则以配置类的Consumer中同一为标准
 * 如果用户希望对于特殊的服务进行特殊的规定，那么可以对该服务进行自定义规范
 */
public @interface RpcConsumer {
    String serviceName();

    String group() default "";

    int maxRetryTime() default 4;

    long timeout() default 3000;

    Class<?> fallback() default void.class;

    boolean async() default false;

}
