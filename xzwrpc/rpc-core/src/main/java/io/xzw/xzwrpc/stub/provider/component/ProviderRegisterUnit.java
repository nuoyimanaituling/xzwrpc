package io.xzw.xzwrpc.stub.provider.component;


import io.xzw.xzwrpc.exception.XzwRpcException;
import io.xzw.xzwrpc.register.RegisterConstant;
import io.xzw.xzwrpc.register.zk.ZkRpcRegister;
import io.xzw.xzwrpc.stub.net.NetConstant;
import io.xzw.xzwrpc.stub.provider.invoke.RpcProviderProxyPool;
import io.xzw.xzwrpc.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Slf4j
public class ProviderRegisterUnit extends ZkRpcRegister {

    private final int port;
    // 上层调用要传入端口，与连接地址1
    public ProviderRegisterUnit(String zkConnStr,int port){
        this.port =port;
        this.zkConnStr =zkConnStr;
        this.init();
        this.start();
        this.registerListeners();
    }
    @Override
    public void registerInvokeProxy(Class<?> clazz) throws Exception {
        // 检测实现类是不是有多个接口实现,在这里暂时就先使用Bytebuddy，在后面可以
        // 优化的时候在改成cglib
        serverCheck(clazz);
        Class<?> clazzInterface =clazz.getInterfaces()[0];
        String clazzName= clazz.getCanonicalName()+"$$xzwRpcProxyByByteBuddy";
        Object proxy =new ByteBuddy().subclass(clazz).name(clazzName)
                .method(ElementMatchers.any()).intercept(MethodCall.invokeSuper().withAllArguments())
                .make().load(ProviderRegisterUnit.class.getClassLoader()).getLoaded().newInstance();
        RpcProviderProxyPool.getInstance().addProxy(clazzInterface,proxy);
    }
    @Override
    public void registerService(int port, Class<?> clazz, String version) {
        try {
            /**
             * 在这个地方相当于经历了两次的node添加，会相应的触发了两次监听事件
             */
            serverCheck(clazz);
            Class<?> service = clazz.getInterfaces()[0];
            String servicePath = NetConstant.FILE_SEPARATOR + service.getCanonicalName();
            if (version != null) {
                servicePath = servicePath.concat("_").concat(version);
            }
            if (client.checkExists().forPath(servicePath) == null){
                //forpath(path,data) 指定znode的路劲
                client.create().withMode(CreateMode.PERSISTENT).
                        forPath(servicePath, RegisterConstant.RPC_SERVICE.getBytes(StandardCharsets.UTF_8));
            }
            // 获取ip地址
            String addr = NetUtil.getIpAddress().concat(":").concat(String.valueOf(port));
            // 创建短暂的节点,此时将ip地址注册到zookeeper当中
             /**
             * 设置节点的信息为当前时间
             */
            client.create().withMode(CreateMode.EPHEMERAL).forPath(servicePath+ NetConstant.FILE_SEPARATOR +addr
            ,String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e){
            log.error("create zk node failed",e);
            e.printStackTrace();
            throw new XzwRpcException("create zk node failed");
        }

    }
    @Override
    public void registerListeners() {
        CuratorCacheListener listener = CuratorCacheListener.builder().forPathChildrenCache(
                NetConstant.FILE_SEPARATOR, client, new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                        if(event.getType()==PathChildrenCacheEvent.Type.CHILD_REMOVED && client.getState()!= CuratorFrameworkState.STOPPED
                        && isSelfRemoved(event.getData().getPath())){
                            /**
                             * 此时节点信息被更新为provider
                             */
                            client.create().withMode(CreateMode.EPHEMERAL).forPath(event.getData().getPath(),
                                    "provider".getBytes(StandardCharsets.UTF_8));
                        }
                    }
                }
        ).build();
        registerListeners(Collections.singletonList(listener));
    }
    private void serverCheck(Class<?> clazz){
        Class<?>[] classes =clazz.getInterfaces();
        if(classes.length==0){
            throw new XzwRpcException("not found interface of"+clazz.getCanonicalName());
        }
        if (classes.length>1){
            throw new XzwRpcException("find more than one interfaces of "+clazz.getCanonicalName());
        }
    }
    private boolean isSelfRemoved(String zkDataPath){
        /**
         * 如果返回true，则代表此时zk中的节点还保持活性
         * 如果返回false，则代表可能服务节点发生了变化，之前的宕掉了又新开了一台机器
         */
        // 检测被zk移除的节点地址在本机服务器上是不是还存在
        String ipAndPort = zkDataPath.substring(zkDataPath.lastIndexOf(NetConstant.FILE_SEPARATOR));
        return NetConstant.FILE_SEPARATOR.concat(NetUtil.getIpAddress().concat(":").concat(String.valueOf(port))).equals(ipAndPort);
    }
}
