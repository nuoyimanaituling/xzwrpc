package io.xzw.xzwrpc.basic;

import io.xzw.xzwrpc.entity.User;
import io.xzw.xzwrpc.service.UserService;
import io.xzw.xzwrpc.stub.invoker.factory.RpcInvokerFactory;


public class TestAppMain {

    public static void main(String[] args) throws InterruptedException {
//        rpcRefBean.setFinalAddr("127.0.0.1:7248");
//        rpcRefBean.setAccessToken("");
////        rpcRefBean.setAvailAddr(Arrays.asList("127.0.0.1:7248"));
//        rpcRefBean.setTargetFace(UserService.class);
//        UserService stub = (UserService) factory.createStub(rpcRefBean);
//        long start = System.currentTimeMillis();
        RpcInvokerFactory factory = new RpcInvokerFactory("127.0.0.1:2181");
        UserService userService = factory.createStubByClass(UserService.class, null);
//        for (int i = 0; i < 100000; i++) {
//            User user = userService.findByUser(10L);
//            System.out.println(user);
//            Thread.sleep(1000);
//        }

        System.out.println(userService.findByUser(10L));
//        OrderService orderService = factory.createStubByClass(OrderService.class);
////        Order order = orderService.findById(10L);
//        System.out.println(order);
//        for (int i = 0; i < 10; i++) {
//            System.out.println(user);
//        }
//        System.out.println(System.currentTimeMillis() - start);
        Thread.sleep(30 * 1000);
//        start = System.currentTimeMillis();
//        UserServiceImpl userService  = new UserServiceImpl();
        for (int i = 0; i < 10; i++) {
            User user = userService.findByUser(10L);
            System.out.println(user);
        }
//        System.out.println(System.currentTimeMillis() - start);

//        User byUser = userService.findByUser(10L);
//        System.out.println(byUser);
//        while(true) {}
//        factory.stop();
        Thread.sleep(300000);
    }
}
