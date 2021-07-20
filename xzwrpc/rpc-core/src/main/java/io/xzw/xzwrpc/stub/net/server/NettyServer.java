package io.xzw.xzwrpc.stub.net.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.xzw.xzwrpc.serializer.RpcSerializer;
import io.xzw.xzwrpc.stub.net.codec.NettyDecoder;
import io.xzw.xzwrpc.stub.net.codec.NettyEncoder;
import io.xzw.xzwrpc.stub.net.common.ProviderInvokerCenter;
import io.xzw.xzwrpc.stub.net.params.RpcRequest;
import io.xzw.xzwrpc.stub.net.params.RpcResponse;
import io.xzw.xzwrpc.stub.provider.invoke.ProviderProxyPool;
import io.xzw.xzwrpc.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xzw
 */
@Slf4j
public class NettyServer {

    /**
     * 端口号
     */
    private final int port;

    /**
     * 序列化器
     */
    private final RpcSerializer serializer;

    /**
     * 设置channelGroup 存放多客户端
     */
    private final ChannelGroup channels;

    /**
     * 请求处理线程池
     */
    private EventLoopGroup bossGroup;

    /**
     * 连接处理线程池
     */
    private EventLoopGroup workerGroup;

    /**
     * 设置异步结果
     */
    private ChannelFuture channelFuture;
    /**
     * 设置全局请求id(递增)
     */
    private final AtomicInteger globalReqNums;

    public NettyServer(Integer port,RpcSerializer serializer){
        this.port =port;
        this.serializer =serializer;
        // 根据全局的eventExecutor来对组里的channel进行通知
        this.channels =new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        // 用来判断线程池是不是还有请求没有处理完
        this.globalReqNums =new AtomicInteger();
    }

    /**
     * NettyServer启动器
     * @param invokerCore 服务端调用中心
     */
    public void run(ProviderInvokerCenter invokerCore){
        int core =Runtime.getRuntime().availableProcessors();
        this.bossGroup =new NioEventLoopGroup(1,new DefaultThreadFactory("boss"));
        this.workerGroup =new NioEventLoopGroup(core,new DefaultThreadFactory("worker"));
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.TCP_NODELAY,true);
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new NettyDecoder(RpcRequest.class, serializer))
                                    .addLast(new NettyEncoder(RpcResponse.class, serializer))
                                    .addLast(new ServerHandler(invokerCore, channels, globalReqNums));
                        }
                    });

            channelFuture = serverBootstrap.bind(port).sync();
            log.info("xzw-rpc server start is successing  and listen for port " + this.port);
            // 开启服务端状态：即SERVER_STATUS 为true;
            ServerHandler.serverHandlerOpen();
            channelFuture.channel().closeFuture().sync();

        }
        catch (InterruptedException e){
            e.printStackTrace();
            log.error("thread of xzw-rpc server is interrupted, perhaps rpc server stopped working.");
        }finally {
            close();
        }
    }


    /**
     * 关闭当前server，做一些资源清理工作：
     */
    public void  close(){
       ServerHandler.serverHandlerClose();
        // 判断是不是还有请求没有处理完
       if(this.globalReqNums.get() > 0){
           try {
               TimeUnit.SECONDS.sleep(10);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
       this.channels.close();
        ThreadPoolUtil.shutdownExistsPools();
        ProviderProxyPool.getInstance().cleanPool();

        if(bossGroup!=null){
            this.bossGroup.shutdownGracefully();
        }
        if(workerGroup!=null){
            this.workerGroup.shutdownGracefully();
        }
        log.info("xzw-rpc netty server has been closed");
    }
    public boolean isValid(){
        return this.channelFuture != null &&this.channelFuture.channel().isActive();
    }

    @Override
    public String toString() {
        return "rpc netty server for port[" + this.port + "]";
    }






}
