package com.rpc.easy_rpc_protocol.coder;

import com.rpc.easy_rpc_govern.config.RpcConfigProperties;
import com.rpc.easy_rpc_protocol.serialization.RpcSerialization;
import com.rpc.easy_rpc_protocol.serialization.SerializationFactory;
import com.rpc.domain.bean.RpcRequestHolder;
import com.rpc.easy_rpc_govern.utils.SpringContextUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class NettyEncoder extends MessageToByteEncoder<RpcRequestHolder>{

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
//    @Resource
//    private SerializationFactory serializationFactory;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequestHolder requestHolder, ByteBuf byteBuf) throws Exception {
        try {
            SerializationFactory serializationFactory = SpringContextUtil.getBean("serializationFactory",SerializationFactory.class);
            RpcConfigProperties rpcConfigProperties = SpringContextUtil.getBean(RpcConfigProperties.class);
            RpcSerialization rpcSerialization =  serializationFactory.getRpcSerialization(rpcConfigProperties.getSerialization());
            byte[] data = null;
            try{
                data = rpcSerialization.serialize(requestHolder);
           }catch (Exception e){
               e.printStackTrace();
               log.error("序列化失败");
           }
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }catch (Exception e){
            e.printStackTrace();
            log.error("序列化失败");
        }
    }
}