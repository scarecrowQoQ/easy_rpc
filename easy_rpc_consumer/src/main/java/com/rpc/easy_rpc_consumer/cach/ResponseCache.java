package com.rpc.easy_rpc_consumer.cach;

import com.rpc.domain.protocol.bean.ProviderResponse;
import io.netty.util.concurrent.Promise;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

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
        responseCache.remove(requestId);
    }

}
