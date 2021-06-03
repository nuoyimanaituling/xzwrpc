package io.xzw.xzwrpc.api;

import net.sf.cglib.proxy.MethodInterceptor;

public interface RpcCgLibInvokeHandler extends MethodInterceptor {


    <T> T create(Class<T> klass);





}
