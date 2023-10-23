### Raft共识算法 选举流程的Java落地实现（SpringBoot，Netty）

---

因为网上已经有很多对Raft算法的原理讲解，所以本文不会再详细的将raft原理，而是更多的注重代码的实现，避坑，和细节上面，现在初步完成一个三节点集群的选举，效果如下
这是8346节点，最后成为Leader的日志打印

```
12:02:44.175  INFO 7300 --- [         task-1]       : 开启netty服务:/0:0:0:0:0:0:0:0:8345
12:02:44.415  INFO 7300 --- [         task-2]       : 节点：127.0.0.1：8344连接成功！
12:02:47.879  INFO 7300 --- [         task-2]       : 节点：127.0.0.1：8346连接成功！
12:02:47.879  INFO 7300 --- [         task-2]       : 节点全部连接成功!开始初始化集群
12:02:47.880  INFO 7300 --- [         task-2]       : 我现在的身份是INITIAL
12:02:47.881  INFO 7300 --- [         task-2]       : 开始本轮选举，当前选期id=0
12:02:47.881  INFO 7300 --- [         task-2]       : 随机睡眠1086ms
12:02:48.968  INFO 7300 --- [         task-2]       : 我现在的身份是CANDIDATE
12:02:48.969  INFO 7300 --- [         task-2]       : 我要去选举
12:02:48.986  INFO 7300 --- [         task-2]       : 开始同步阻塞等待本轮投票结束
12:02:49.109  INFO 7300 --- [ntLoopGroup-2-3]       : 接收到来自127.0.0.1:8346的投票
12:02:49.109  INFO 7300 --- [ntLoopGroup-2-1]       : 接收到来自127.0.0.1:8344的投票
12:02:49.110  INFO 7300 --- [         task-2]       : 同步结束，耗时:124
12:02:49.111  INFO 7300 --- [         task-2]       : 本轮投票结束，当前票数3
12:02:49.111  INFO 7300 --- [         task-2]        : 我现在的身份是LEADER
12:02:49.112  INFO 7300 --- [         task-2]       : 开始本轮选举，当前选期id=1
12:02:49.112  INFO 7300 --- [         task-2]       : 选举结束
```

下面是8345节点日志打印

```
12:02:47.195  INFO 8184 --- [         task-1]       : 开启netty服务:/0:0:0:0:0:0:0:0:8346
12:02:47.436  INFO 8184 --- [         task-2]       : 节点：127.0.0.1：8344连接成功！
12:02:47.656  INFO 8184 --- [         task-2]       : 节点：127.0.0.1：8345连接成功！
12:02:47.656  INFO 8184 --- [         task-2]       : 节点全部连接成功!开始初始化集群
12:02:47.656  INFO 8184 --- [         task-2]       : 我现在的身份是INITIAL
12:02:47.656  INFO 8184 --- [         task-2]       : 开始本轮选举，当前选期id=0
12:02:47.656  INFO 8184 --- [         task-2]       : 随机睡眠1580ms
12:02:49.091  INFO 8184 --- [ntLoopGroup-4-2]       : 我收到了来自127.0.0.1:8345的拉票,我为他投票
12:02:49.114  INFO 8184 --- [ntLoopGroup-4-2]       : 接受到Leader确认信号
12:02:49.114  INFO 8184 --- [ntLoopGroup-4-2]       : 我现在的身份是FOLLOWER
12:02:49.246  INFO 8184 --- [         task-2]       : 已经给其他节点投过票了,退出本轮选举
```

此外还有一个8344节点

