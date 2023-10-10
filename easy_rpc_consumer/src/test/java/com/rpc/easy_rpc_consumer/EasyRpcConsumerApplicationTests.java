package com.rpc.easy_rpc_consumer;

import com.rpc.domain.bean.HeartBeat;
import com.rpc.domain.bean.RequestHeader;
import com.rpc.domain.bean.RpcRequestHolder;
import com.rpc.domain.enumeration.RequestType;
import com.rpc.easy_rpc_protocol.serialization.SerializationFactory;
import com.rpc.easy_rpc_protocol.serialization.serializationImpl.HessianSerialization;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

@SpringBootTest
@ComponentScan({"com.rpc.easy_rpc_consumer","com.rpc.easy_rpc_govern", "com.rpc.easy_rpc_protocol"})
class EasyRpcConsumerApplicationTests {

    @Resource
    SerializationFactory serializationFactory;
    @Test
    void contextLoads() {
        RpcRequestHolder rpcRequestHolder = new RpcRequestHolder(new RequestHeader(RequestType.SEND_HEARTBEAT),new HeartBeat());
        HessianSerialization hessianSerialization = serializationFactory.getHessianSerialization();
        byte[] serialize = hessianSerialization.serialize(rpcRequestHolder);

        RpcRequestHolder requestHolder = hessianSerialization.deserialize(serialize, RpcRequestHolder.class);
        System.out.println(requestHolder);

    }

}
