package io.xzw.xzwrpc.stub.provider.invoke;

import io.xzw.xzwrpc.exception.XzwRpcException;
import io.xzw.xzwrpc.stub.net.NetConstant;
import io.xzw.xzwrpc.stub.net.common.ProviderInvokerCenter;
import io.xzw.xzwrpc.stub.net.params.RpcRequest;
import io.xzw.xzwrpc.stub.net.params.RpcResponse;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ProviderInvoker extends ProviderInvokerCenter {
    @Override
    public Object invoke(RpcRequest req) throws Exception {
        Object proxy = ProviderProxyPool.getInstance().getProxy(req.getClassType());
        if(proxy ==null){
            throw new XzwRpcException("can not find service["+req.getClass().getName()+"] proxy object " );
        }
        // 获取参数数组 （转换参数类型）
        Class<?>[] classesType = Arrays.stream(req.getParams()).map(Object::getClass).toArray(Class[]::new);
        Method method =proxy.getClass().getMethod(req.getMethodName(),classesType);
        return method.invoke(proxy,req.getParams());
    }
    @Override
    // 针对请求，调用代理类反射调用，然后进行返回
    public RpcResponse requestComingBellRing(RpcRequest req) {
        RpcResponse.RpcResponseBuilder builder =RpcResponse.builder().requestId(req.getRequestId());
        try{
            Object res =invoke(req);
            builder.data(res).code(0).msg("success");
        }
        catch (Exception e){
            builder.data(null).code(-1).msg(e.getMessage()).exception(e);
            e.printStackTrace();
        }
        return builder.build();
    }

    @Override
    // 涉及到心跳检测，还有状态检测
    public boolean valid(RpcRequest req) {
        /**
         * 客户端发来后的请求信息校验
         */
        return (req.getRequestId().startsWith(NetConstant.HEART_BEAT_REQ_ID) || req.getRequestId().startsWith(NetConstant.IDLE_CHANNEL_CLOSE_REQ_ID))
        || (req.getCreateTimeMills() +req.getTimeout()>=System.currentTimeMillis() && req.getRequestId()!=null
        && req.getRequestId().length()!=0 && req.getClassName()!=null && req.getClassName().length()!=0
        && req.getMethodName()!=null && req.getMethodName().length()!=0 && req.getClassType()!=null
        && req.getParams()!=null);
    }
}