```
 12:02:47.736  INFO 3560 --- [         task-2]      : 节点：127.0.0.1：8346连接成功！
 12:02:47.736  INFO 3560 --- [         task-2]      : 节点全部连接成功!开始初始化集群
 12:02:47.736  INFO 3560 --- [         task-2]      : 我现在的身份是INITIAL
 12:02:47.736  INFO 3560 --- [         task-2]      : 开始本轮选举，当前选期id=0
 12:02:47.736  INFO 3560 --- [         task-2]      : 随机睡眠1485ms
 12:02:49.088  INFO 3560 --- [ntLoopGroup-4-1]      : 我收到了来自127.0.0.1:8345的拉票,我为他投票
 12:02:49.113  INFO 3560 --- [ntLoopGroup-4-1]      : 接受到Leader确认信号
 12:02:49.113  INFO 3560 --- [ntLoopGroup-4-1]      : 我现在的身份是FOLLOWER
 12:02:49.222  INFO 3560 --- [         task-2]      : 已经给其他节点投过票了,退出本轮选举
```



**Raft选举大概流程是：**

**1. 当一个节点超时没有收到Leader节点的心跳则会进入选举过程，或者集群初始化也要进入选举过程**

**2. 节点首先会随机睡眠一段时间，唤醒后检查当前是否有为其他节点投票，如果没有则变为Candidate，为自己投票然后向其他节点拉票，如果获得的票数大于集群节点的一半，则身份转为Leader，向其他节点发送确认Leader信号，让其他节点身份转为Follwer，停止选举**

初步看起来还是有点繁琐的，实现起来因为Netty是这种异步，响应式模型，导致代码容易东一块西一块的，虽然我实现完成了，但是仍然有很大优化空间。

---

#### 使用观察者模式

从上面分析很容易得出，可以使用观察者模式来优化代码结构，一个节点的行为其实完全是由其身份驱动的，比如节点状态为INITIAL（初始的）那么他就会进入选举，如果一个节点是CANDIDATE（候选）那么他会去向其他节点拉票，如果节点是LEADER（领导）那么他会去定时发送心跳，如果节点状态时Follower（跟随者）那么节点会开启心跳检测，保证Leader存活。

**定义一个枚举身份类**

```
public enum NodeStatus {
//  领导
    LEADER,
//  跟随者
    FOLLOWER,
//  候选者
    CANDIDATE,
//  初始(系统初始化或者失去Leader时的状态)
    INITIAL;
}
```

**定义一个节点状态类，里面包含了节点和集群的基本消息，以及一个观察者，在改变Status的同时就会触发观察者的方法，从而完成相关行为**

```
public class NodeContent {
//  本节点地址
    private String address;

//  节点状态
    private volatile NodeStatus nodeStatus;

//  选期id
    private Integer termId;

//  当前Leader
    private String leaderAddress;

//  集群节点数量
    private Integer clusterNodeAmount;

//  当前选期的选票数
    private AtomicInteger ballot;

//  状态监听器
    private NodeStatusListener nodeStatusListener;

//  是否已经给其他节点进行了投票
    private volatile Boolean voted;

//  Leader心跳，用于检测Leader是否存活
    private HeartBeat leaderHeartBeat;

    public void setNodeStatus(NodeStatus nodeStatus) {
        log.info("我现在的身份是"+nodeStatus);
        this.nodeStatus = nodeStatus;
        if(nodeStatus.equals(NodeStatus.INITIAL)){
            nodeStatusListener.onBecomingInception();
        }else if(nodeStatus.equals(NodeStatus.LEADER)){
            nodeStatusListener.onBecomingLeader();
        }else if(nodeStatus.equals(NodeStatus.FOLLOWER)){
            nodeStatusListener.onBecomingFollower();
        }else if(nodeStatus.equals(NodeStatus.CANDIDATE)){
            nodeStatusListener.onBecomingCandidate();
        }
    }
}
```

**然后我们定义一个观察者类,我这里给出大概的方法，然后逐一进行分析**

