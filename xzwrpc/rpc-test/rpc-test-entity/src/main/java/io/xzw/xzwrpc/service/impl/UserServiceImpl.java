package io.xzw.xzwrpc.service.impl;


import io.xzw.xzwrpc.entity.ClassInfo;
import io.xzw.xzwrpc.entity.User;
import io.xzw.xzwrpc.service.UserService;
import io.xzw.xzwrpc.stub.provider.annotation.RpcService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;


@RpcService()
public class UserServiceImpl implements UserService {

    @Override
    public void sayHello(String name) {
        System.out.println("hello" + name);
    }

    @Override
    public User findByUser(Long id) {
//        Thread.sleep(10 * 1000);
        ClassInfo info = ClassInfo.builder().grade(2).schoolName("hello school").stuMap(Collections.singletonMap("xzw", "xzw"))
                .nums(Arrays.asList(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.TEN)).build();
        return User.builder().id(id).username("xzw").age(26).info(info).build();
    }
}
