package com.rpc.easy_rpc_protocol.coder;

import com.rpc.domain.bean.RpcRequestHolder;
import com.rpc.easy_rpc_govern.config.RpcConfigProperties;
import com.rpc.easy_rpc_govern.utils.SpringContextUtil;
import com.rpc.easy_rpc_protocol.serialization.RpcSerialization;
import com.rpc.easy_rpc_protocol.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
public class NettyDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int dataLength = byteBuf.readInt();
        if(byteBuf.readableBytes() < dataLength){
            byteBuf.resetReaderIndex();
            return;
        }
        SerializationFactory serializationFactory = SpringContextUtil.getBean(SerializationFactory.class);
        RpcConfigProperties rpcConfigProperties = SpringContextUtil.getBean(RpcConfigProperties.class);
        RpcSerialization rpcSerialization =  serializationFactory.getRpcSerialization(rpcConfigProperties.getSerialization());
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