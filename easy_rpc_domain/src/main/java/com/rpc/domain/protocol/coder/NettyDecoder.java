package com.rpc.domain.protocol.coder;

import com.rpc.domain.serialization.RpcSerialization;
import com.rpc.domain.serialization.SerializationFactory;
import com.rpc.domain.protocol.bean.RpcRequestHolder;
import com.rpc.domain.utils.SpringContextUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Scope("prototype")
public class NettyDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int dataLength = byteBuf.readInt();
        if(byteBuf.readableBytes() < dataLength){
            byteBuf.resetReaderIndex();
            return;
        }
        SerializationFactory serializationFactory = SpringContextUtil.getBean(SerializationFactory.class);
        RpcSerialization rpcSerialization =  serializationFactory.getRpcSerialization();
        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);
        try {
            RpcRequestHolder requestHolder = rpcSerialization.deserialize(data, RpcRequestHolder.class);
            list.add(requestHolder);
        }catch (Exception e){
            log.error("序列化失败");
        }

    }
}