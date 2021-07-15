package io.xzw.xzwrpc.basic;

import io.xzw.xzwrpc.service.impl.UserServiceImpl;
import io.xzw.xzwrpc.stub.provider.boot.RpcServerBoot;


/**
 * @author xzw
 */
public class ServerTestMain {

    public static void main(String[] args) {
        RpcServerBoot.builder().init("127.0.0.1:2181").registerService(UserServiceImpl.class, "1.0")
                .build().runAsync();
    }
}
