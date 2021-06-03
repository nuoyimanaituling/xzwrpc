package io.xzw.xzwrpc.demo.service.impl;

import io.xzw.xzwrpc.anntataion.RpcService;
import io.xzw.xzwrpc.demo.pojo.User;
import io.xzw.xzwrpc.demo.service.UserService;

/**
 * Created by Daiwei on 2021/3/20
 */


@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public User findById(Integer id) {
        return new User(1, "daiwei", 24);

    }

}
