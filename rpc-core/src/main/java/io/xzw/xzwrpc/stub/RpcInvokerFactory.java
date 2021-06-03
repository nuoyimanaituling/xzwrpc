package io.xzw.xzwrpc.stub;


import com.alibaba.fastjson.parser.ParserConfig;
import io.xzw.xzwrpc.api.RpcCgLibInvokeHandler;

// 使用cglib实现动态代理
public class RpcInvokerFactory {


    static {
        ParserConfig.getGlobalInstance().addAccept("io.xzw");
    }
    private  RpcCgLibInvokeHandler handler;

    public RpcInvokerFactory(RpcCgLibInvokeHandler invokeHandler) {
        this.handler =invokeHandler;
    }

    public <T> T createStub(Class<T> clazz ){
        return this.handler.create(clazz);
    }
}
