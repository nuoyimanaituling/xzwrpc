package io.xzw.xzwrpc.service;

import io.xzw.xzwrpc.entity.User;


public interface UserService {

    void sayHello(String name);

    User findByUser(Long id);
}