```
 @Resource
    @Lazy
    ClusterService clusterService;

    @Resource
    @Lazy
    NodeContent nodeContent;

    @Resource
    RpcConfigProperties.RpcRegistry registry;


//  检测到成为Leader，首先需要向其他节点发送Leader身份确认，然后进行日志记录同步，同时开启心跳发送
    public void onBecomingLeader(){

    }

//  检测到成为Follower，开启监听心跳
    public void onBecomingFollower(){
    }

//   检测到状态为初始化 ，直接进入选举流程
    public void onBecomingInception() {

    }

    /**
     * 成为候选者后先给自己投票，然后向其他节点发起投票请求
     */
    public void onBecomingCandidate() {
      
    }
	/**
	*添加一次投票
	*/
    public void incrBallot(String voteId){
        clusterService.incrResponseCount(voteId);
    }
```

---

#### 系统初始化，节点相互连接

```
 public void initCluster(){
        threadPoolTaskExecutor.execute(()->{
            List<String> cluster = rpcRegistry.getClusterAddress();
            for (String nodeAddress : cluster) {
                String host = nodeAddress.split(":")[0];
                int port = Integer.parseInt(nodeAddress.split(":")[1]);
                if (rpcRegistry.getHost().equals(host)&& rpcRegistry.getPort().equals(port)) {
                    continue;
                }
                ChannelFuture connect = null;
                while(connect == null){
                    try {
                        connect = ConnectUtil.connect(host, port,bootstrap,clusterConnectCache);
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                        log.error("集群节点："+host+":"+port+"重连中");
                    }
                }
                log.info("节点："+host+"："+port +"连接成功！");
            }
            log.info("节点全部连接成功!开始初始化集群");
            nodeContent.setNodeStatus(NodeStatus.INITIAL);
        });
    }
```

流程：通过配置类配置的集群节点进行连接就行，没有什么技术难点。ConnectUtil.connect(host, port,bootstrap,clusterConnectCache)这是一个连接封装类，使用了一个连接缓存，并且指定bootstrap，远程的地址。当连接完成后，则将节点状态设置为初始化，会触发观察者的行为然后进入选举过程

---

#### 首先来看开始选举过程

```
 public void onBecomingInception() {
//      清空上一次选举的状态
        nodeContent.setBallot(new AtomicInteger(0));
        nodeContent.setVoted(false);
//      只要节点状态不是leader或者不是follower 则不断进行选举
        while (!nodeContent.getNodeStatus().equals(NodeStatus.FOLLOWER)||!nodeContent.getNodeStatus().equals(NodeStatus.LEADER)){
            log.info("开始本轮选举，当前选期id="+nodeContent.getTermId());
            if(nodeContent.getNodeStatus().equals(NodeStatus.LEADER)||nodeContent.getNodeStatus().equals(NodeStatus.FOLLOWER)){
                log.info("选举结束");
                return;
            }
            Random random = new Random();
//          随机睡眠时间，不超过最大规定时间
            long randomSleepTime = Math.abs(random.nextLong()) % maxSleepTime;
            log.info("随机睡眠"+randomSleepTime+"ms");
            try {
                Thread.sleep(randomSleepTime);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
//          如果已经给其他节点投过票了，则不参与选举
            if(nodeContent.getVoted()){
                log.info("已经给其他节点投过票了,退出本轮选举");
                return;
            }
//          如果睡眠结束后仍然没有进行投票则转为候选者并发起投票
            nodeContent.setNodeStatus(NodeStatus.CANDIDATE);
        }
        log.info("选举结束，当前集群节点的Leader是："+nodeContent.getLeaderAddress());
    }
```

**细节分析**

1. While循环是有必要的，因为选举结束的条件是集群中有Leader才会结束，而产生Leader节点的条件是获得了绝大多数节点的投票，我通过计算发现从发送投票到收集投票的过程大概是100ms，如果有多个节点同时在100ms内被唤醒然后去拉票，情况往往都是多个节点都拉不到票（因为唤醒后第一件事是给自己投票，而投过票的是不能在给其他节点投票的），所以不能保证一次选举就一定能选举出Leader（这也是为什么需要随机睡眠来避免这种情况）。

