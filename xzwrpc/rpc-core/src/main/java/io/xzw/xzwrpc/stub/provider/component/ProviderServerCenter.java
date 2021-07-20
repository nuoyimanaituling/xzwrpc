package io.xzw.xzwrpc.stub.provider.component;

import io.xzw.xzwrpc.serializer.impl.HessianSerializer;
import io.xzw.xzwrpc.stub.net.server.NettyServerHub;
import io.xzw.xzwrpc.stub.provider.invoke.ProviderInvoker;

/**
 * @author xzw
 */
public class ProviderServerCenter {

    private final NettyServerHub serverHub;

    public ProviderServerCenter(int port){
        ProviderInvoker invoker = new ProviderInvoker();
        HessianSerializer serializer = new HessianSerializer();
        this.serverHub = new NettyServerHub(port,invoker,serializer);
    }

    /**
     * 在设置完属性后可以开始运行
     */
    public void afterSetProperties(){
        this.serverHub.start();
    }

    public void stop(){

        this.serverHub.stop();
    }
    public boolean isValid(){
        return this.serverHub.isActive();
    }

}
