package io.xzw.xzwrpc.router;

import io.xzw.xzwrpc.router.common.Filter;

import java.lang.reflect.Method;

/**
 * @author xzw
 */
public class DefaultFilter implements Filter {

    @Override
    public boolean filter(String urls, Method method, Object[] args) {

        System.out.println("我是过滤链逻辑");
        System.out.println("default filter!!!");
        return true;
    }
}
