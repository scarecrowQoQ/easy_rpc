package com.rpc.domain.protocol.coder;

import com.rpc.domain.protocol.serialization.RpcSerialization;
import com.rpc.domain.protocol.serialization.SerializationFactory;
import com.rpc.domain.protocol.serialization.serializationImpl.HessianSerialization;
import com.rpc.domain.protocol.serialization.serializationImpl.JsonSerialization;
import com.rpc.domain.rpc.RpcRequestHolder;
import com.rpc.domain.utils.SpringContextUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
@Component
//@Scope("prototype")
public class NettyEncoder extends MessageToByteEncoder<RpcRequestHolder> {

    @Resource
    SerializationFactory serializationFactory;

    /**
     * 编码器 步骤：
     * 1：先写入 requestHolder 数据载体的长度，方便解决数据半包粘包问题
     * 2：使用序列化工厂获取序列化器，进行序列化获取字节数组
     * 3：将字节数组写入bytebuf
     * @param channelHandlerContext
     * @param requestHolder
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequestHolder requestHolder, ByteBuf byteBuf) throws Exception {
        RpcSerialization rpcSerialization = serializationFactory.getRpcSerialization();
        try {
           byte[] data = rpcSerialization.serialize(requestHolder);
           byteBuf.writeInt(data.length);
           byteBuf.writeBytes(data);
            System.out.println("饭序列化成功"+data.length);
       }catch (Exception e){
           e.printStackTrace();
           System.out.println("序列化失败");
       }

    }
}
