## EASY_RPC 一款轻量，全面，一体化远程调用框架

​	EASY_RPC  使用 netty  高可用的主从Reactor多线程模型 搭建服务器，底层使用NIO减少连接时产生的资源损耗。其内部包含熔断器，负载均衡器，注册中心等等模块，可由用户手动自定义配置。



## 快速开始

#### 创建一个注册中心

	* **引入依赖:**

``` 
<dependency>
            <groupId>io.github.scarecrowQoQ</groupId>
            <artifactId>easy_rpc_registry</artifactId>
            <version>0.0.2-release</version>
</dependency>
```

**创建一个Maven项目，这里选择springboot模板**

![](.\ReadMe_img\image-20230817214028719.png)

**在启动类上开启注册中心服务**

```
@SpringBootApplication
@EnableEasyRPC
public class ServerDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerDemoApplication.class, args);
    }
}
```

* **编写配置文件（以yaml文件为例）**

```
rpc:
  registry:
    port: 8344
    host: 127.0.0.1
```

* 然后点击运行即可（如下即开启成功）

  ```
  2023-10-09 13:10:26.720  INFO 532 --- [           main] c.r.e.EasyRpcCoreApplication             : No active profile set, falling back to 1 default profile: "default"
  2023-10-09 13:10:27.684  INFO 532 --- [           main] c.r.e.EasyRpcCoreApplication             : Started EasyRpcCoreApplication in 1.436 seconds (JVM running for 2.356)
  2023-10-09 13:10:28.532  INFO 532 --- [         task-1] c.r.e.nettyServer.NettyServiceStart      : 开启netty服务:/0:0:0:0:0:0:0:0:8344
  ```

  

#### 创建一个API模块

![image-20231010172508397](.\ReadMe_img\image-20231010172508397.png)

* **编写一个测试接口**

* ```
  public interface UserOrder {
      public String getOrder();
  }
  ```



#### 创建一个服务提供者,依然是一个SpringBoot项目

![image-20231010172639353](.\ReadMe_img\image-20231010172639353.png)

 - **引入依赖**

- ```
  <dependency>
      <groupId>io.github.scarecrowQoQ</groupId>
      <artifactId>easy_rpc_provider</artifactId>
      <version>0.0.2-release</version>
  </dependency>
  ```

 * **在SpringBoot启动类上开启服务提供机制**

* ```
  @SpringBootApplication
  @EnableRpcProvider
  public class ProviderDemoApplication {
      public static void main(String[] args) {
          SpringApplication.run(ProviderDemoApplication.class, args);
      }
  }
  ```

 * 创建一个服务实现类，给定服务名，完成创建

   ```
   @EasyRpcProvider(serviceName = "userOrder")
   @Service
   public class OrderService implements UserOrder {
       @Override
       public String getOrder() {
           return "返回：来自服务1的订单处理";
       }
   }
   ```

#### 创建服务消费者

![image-20231010172721274](.\ReadMe_img\image-20231010172721274.png)

* **引入依赖**

* ```
  <dependency>
      <groupId>io.github.scarecrowQoQ</groupId>
      <artifactId>easy_rpc_consumer</artifactId>
      <version>0.0.2-release</version>
  </dependency>
  
  ```



  * **在SpringBoot启动类上开启服务消费功能**

* ```
  @SpringBootApplication
  @EnableRpcConsumer
  public class RpcConsumerDemoApplication {
      public static void main(String[] args) {
          SpringApplication.run(RpcConsumerDemoApplication.class, args);
      }
  }
  ```



* **在消费端创建服务调用类，注入接口，添加服务名.**

* ```
  @Service
  public class UserServiceImpl implements UserService {
      @RpcConsumer(serviceName = "userOrder")
      UserOrder userOrder;
  
      @Override
      public String getUserOrder() {
          return userOrder.getOrder();
      }
  }
  
  ```

这里示例为了方便展示，使用HTTP调用消费者触发远程服务调用，添加一个web接口

```
@RestController
public class UserController {
    @Resource
    UserService userService;
    
    @RequestMapping("/order")
    public String getUserOrder(){
        return userService.getUserOrder();
    }
}
```

接下来访问：[localhost:8080/order](http://localhost:8080/order)

成功显示![image-20231010172853254](.\ReadMe_img\image-20231010172853254.png)

####  这样就完成一次简单的远程服务调用



### RPC服务治理

---

**项目提供了多样的服务治理，包含负载均衡，服务熔断，服务限流，服务分组，自定义过滤，拦截器等等**

1. 负载均衡：

​	





## EASY_RPC 架构说明（以下为主要部分）

- **easy_rpc_consumer： 消费者模块**

    - consumer->Consumer Processor：消费者主启动类，包含注解扫描，服务获取
    - consumer->ConsumerProxy：消费者代理对象，触发消费请求发送
    - net Server->Consumer Handler： 消费者netty处理类，负责注册中心，服务提供者数据接受
    - service-> Consumer Service： 消费者服务类，具体执行服务列表拉取，服务请求方法

- **easy_rpc_provider: 服务提供者模块**

    - provider-> ProviderProcessor: 提供者主要进行，开启服务器，服务列表的注册
    - provider-> ProviderFinder：服务提供者扫描类，负责扫描提供者注解，生成服务元信息
    - service ->ProviderSerivce： 业务执行具体类，负责如消费处理，列表注册 。
    - nettyServer -> NettyServerStarter： 开启服务器
    - nettyServer -> ProviderHandler： 服务器处理器，用于处理消费请求，调用服务类执行具体方法

- **easy_rpc_service: 注册中心**

    - nettyServer -> NettyServiceStater: 注册中心 服务器启动类
    - nettyServer -> NettyHandlerinit： 服务器处理器，负责服务列表保存，拉取。

- **easy_rpc_domain: 公共模块**

    - bean: 公共使用的对象，如服务元数据信息，心跳，请求载体
    - annotation： 公共使用的注解。
    - enumeration：公共使用的枚举类

- **easy_rpc_govern: 服务治理模块**

    - config 包：包含一个rpc框架的配置类
    - context 包：关于rpc 过程调用的上下文和处理管道
    - filter 包：包含rpc请求和响应的过滤处理
    - fuse 包： 包含rpc 熔断 处理
    - interceptor： 包含 rpc 请求和响应拦截处理
    - limit 包：包含rpc 调用限流处理
    - loadBalancer 包：包含服务负载均衡处理
    - route 包：包含服务选择路由处理
    - utils 包：一个spring的工具类
    - thread 包：包含rpc线程池

- easy_rpc_protocol: 协议模块

    - 主要关于编码与解码，调用协议选择（待完善）

    



## RPC框架开发整理

### 主要涉及的功能实现

- 提供者：
    - 实现向注册中心提交服务列表
    - 实现向消费者响应服务消费
    - 实现向注册中心保持在线状态-心跳检测
    
- 消费者
    - 实现向注册中心获取服务列表并本地缓存
    
    - 实现向服务提供者提交消费请求
    
    - 实现熔断器，对不可用服务进行熔断
    
    - 实现服务限流
    
    - 实现服务路由
    
      
    
- 注册中心
    - 实现保存服务提供者的服务列表提交
    - 实现消费者获取服务列表的请求
    - 定期检查 服务列表是否过期，进行服务剔除
    - 服务剔除后主动与消费者提供服务列表更新
    - 在心跳的基础上检查服务列表是否上传