package com.rpc.domain.limit.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public interface SelectLimitKey {
    public Object select(Object obj);
}
