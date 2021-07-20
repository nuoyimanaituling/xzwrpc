package io.xzw.xzwrpc.stub.invoker.component;

import io.xzw.xzwrpc.exception.XzwRpcException;
import io.xzw.xzwrpc.register.RegisterConstant;
import io.xzw.xzwrpc.register.zk.ZookeeperRpcRegister;
import io.xzw.xzwrpc.stub.common.ConnectionManager;
import io.xzw.xzwrpc.stub.net.NetConstant;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * zkData与availPathMap最初是在InvokerRegister定义
 * @author xzw
 */
public class InvokerRegisterCenter extends ZookeeperRpcRegister {

    private final Map<String, List<String>> availPathMap;
    private final Map<String,String> zkData;
    protected final ConnectionManager connectionManager;
    public InvokerRegisterCenter(String zkConnStr, ConnectionManager connectionManager){
        this.zkConnStr = zkConnStr;
        this.availPathMap = new ConcurrentHashMap<>();
        this.zkData = new ConcurrentHashMap<>();
        this.connectionManager = connectionManager;
    }
    public void afterSetProperties(){
        this.init();
        this.start();
        this.registerListeners();
    }

    @Override
    public List<String> findAvailableUrls(Class<?> clazz, String version) {
        String clazzName = clazz.getCanonicalName();
        if (version!=null){
            clazzName = clazzName.concat("_").concat(version);
        }
        if (availPathMap.containsKey(clazzName)){
            return availPathMap.get(clazzName);
        }
        /**
         *下面这个逻辑就是在判断是不是在后面又重新加了，如果重新加入那么就返回路径，如果为空的话，那么就抛出错误。
         */
        List<String> availPath = new CopyOnWriteArrayList<>();
        try{
            Stat stat = client.checkExists().forPath(NetConstant.FILE_SEPARATOR+clazzName);
            if(stat!=null){
                availPath = new CopyOnWriteArrayList<>(client.getChildren().forPath(NetConstant.FILE_SEPARATOR+clazzName));
                if(!availPath.isEmpty()){
                    availPathMap.putIfAbsent(clazzName,availPath);
                }else{
                    throw new XzwRpcException("no available remote service found for " + clazzName +".");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return availPath;
    }

    @Override
    public void registerListeners() {
        /**
         * 格式：
         * str【1】对应的是 服务名
         * str【2】对应的是 url
         *
         * 服务端的两次添加逻辑，先添加服务节点，后添加url
         * 此时客户端也有两次事件，在availmap中添加对应的服务名，生成存放服务地址的url
         * 第二次是往负载均衡的节点中添加url和时间戳，这个map后面使用到的是预热保护基于权重的负载均衡算法
         */
        CuratorCacheListener listener = CuratorCacheListener.builder().forPathChildrenCache(NetConstant.FILE_SEPARATOR, client,((client, event) ->{
            if (!Arrays.asList(PathChildrenCacheEvent.Type.CHILD_ADDED,PathChildrenCacheEvent.Type.CHILD_REMOVED).contains(event.getType())){
                return;
            }
            /**
             * str[1]是服务名
             * str[2]是地址名
             */
            String[] str = event.getData().getPath().split(NetConstant.FILE_SEPARATOR);
            String data  = new String(event.getData().getData(), StandardCharsets.UTF_8);
            /**
             * 这一段逻辑比较复杂，在这里详细说明一下。首先就是根据触发的事件，拿到对应节点的目录。
             * 如果是删除，则返回删除节点的目录。如果是添加，则返回添加后的目录
             * 所以可以根据返回目录进行字符串分割，然后针对我们定义的目录名进行分析
             */
            /**
             * 保证如果是新加入的服务，那么也可以有对应的空地址列表
             */
            if (RegisterConstant.RPC_SERVICE.equals(data)){
                if (!availPathMap.containsKey(str[1])){
                    availPathMap.put(str[1],new CopyOnWriteArrayList<>());
                }
                return;
            }
            /**
             * 如果是删除，那么直接拿到服务名后，可以在availPathmap的url list里面进行删除
             */
            if(event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED){
                availPathMap.get(str[1]).remove(str[2]);
                this.connectionManager.removeConn(str[2]);
                zkData.remove(str[2]);
            }
            /**
             * 如果是进行添加，那么可以availPathMap中进行地址的新添加
             */
            else if(event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                zkData.put(str[2], data);
                List<String> urls = availPathMap.get(str[1]);
                if (!urls.contains(str[2])) {
                    urls.add(str[2]);
                }
            }
        }
        )).build();
        registerListeners(Collections.singletonList(listener));
    }
    public Map<String,String> getZkData(){
        return this.zkData;
    }
}
