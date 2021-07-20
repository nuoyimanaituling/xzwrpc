package io.xzw.xzwrpc.router.common;

import java.lang.reflect.Method;


/**
 * 过滤器 用于过滤请求
 * @author xzw
 */
public interface Filter {


    /**
     * 根据提供的url执行过滤路径
     * @param urls 过滤urls
     * @param method 方法
     * @param args 方法参数
     * @return
     */
    boolean filter(String urls, Method method,Object args[]);


}
