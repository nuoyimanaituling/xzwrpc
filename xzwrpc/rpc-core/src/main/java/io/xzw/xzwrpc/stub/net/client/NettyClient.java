package io.xzw.xzwrpc.stub.net.client;


import io.xzw.xzwrpc.stub.net.Client;
import io.xzw.xzwrpc.stub.net.common.ConnectServer;
import io.xzw.xzwrpc.stub.net.params.RpcFutureResp;
import io.xzw.xzwrpc.stub.net.params.RpcRequest;

import java.util.Map;

public class NettyClient implements Client {
    private final ConnectServer connectServer;
    private final Map<String,RpcFutureResp> respPool;
    public NettyClient(ConnectServer connectServer, Map<String, RpcFutureResp> respPool) {
        this.connectServer = connectServer;
        this.respPool = respPool;
    }
    @Override
    public RpcFutureResp send(RpcRequest request) {
        RpcFutureResp resp =new RpcFutureResp();
        // respPool存放的是发送请求的异步返回的结果，这个respPool最后也会作为参数，传递给clientHandler
        this.respPool.put(request.getRequestId(),resp);
        /**
         * 异步发送，发送之后直接返回结果
         */
        connectServer.sendAsync(request);
        return resp;
    }
    @Override
    public void cleanAfterInvoke(RpcRequest request) {

        // 收到响应后，清除缓存列表
        this.respPool.remove(request.getRequestId());
    }
}
