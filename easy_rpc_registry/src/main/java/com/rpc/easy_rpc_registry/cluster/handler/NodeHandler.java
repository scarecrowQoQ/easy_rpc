package com.rpc.easy_rpc_registry.cluster.handler;

import com.rpc.domain.bean.RequestHeader;
import com.rpc.domain.bean.RpcRequestHolder;
import com.rpc.domain.enumeration.RequestType;
import com.rpc.easy_rpc_registry.cluster.bean.NodeContent;
import com.rpc.easy_rpc_registry.cluster.bean.VoteResponse;
import com.rpc.easy_rpc_registry.cluster.enumeration.NodeStatus;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
@ConditionalOnProperty(
        value="rpc.registry.cluster",
        havingValue = "true",
        matchIfMissing = false)
@Slf4j
public class NodeHandler extends ChannelInboundHandlerAdapter {
    @Resource
    NodeContent nodeContent;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequestHolder rpcRequestHolder = (RpcRequestHolder) msg;
        RequestHeader requestHeader = rpcRequestHolder.getRequestHeader();
        RequestType type = requestHeader.getType();
        Object data = rpcRequestHolder.getData();

//      其他节点返回投票结果
        if (type.equals(RequestType.VOTE)) {
            VoteResponse voteResponse = (VoteResponse) data;
            log.info("接收到来自"+voteResponse.getResponder()+"的投票");
            handleVoteResponse(voteResponse);
        }
//      其他节点返回确认消息
        else if (type.equals(RequestType.RESPONSE_ACK)) {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void handleVoteResponse(VoteResponse voteResponse){
        nodeContent.getNodeStatusListener().incrBallot(voteResponse.getVoteId());
//      如果结果为不投票或者当前身份不是候选者或者响应的选期id与当前选期id不一致则为无效投票
        if(!voteResponse.getVote()){
            log.error("无效投票，对方不选择投票");
            return;
        }
        if(!nodeContent.getNodeStatus().equals(NodeStatus.CANDIDATE) ){
            log.error("无效投票，当前身份不是候选者");
            return;
        }
        if(voteResponse.getTermId() != nodeContent.getTermId()){
            log.error("无效投票，id过期");
            return;
        }
        nodeContent.getBallot().incrementAndGet();
    }
}
