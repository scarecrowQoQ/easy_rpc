package com.rpc.easy_rpc_consumer.cach;

import com.rpc.domain.rpc.ProviderResponse;
import com.rpc.domain.rpc.RpcRequestHolder;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
@Component
@Data
public class ResponseCache {
    public static ConcurrentHashMap<String, ProviderResponse> responseCache = new ConcurrentHashMap<>();

    public void addResult(String requestId,ProviderResponse providerResponse){
        responseCache.put(requestId,providerResponse);
    }

    public ProviderResponse getResult(String requestId){
        return responseCache.get(requestId);
    }
}
