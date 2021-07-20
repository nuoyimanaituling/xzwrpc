package io.xzw.xzwrpc.service;

import io.xzw.xzwrpc.entity.User;


/**
 * @author xzw
 */
public interface UserService {

    /**
     * 打印信息
     * @param name 名字
     */
    void sayHello(String name);

    /**
     * 根据id查找user
     * @param id id信息
     * @return 返回user对象
     */
    User findByUser(Long id);
}
