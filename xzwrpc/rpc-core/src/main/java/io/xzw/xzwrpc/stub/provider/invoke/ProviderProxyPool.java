package io.xzw.xzwrpc.stub.provider.invoke;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// service端 存储服务的具体实现
public class ProviderProxyPool {

    /**
     * 这里我设计为代理池为一个map，存储class到object的映射关系
     */
    private final Map<Class<?>,Object> proxyMap;
    // 全局唯一
    private volatile static ProviderProxyPool POOL;
    private ProviderProxyPool(){
        this.proxyMap =new ConcurrentHashMap<>();
    }
    // 单例创建
    public static ProviderProxyPool getInstance(){
        if(POOL ==null){
            synchronized (ProviderProxyPool.class){
                if(POOL ==null){
                    POOL =new ProviderProxyPool();
                }
            }
        }
        return POOL;
    }
    // 添加服务端的代理类
    public void addProxy(Class<?> clazz,Object proxy){
        this.proxyMap.put(clazz,proxy);
    }
    // 清理代理池
    public void cleanPool(){
        this.proxyMap.clear();
        POOL =null;
    }
    // 获取代理服务
    public Object getProxy(Class<?> clazz){
        return this.proxyMap.get(clazz);
    }
}
