package io.xzw.xzwrpc.stub.net.common;

import io.netty.channel.Channel;
import io.xzw.xzwrpc.stub.net.client.ClientHandler;
import io.xzw.xzwrpc.stub.net.params.RpcRequest;

/**
 * @author xzw
 * 连接channel管理器，负责维护连接
 */
public abstract class ConnectServer {

    protected Channel channel;

    protected String host;

    protected Integer port;

    /**
     * 初始化方法
     * @param address 服务端地址
     * @param clientHandler 客户端处理器
     */
    public abstract void init(String address, ClientHandler clientHandler);

    /**
     * 关闭服务器
     */
    public abstract void close();


    /**
     * 判断客户端连接活性
     * @return 判断客户端连接是否有效
     */
    public abstract boolean isValid();

    /**
     * 异步发送消息
     * @param request 请求包
     */
    public abstract void sendAsync(RpcRequest request);
    /**
     * 同步发送消息
     * @param request 请求包
     */
    public abstract void send(RpcRequest request);


    /**
     * 重写toString方法
     * @return 重写string打印方法
     */
    @Override
    public abstract String toString();


    /**
     * 清理静态资源
     */
    public abstract void cleanStaticResource();
}
