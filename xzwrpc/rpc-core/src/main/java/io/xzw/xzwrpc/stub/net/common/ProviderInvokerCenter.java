package io.xzw.xzwrpc.stub.net.common;

import io.xzw.xzwrpc.stub.net.params.RpcRequest;
import io.xzw.xzwrpc.stub.net.params.RpcResponse;

/**
 * 服务端调用核型抽象
 * @author xzw
 */
public abstract class ProviderInvokerCenter {

    /**
     * 反射调用代理请求的方法
     * @param req 请求包
     * @return 反射调用返回对象
     * @throws Exception
     */
    public abstract Object invoke(RpcRequest req) throws Exception;

    /**
     * 如果出现消息了,消息环回通知，一直循环检测是否有请求消息
     * @param req 请求包
     * @return 响应包
     */
    public abstract RpcResponse requestComingBellRing(RpcRequest req);


    /**
     * 检测是否有效
     * @param req 请求包
     * @return  检测是否有活性
     */
    public abstract boolean valid(RpcRequest req);






}
