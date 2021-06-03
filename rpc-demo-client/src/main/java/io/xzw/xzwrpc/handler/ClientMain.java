package io.xzw.xzwrpc.handler;

import io.xzw.xzwrpc.client.HttpInvokeHandler;
import io.xzw.xzwrpc.demo.pojo.User;
import io.xzw.xzwrpc.demo.service.UserService;
import io.xzw.xzwrpc.stub.RpcInvokerFactory;

import javax.jws.soap.SOAPBinding;

public class ClientMain {

    public static void main(String[] args) {

        RpcInvokerFactory invokerFactory =new RpcInvokerFactory(new HttpInvokeHandler());
        UserService userService = invokerFactory.createStub(UserService.class);
        User user = userService.findById(1);
        System.out.println(user);





    }
}
