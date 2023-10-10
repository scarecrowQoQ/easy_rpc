package com.rpc.easy_rpc_registry.cluster.handler;

import com.rpc.domain.bean.RequestHeader;
import com.rpc.domain.bean.RpcRequestHolder;
import com.rpc.domain.enumeration.RequestType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Service;

/**
 * 作为客户端主动发送消息后的响应
 * 主要包含：
 * 1. 主动发出拉票请求后 其他节点响应然后处理
 * 2. 主动向其他节点发送自己作为Leader身份，其他节点响应然后处理
 * 3. 主动向其他Follower节点 发送心跳，确认自己的Leader身份（返回消息无需处理）
 * 4. 主动向其他Follower节点 更新数据
 */
@Service
@ChannelHandler.Sharable
public class NodeHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequestHolder rpcRequestHolder = (RpcRequestHolder) msg;
        RequestHeader requestHeader = rpcRequestHolder.getRequestHeader();
        RequestType type = requestHeader.getType();

//      其他节点返回投票结果
        if(type.equals(RequestType.VOTE)){

        }
//      其他节点返回确认消息
        else if (type.equals(RequestType.RESPONSE_ACK)) {

        }

        super.channelRead(ctx, msg);
    }
}
