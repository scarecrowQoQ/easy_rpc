package com.rpc.easy_rpc_protocol.serialization;

import com.rpc.easy_rpc_govern.config.RpcConfigProperties;
import com.rpc.easy_rpc_protocol.serialization.serializationImpl.HessianSerialization;
import com.rpc.easy_rpc_protocol.serialization.serializationImpl.JsonSerialization;
import lombok.Data;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;


@Data
@Component("serializationFactory")
public class SerializationFactory {

    @Resource
    RpcConfigProperties rpcProperties;

    @Resource
    HessianSerialization hessianSerialization;

    @Resource
    JsonSerialization jsonSerialization;

    public RpcSerialization getRpcSerialization(String serializationType){
        if(serializationType.equals("hessianSerialization")){
            return hessianSerialization;
        }else {
            return jsonSerialization;
        }
    }
}
