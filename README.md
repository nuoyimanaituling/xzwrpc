# 开发者手册

## 如何编译源代码：

```
maven clean package
```

## 启动流程

1. 启动Zookeeper：推荐3.6+版本，没有启动zookeeper则服务无法注册，将报错
2.   [xzwrpc](https://github.com/nuoyimanaituling/xzwrpc/tree/v3)/[xzwrpc](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc)/[rpc-test](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test)/[rpc-test-demo](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo)/[src](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo/src)/[main](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo/src/main)/[java](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo/src/main/java)/[io](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo/src/main/java/io)/[xzw](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo/src/main/java/io/xzw)/[xzwrpc](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo/src/main/java/io/xzw/xzwrpc)/[basic](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo/src/main/java/io/xzw/xzwrpc/basic)/**ServerTestMain.java** 下启动服务器
3. 在[xzwrpc](https://github.com/nuoyimanaituling/xzwrpc/tree/v3)/[xzwrpc](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc)/[rpc-test](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test)/[rpc-test-demo](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo)/[src](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo/src)/[main](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo/src/main)/[java](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo/src/main/java)/[io](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo/src/main/java/io)/[xzw](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo/src/main/java/io/xzw)/[xzwrpc](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo/src/main/java/io/xzw/xzwrpc)/[basic](https://github.com/nuoyimanaituling/xzwrpc/tree/v3/xzwrpc/rpc-test/rpc-test-demo/src/main/java/io/xzw/xzwrpc/basic)/**TestAppMain.java** 下启动客户端

**启动服务端成功后**

![image-20210715210547093](https://github.com/nuoyimanaituling/xzwrpc/blob/comment_version/image/1.png)

**启动客户端得到测试结果**

![image-20210715210725001](https://github.com/nuoyimanaituling/xzwrpc/blob/comment_version/image/2.png)

#  XzwRpc核心模块架构图

## 服务端核心架构设计

![image-20210715211145273](https://github.com/nuoyimanaituling/xzwrpc/blob/comment_version/image/3.png)

## 客户端核心架构设计

![image-20210715211200776](https://github.com/nuoyimanaituling/xzwrpc/blob/comment_version/image/5.png)

##  rpc-core模块实现说明

| 模块名     | 子模块名                           | 中文名             | 说明                                                         |
| ---------- | ---------------------------------- | ------------------ | ------------------------------------------------------------ |
| exception  |                                    | 异常管理模块       | 自定义异常处理类                                             |
| spi        |                                    | spi扩展            | 定义java spi 的类加载机制实现类，实现了spi 可拓展的过滤链和路由策略 |
| router     |                                    | 过滤器与路由管理   | 定义**filter**抽象接口和**router**抽象接口(spi扩展实现)，定义负载均衡器 |
| serializer |                                    | 网络通信序列化协议 | 抽象序列化器，实现类基于hessian的序列化，还有默认jdk的序列化 |
| util       |                                    | 常用辅助工具包     | 定义常用地工具类                                             |
| register   |                                    | zk注册中心         | 客户端服务发现与服务端注册抽象                               |
| Stub       | （common，invoker，net，provider） | 服务调用管理       | 核心逻辑管理                                                 |
| common     |                                    | 客户端管理         | 客户端连接管理工具包                                         |
| invoker    |                                    | 客户端本地代理调用 | 客户端实现本地代理调用                                       |
| net        |                                    | 网络通信层         | 网络通信管理                                                 |
| provider   |                                    | 服务端本地调用     | 服务单实现本地调用                                           |

