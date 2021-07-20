package io.xzw.xzwrpc.stub.net;


import io.xzw.xzwrpc.stub.net.params.FutureResp;
import io.xzw.xzwrpc.stub.net.params.RpcRequest;

/**
 *
 * 定义客户端的基础类
 * @author xzw
 */
public interface Client {

    /**
     * 发送信息
     * @param request 请求消息
     * @return 异步返回消息
     */

    FutureResp send(RpcRequest request);

    /**
     * 调用以后进行清除资源的动作
     * @param request 请求消息
     */
    void cleanAfterInvoke(RpcRequest request);
}
