package io.xzw.xzwrpc.stub.invoker.component;


import io.xzw.xzwrpc.serializer.RpcSerializer;
import io.xzw.xzwrpc.stub.net.Client;
import io.xzw.xzwrpc.stub.net.common.ClientInvokerCenter;

import java.util.List;

/**
 * 客户端服务调用单元
 */
public class InvokerClientCenter {
    private final Class<? extends RpcSerializer> serializerClazz;
    /**
     * 传入类型是因为clientClazz有子类,根据子类型反射创建clientCore
     */
    private final Class<? extends ClientInvokerCenter> clientClazz;

    private ClientInvokerCenter clientCore;

    public InvokerClientCenter(Class<? extends RpcSerializer> serializerClazz, Class<? extends ClientInvokerCenter> clientClazz )
    {
        this.serializerClazz =serializerClazz;
        this.clientClazz =clientClazz;
    }

    public void afterSetProperties() throws Exception{
        this.clientCore =clientClazz.newInstance();
        RpcSerializer serializer = serializerClazz.newInstance();
        this.clientCore.setSerializer(serializer);
    }

    public Client getInvokeClient(String addr){
        return this.clientCore.getClient(addr);
    }

    public ClientInvokerCenter getClientCore(){
        return  this.clientCore;
    }

    public List<String> filterSubHealth(List<String> urls){
        return this.clientCore.removeSubHealthUrl(urls);
    }

    public void stop() throws Exception{
        clientCore.stopClientServer();
    }


}