2. 整个方法需要使用线程池来执行，这样避免当线程在休眠的时候无法处理其他的拉票请求从而错过投票
3.  nodeContent.setNodeStatus(NodeStatus.CANDIDATE); 这一行代码会触发拉票过程，虽然发送拉票请求是异步的，但是需要同步接受结果，注意这里有个大坑，如果你没有同步等待所有投票结束，那么while进入下一个循环，选期Id++（在最后发起拉票时会触发），会导致上一轮的投票接受时发现现在已经是第二轮的投票了，根据raft安全性规定这些选票将被丢弃，所以导致投票的速度更不上你循环进入下一轮的速度，所以要同步阻塞到绝大多数的投票结果过来才能进入下一轮

---

#### 拉票过程

拉票请求类，生成一个随机id

```
public class VoteRequest implements Serializable {
    // 标识id
    private String voteId;

    //  拉票的发起人
    private String initiator;

    //  选期id
    private int termId;

    //  发起时间
    private Date createTime;

    public VoteRequest(String initiator, int termId){
        this.initiator = initiator;
        this.termId = termId;
        this.createTime = new Date();
        long time = System.currentTimeMillis();
        int random = (int) (Math.random() * Integer.MAX_VALUE);
        UUID uuid = new UUID(time, random);
        this.voteId = uuid.toString();

    }
}
```

投票响应类，需要注意的是这里的voteId需要与上面的VoteRequest的id一致，这样方便收集投票结果数量

```
@Data
public class VoteResponse implements Serializable {
    private String voteId;

    //  响应者
    private String responder;

    //  选期id
    private int termId;

    //  发起时间
    private Date createTime;

    private Boolean vote;

    public VoteResponse(String responder, int termId, String voteId, Boolean vote){
        this.responder = responder;
        this.termId = termId;
        this.vote = vote;
        this.createTime = new Date();
        this.voteId = voteId;
    }
}
```

**开始拉票，将自己的选期Id++，这里并不是主要的代码，主要的业务是调用了askVote方法，然后再看askVote方法**

```
/**
 * 成为候选者后先给自己投票，然后向其他节点发起投票请求
 */
public void onBecomingCandidate() {
    nodeContent.setTermId(nodeContent.getTermId()+1);
    nodeContent.setVoted(true);
    nodeContent.getBallot().incrementAndGet();
    log.info("我要去选举");
    clusterService.askVote();
}
```

askVote方法

```
 @Override
    public void askVote() {
        VoteRequest voteRequest = new VoteRequest(rpcRegistry.host+":"+rpcRegistry.port,nodeContent.getTermId());
        RequestHeader requestHeader = new RequestHeader(RequestType.ASK_VOTE,true);
        RpcRequestHolder rpcRequestHolder = new RpcRequestHolder(requestHeader,voteRequest);
        sendDataToOtherNode(rpcRequestHolder);
        log.info("开始同步阻塞等待本轮投票结束");
        syncWaitResponse(voteRequest.getVoteId(),1000l,nodeContent.getClusterNodeAmount()-1);
        log.info("本轮投票结束，当前票数"+nodeContent.getBallot());
        //       当前的选票数量大于集群节点数量/2+1，则身份变为Leader
        if(nodeContent.getBallot().get()>=(nodeContent.getClusterNodeAmount()/2+1)){
            nodeContent.setNodeStatus(NodeStatus.LEADER);
        }
    }
```

**细节分析**：

requestHeader 我是定义的一个类似于请求头，而RpcRequestHolder是封装的同一请求类，sendDataToOtherNode是封装的向其他节点进行数据发送方法，

这些都不重要，主要在于syncWaitResponse（）这个同步等待结果

---

#### 同步等待结果

