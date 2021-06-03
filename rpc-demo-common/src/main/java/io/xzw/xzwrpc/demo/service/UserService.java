package io.xzw.xzwrpc.demo.service;

import io.xzw.xzwrpc.demo.pojo.User;

/**
 * Created by Daiwei on 2021/3/20
 */
public interface UserService {

    User findById(Integer id);
}
