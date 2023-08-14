## EASY_RPC 一款轻量，全面，一体化远程调用框架

​	EASY_RPC  使用 netty  高可用的主从Reactor多线程模型 搭建服务器，底层使用NIO减少连接时产生的资源损耗。其内部包含熔断器，负载均衡器，注册中心等等模块，可由用户手动自定义配置。

​

## EASY_RPC 架构说明（以下为主要部分）

- easy_rpc_consumer： 消费者模块
    - consumer->Consumer Processor：消费者主启动类，包含注解扫描，服务获取
    - consumer->ConsumerProxy：消费者代理对象，触发消费请求发送
    - net Server->Consumer Handler： 消费者netty处理类，负责注册中心，服务提供者数据接受
    - service-> Consumer Service： 消费者服务类，具体执行服务列表拉取，服务请求方法
- easy_rpc_provider: 服务提供者模块
    - provider-> ProviderProcessor: 提供者主要进行，开启服务器，服务列表的注册
    - provider-> ProviderFinder：服务提供者扫描类，负责扫描提供者注解，生成服务元信息
    - service ->ProviderSerivce： 业务执行具体类，负责如消费处理，列表注册 。
    - nettyServer -> NettyServerStarter： 开启服务器
    - nettyServer -> ProviderHandler： 服务器处理器，用于处理消费请求，调用服务类执行具体方法
- easy_rpc_service: 注册中心
    - nettyServer -> NettyServiceStater: 注册中心 服务器启动类
    - nettyServer -> NettyHandlerinit： 服务器处理器，负责服务列表保存，拉取。
- easy_rpc_domain: 公共模块
    - protocol： 协议包，包含netty的编码和解码，请求类型的枚举
    - rpc： 传输包，定义类传输类及其结构，其传输载体为RpcRequestHolder,包含一个CommonHeader 以及 data  规范为 比如提供者响应包，服务调用者请求包，服务列表发送包。
    - serialization：序列化包，包含一个序列化工厂，以及序列化接口和实现类

​







## RPC框架开发整理

### 整体架构

- 提供者：
    - 实现向注册中心提交服务列表
    - 实现向消费者响应服务消费
    - 实现向注册中心保持在线状态-心跳检测
- 消费者
    - 实现向注册中心获取服务列表并本地缓存
    - 实现向服务提供者提交消费请求
    - 实现熔断器，对不可用服务进行熔断
- 注册中心
    - 实现保存服务提供者的服务列表提交
    - 实现消费者获取服务列表的请求
    - 定期检查 服务列表是否过期，进行服务剔除
    - 服务剔除后主动与消费者提供服务列表更新（两种思路实现）