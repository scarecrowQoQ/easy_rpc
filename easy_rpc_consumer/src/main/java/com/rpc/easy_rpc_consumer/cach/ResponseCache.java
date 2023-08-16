package com.rpc.easy_rpc_consumer.cach;

import com.rpc.domain.rpc.ProviderResponse;
import com.rpc.domain.rpc.RpcRequestHolder;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Component
@Data
@Slf4j
public class ResponseCache {

    public ConcurrentHashMap<String, Promise<ProviderResponse>> responseCache = new ConcurrentHashMap<>();

    public void putPromise(String requestId,Promise<ProviderResponse> promise){
        responseCache.put(requestId,promise);
    }

    public Promise<ProviderResponse> getPromise(String requestId){
        return responseCache.get(requestId);
    }

    public void removeResponse(String requestId){
        if(responseCache.contains(requestId)){
            responseCache.remove(requestId);
        }
    }

    public ProviderResponse getResult(Promise<ProviderResponse> promise) throws ExecutionException, InterruptedException {
        return  promise.get();

    }
}
