package com.rpc.easy_rpc_core.annotation;

import com.rpc.easy_rpc_core.nettyServer.NettyServiceStart;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(NettyServiceStart.class)
public @interface EnableEasyRPC {
}
