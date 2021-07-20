package io.xzw.xzwrpc.register;

import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import java.util.List;

/**
 * @author xzw
 */
public interface RpcRegister {
    /**
     * 初始化注册中心配置
     */
    void init();

    /**
     * 启动注册中心
     */
    void start();

    /**
     * 关闭zk
     */
    void stop();


    /** 注册服务
     * @param port 注册服务端口号
     * @param clazz clazz类型
     * @param version 服务版本号
     */
    void registerService(int port,Class<?> clazz,String version);

    /**
     * 注册监听器，监听上线，下线节点
     * @param listenerLists 注册监听器
     */

    void registerListeners(List<CuratorCacheListener> listenerLists);

    /**
     * 客户端或者服务端重写的服务
     */
    void registerListeners();

    /**
     * 根据服务接口查找可用的url
     * @param clazz 服务类型
     * @param version 版本号
     * @return
     */
    List<String> findAvailableUrls(Class<?> clazz, String version);

    /**
     * 注册代理类
     * @param clazz 注册本地代理
     * @throws Exception
     */
    void registerInvokeProxy(Class<?> clazz) throws Exception;





}
