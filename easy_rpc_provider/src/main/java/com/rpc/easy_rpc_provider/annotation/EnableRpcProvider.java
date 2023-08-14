package com.rpc.easy_rpc_provider.annotation;

import com.rpc.easy_rpc_provider.provider.ProviderProcessor;
import org.springframework.context.annotation.Import;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ProviderProcessor.class)
public @interface EnableRpcProvider {

}
