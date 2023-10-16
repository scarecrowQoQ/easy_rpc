package com.rpc.easy_rpc_registry.cluster.handler;

import com.rpc.domain.bean.HeartBeat;
import com.rpc.domain.bean.RequestHeader;
import com.rpc.domain.bean.RpcRequestHolder;
import com.rpc.domain.bean.RpcServiceList;
import com.rpc.domain.enumeration.RequestType;
import com.rpc.easy_rpc_registry.cluster.bean.NodeContent;
import com.rpc.easy_rpc_registry.cluster.bean.VoteRequest;
import com.rpc.easy_rpc_registry.cluster.enumeration.NodeStatus;
import com.rpc.easy_rpc_registry.cluster.service.ClusterService;
import com.rpc.easy_rpc_registry.utils.SyncResultUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.Map;
/**
 * 作为服务端接受消息后响应
 * 主要包含：
 * 1.对拉票请求进行响应
 * 2.对确认Leader请求进行响应
 * 3.对Leader心跳进行接受
 */
@Service
@ChannelHandler.Sharable
@ConditionalOnProperty(
        value="rpc.registry.cluster",
        havingValue = "true",
        matchIfMissing = false)
@Slf4j
public class ClusterHandler extends ChannelInboundHandlerAdapter {

    @Resource
    ClusterService clusterService;

    @Resource
    NodeContent nodeContent;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequestHolder rpcRequestHolder = (RpcRequestHolder) msg;
        RequestHeader requestHeader = rpcRequestHolder.getRequestHeader();
        RequestType type = requestHeader.getType();
        Object data = rpcRequestHolder.getData();
        Boolean sync = requestHeader.getSync();
        if(sync!=null && sync){
            SyncResultUtil.setResult(requestHeader.getRequestId(),data);
        }
        if(type.equals(RequestType.ASK_VOTE)){
            VoteRequest voteRequest = (VoteRequest) data;
            RpcRequestHolder vote = clusterService.vote(voteRequest);
            ctx.channel().writeAndFlush(vote);
//      集群确认了新的Leader,数据包为最新的Leader心跳
        }else if(type.equals(RequestType.CONFIRM_LEADER)){
            log.info("接受到Leader确认信号");
            HeartBeat leaderHeartBeat = (HeartBeat) data;
//           关键，需要先设置好心跳，否则下一步开启心跳检测后发现心跳为空则误以为Leader不存在
            nodeContent.setLeaderHeartBeat(leaderHeartBeat);
            nodeContent.setNodeStatus(NodeStatus.FOLLOWER);
            nodeContent.setLeaderAddress(leaderHeartBeat.getInitiator());
//      对于Leader心跳进行接受并刷新上传时间
        }else if(type.equals(RequestType.LEADER_HEARTBEAT)){
            HeartBeat leaderHeartBeat = (HeartBeat) data;
            nodeContent.setLeaderHeartBeat(leaderHeartBeat);
        }else if(type.equals(RequestType.UPDATE_DATA)){
            RpcServiceList rpcServiceList = (RpcServiceList) data;
            Map<String,Object> attrs = rpcRequestHolder.getRequestHeader().getAttrs();
            if(attrs != null&&attrs.containsKey("termId")){
                int termId = (int) attrs.get("termId");
//              选期id 大于等于当前节点的id才认为数据有效
                if(termId<nodeContent.getTermId()){
                    return;
                }
                nodeContent.setTermId(termId);
                clusterService.updateDataFromLeader(rpcServiceList);

            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String host = insocket.getAddress().getHostAddress();
        int port = insocket.getPort();
        log.error("连接断开了host:"+host+":"+port);
    }
}
