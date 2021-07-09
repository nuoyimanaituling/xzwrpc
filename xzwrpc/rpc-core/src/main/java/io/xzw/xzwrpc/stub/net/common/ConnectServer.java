package io.xzw.xzwrpc.stub.net.common;

import io.netty.channel.Channel;
import io.xzw.xzwrpc.stub.net.client.ClientHandler;
import io.xzw.xzwrpc.stub.net.params.RpcRequest;


/**
 * 连接channel管理器，负责维护连接
 */
public abstract class ConnectServer {

    protected Channel channel;

    protected String host;

    protected Integer port;

    public abstract void init(String address, ClientHandler clientHandler);

    public abstract void close();

    public abstract boolean isValid();

    // 异步发送消息
    public abstract void sendAsync(RpcRequest request);
    // 同步发送消息
    public abstract void send(RpcRequest request);

    public abstract String toString();

    public abstract void cleanStaticResource();
}
