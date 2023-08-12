package com.rpc.domain.protocol.serialization;

import com.rpc.domain.config.RpcProperties;
import com.rpc.domain.utils.SpringContextUtil;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Data
@Component
public class SerializationFactory {

    @Resource
    RpcProperties rpcProperties;

    public RpcSerialization getRpcSerialization(){
      return (RpcSerialization) SpringContextUtil.getBean(rpcProperties.serialization);
    }


}
