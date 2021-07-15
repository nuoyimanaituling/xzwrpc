package io.xzw.xzwrpc.router.common;


import java.util.List;
import java.util.Map;

/**
 * 定义负载均衡策略 实现了负载均衡需要的组件
 */
public abstract class LoadBalance {
    /**
     * 缓存url与创建时间
     */
    protected Map<String,String> dataMap;
    public void setDataMap(Map<String,String> map){
        this.dataMap =map;
    }
    // 定义抽象方法，由具体的负载均衡器去实现
    public abstract String select(List<String> healthUrls,List<String> allAvailableUrl);







}
