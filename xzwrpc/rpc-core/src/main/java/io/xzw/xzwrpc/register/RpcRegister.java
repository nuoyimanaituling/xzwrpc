package io.xzw.xzwrpc.register;

import org.apache.curator.framework.recipes.cache.CuratorCacheBuilder;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

import java.util.List;

public interface RpcRegister {
    // 初始化注册中心配置
    void init();
    // 启动注册中心
    void start();
    // 关闭zk
    void stop();
    // 注册服务
    void registerService(int port,Class<?> clazz,String version);
    // 注册监听器，监听上线，下线节点
    void registerListeners(List<CuratorCacheListener> listenerLists);
    //
    void registerListeners();
    // 根据服务接口查找可用的url
    List<String> findAvailableUrls(Class<?> clazz, String version);
    //  注册代理
    void registerInvokeProxy(Class<?> clazz) throws Exception;





}
