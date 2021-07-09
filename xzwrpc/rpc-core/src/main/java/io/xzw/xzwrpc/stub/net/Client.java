package io.xzw.xzwrpc.stub.net;


import io.xzw.xzwrpc.stub.net.params.FutureResp;
import io.xzw.xzwrpc.stub.net.params.RpcRequest;

/**
 *
 * 定义客户端的基础类
 */
public interface Client {

    // 发送信息
    FutureResp send(RpcRequest request);

    // 调用以后进行清除资源的动作
    void cleanAfterInvoke(RpcRequest request);
}