**第一个是选票的id，同一次的拉票请求下所有的投票id都是相同的，第二个是超时时间，如果超时则不再统计投票结果，第三个是期待数量，如果返回结果的数量>=我所期待的数量，那么则直接返回，方法并不需要返回值，投票的数量在方法执行后再统计就好了。整个方法的目的就是阻塞直到投票响应结束，根据投票数判断是否开启下一轮选举**，

```
private final Lock lock = new ReentrantLock();

private final Condition condition = lock.newCondition();

private void syncWaitResponse(String id, long timeout, int expectNum){
        long start = System.currentTimeMillis();
        while (true) {
            AtomicInteger count = responseCount.get(id);
            if (count != null && count.get() >= expectNum) {
                log.info("同步结束，耗时:"+(System.currentTimeMillis() - start));
                return;
            }
            lock.lock();
            try {
                if (!condition.await(timeout, TimeUnit.MILLISECONDS)) {
                    log.info("同步结束，耗时超时:"+(System.currentTimeMillis() - start));
                    return;
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            finally {
                lock.unlock();
            }
            if (System.currentTimeMillis() - start >= timeout) {
                return;
            }
        }
    }
```

----

#### 对其他节点的拉票请求接受

```

private static final Semaphore vote = new Semaphore(1);

@Override
    public RpcRequestHolder vote(VoteRequest voteRequest) {
        int termId = voteRequest.getTermId();
        RequestHeader header = new RequestHeader(RequestType.VOTE);
//       对于选期不大于当前选期的投票请求拒绝,对于当前身份已经是Leader拒绝，对于已为其他节点投票进行拒绝
        if(nodeContent.getTermId()>termId||nodeContent.getNodeStatus().equals(NodeStatus.LEADER)||nodeContent.getVoted()){
            VoteResponse voteResponse = new VoteResponse(nodeContent.getAddress(),termId,voteRequest.getVoteId(), Boolean.FALSE);
            return new RpcRequestHolder(header,voteResponse);
        }else {
            //      进行并发控制。
            try {
                vote.acquire();
                log.info("我收到了来自"+voteRequest.getInitiator()+"的拉票,我为他投票");
                VoteResponse voteResponse = new VoteResponse(nodeContent.getAddress(),termId,voteRequest.getVoteId(),Boolean.TRUE);
                nodeContent.setVoted(true);
                return new RpcRequestHolder(header,voteResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }finally {
                vote.release();
            }
        }
    }
```

实现细节方面主要是安全性保证，比如说有没有给其他节点投票，或者是投票的选期id是不是小于本节点的选期id等等，这些情况下投票时无效的。然后就是并发的控制，避免多个拉票请求同时到达然后发出多个投票。

---

#### 对投票结果进行接收

当对方收到拉票请求后会返回VoteResponse ，完成VoteResponse的接收就是最后一环了

```
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
```

实现细节：仍然是安全性保证，对投票结果的几种情况进行判断，若有效则增加获得的选票数量。调用观察者的添加选票方法incrBallot（）传入选票的id 这个操作并不需要判断投票的有效性，因为他只是个统计结果数量的方法。

```
public void incrBallot(String voteId){
        clusterService.incrResponseCount(voteId);
}
```

调用service的incrResponseCount方法。

```
private final ConcurrentHashMap<String, AtomicInteger> responseCount = new ConcurrentHashMap<>();

private final Lock lock = new ReentrantLock();

private final Condition condition = lock.newCondition();

@Override
    public void incrResponseCount(String voteId){
        responseCount.compute(voteId, (key, value) -> {
            if (value == null) {
                return new AtomicInteger(1);
            } else {
                value.incrementAndGet();
                return value;
            }
        });
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
```

这里是响应操作，也就是和之前的同步等待投票结果是相应的，一个等待，一个通知，而这里则是通知。



**这样就完成了一整个Raft选举过程，除此之外还有日志同步等等操作，项目目前还没有实现到这一步，希望这对于你有帮助,如果需要全部代码，你可以进入项目地址的registry模块下的cluster包查看所有代码**

