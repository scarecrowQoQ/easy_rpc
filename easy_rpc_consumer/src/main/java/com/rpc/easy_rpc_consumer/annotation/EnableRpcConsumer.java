package com.rpc.easy_rpc_consumer.annotation;

import com.rpc.easy_rpc_consumer.consumer.ConsumerProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ConsumerProcessor.class)
public @interface EnableRpcConsumer {
}
