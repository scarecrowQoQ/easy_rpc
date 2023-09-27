package com.rpc.easy_rpc_govern.context;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
@Component
public class  AsyncContext {

    public static <T> CompletableFuture<T> getCallback(Supplier<T> async){
        return CompletableFuture.supplyAsync((Supplier<T>) async);
    }
}
