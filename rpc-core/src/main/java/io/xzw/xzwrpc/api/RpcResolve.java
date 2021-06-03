package io.xzw.xzwrpc.api;

public interface RpcResolve {


    // 服务端注册写好的服务
    void register(String name,Object service );


    // 获取服务对象
   Object resolve(String name);

    void register();

}
