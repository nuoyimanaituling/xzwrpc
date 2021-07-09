package io.xzw.xzwrpc.router.common;

import java.lang.reflect.Method;


/**
 * 过滤器 用于过滤请求
 */
public interface Filter {


    boolean filter(String urls, Method method,Object args[]);


}
