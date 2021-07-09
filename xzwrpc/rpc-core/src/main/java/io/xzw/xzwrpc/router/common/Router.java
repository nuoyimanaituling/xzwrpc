package io.xzw.xzwrpc.router.common;


import java.lang.reflect.Method;
import java.util.List;

/**
 * 定义使用的路由策略
 */
public interface Router {

    List<String> route(List<String> urls, Method method,Object[] args);
}
