package com.rpc.easy_rpc_consumer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface RpcConsumer {
    String serviceName();

    int maxRetryTime() default 4;

    long timeout() default 3000;

    Class<?> fallback() default void.class;

    String loadBalancer() default "loadBalancer";

    boolean async() default false;

}
