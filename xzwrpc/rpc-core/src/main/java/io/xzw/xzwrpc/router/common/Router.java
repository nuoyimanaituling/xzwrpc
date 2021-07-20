package io.xzw.xzwrpc.router.common;


import java.lang.reflect.Method;
import java.util.List;

/**
 * 定义使用的路由策略
 * @author xzw
 */
public interface Router {

    /**
     *  路由执行逻辑
     * @param urls 过滤使用的url
     * @param method 方法
     * @param args 方法参数
     * @return 返回路由到的一组服务地址
     */
    List<String> route(List<String> urls, Method method,Object[] args);
}
