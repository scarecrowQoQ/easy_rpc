package com.rpc.domain.serialization;

import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.serialization.serializationImpl.HessianSerialization;
import com.rpc.domain.serialization.serializationImpl.JsonSerialization;
import lombok.Data;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;


@Data
@Component
public class SerializationFactory {

    @Resource
    RpcProperties rpcProperties;

    @Resource
    HessianSerialization hessianSerialization;

    @Resource
    JsonSerialization jsonSerialization;

    public RpcSerialization getRpcSerialization(){
        if(rpcProperties.serialization.equals("hessianSerialization")){
            return hessianSerialization;
        }else {
            return jsonSerialization;
        }
    }
}
