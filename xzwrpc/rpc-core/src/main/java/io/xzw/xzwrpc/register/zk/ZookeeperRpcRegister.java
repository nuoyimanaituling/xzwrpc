package io.xzw.xzwrpc.register.zk;

import io.xzw.xzwrpc.register.RegisterConstant;
import io.xzw.xzwrpc.register.RpcRegister;
import io.xzw.xzwrpc.stub.net.NetConstant;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import java.util.List;
public abstract class ZookeeperRpcRegister implements RpcRegister {
    protected String zkConnStr;
    protected CuratorFramework client;
    @Override
    public void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        this.client = CuratorFrameworkFactory.builder().connectString(zkConnStr).namespace(RegisterConstant.XZW_RPC_NAME_SPACE)
                .retryPolicy(retryPolicy).build();
    }
    @Override
    public void start() {
        this.client.start();
    }
    @Override
    public void stop() {
        // 在client不为null，并且客户端的状态本身没有关闭的条件下进行
        if(this.client!=null && CuratorFrameworkState.STOPPED != this.client.getState()){
            this.client.close();
        }
    }
    @Override
    public void registerService(int port, Class<?> clazz, String version) {}

    @Override
    public void registerListeners(List<CuratorCacheListener> listenerLists) {

        // 传入的listenerList是对zk节点的监听
        /*
        CuratorCache.builder(client,NetConstant.FILE_SEPARATOR)
        第一个参数就是传入的客户端
        第⼆个参数就是监听节点的路径
         */

        /**
         * 观察ZNode的子节点并缓存状态，如果ZNode的子节点被创建，更新或者删除，那么Path Cache会更新缓存，
         *并且触发事件给注册的监听器。Path Cache是通过PathChildrenCache类来实现的，监听器注册是通过
         * PathChildrenCacheListener。
         */
        CuratorCache cache = CuratorCache.builder(client,NetConstant.FILE_SEPARATOR).build();
        for (CuratorCacheListener listener : listenerLists) {
            cache.listenable().addListener(listener);
        }
        cache.start();
    }
    @Override
    public void registerListeners() {}
    @Override
    public List<String> findAvailableUrls(Class<?> clazz, String version) {
        return null;
    }
    @Override
    public void registerInvokeProxy(Class<?> clazz) throws Exception {}
}
