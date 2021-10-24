package io.xzw.xzwrpc.stub.net.server;

import io.xzw.xzwrpc.serializer.RpcSerializer;
import io.xzw.xzwrpc.stub.net.Server;
import io.xzw.xzwrpc.stub.net.common.ProviderInvokerCore;
import io.xzw.xzwrpc.stub.provider.invoke.ProviderInvoker;

public class NettyServerHub implements Server {

    private final NettyServer nettyServer;

    private final ProviderInvokerCore invokerCore;

    public NettyServerHub(int port, ProviderInvokerCore invokerCore, RpcSerializer rpcSerializer){
        this.nettyServer=new NettyServer(port,rpcSerializer);
        this.invokerCore =invokerCore;
    }

    @Override
    public boolean isActive() {
        return this.nettyServer.isValid();
    }
    @Override
    public void stop() {
        this.nettyServer.close();
    }
    @Override
    public void start() {
        if(this.nettyServer!=null && this.invokerCore!=null){
            new Thread(()->{
                this.nettyServer.run(invokerCore);
            },"xzw-rpc-server-thread").start();

        }
    }
}