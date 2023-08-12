package com.rpc.domain.protocol.coder;

import com.rpc.domain.protocol.serialization.RpcSerialization;
import com.rpc.domain.protocol.serialization.SerializationFactory;
import com.rpc.domain.protocol.serialization.serializationImpl.HessianSerialization;
import com.rpc.domain.protocol.serialization.serializationImpl.JsonSerialization;
import com.rpc.domain.rpc.RpcRequestHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
@Component
@Scope("prototype")
public class NettyDecoder extends ByteToMessageDecoder {
    @Resource
    SerializationFactory serializationFactory;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int dataLength = byteBuf.readInt();
        if(byteBuf.readableBytes() < dataLength){
            byteBuf.resetReaderIndex();
            return;
        }
        RpcSerialization rpcSerialization = serializationFactory.getRpcSerialization();
        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);
        try {
            RpcRequestHolder requestHolder = rpcSerialization.deserialize(data, RpcRequestHolder.class);
            list.add(requestHolder);
        }catch (Exception e){
            System.out.println("序列化失败");
        }


    }
}
