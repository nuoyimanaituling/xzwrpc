package io.xzw.xzwrpc.router;


import io.xzw.xzwrpc.exception.XzwRpcException;
import io.xzw.xzwrpc.router.common.LoadBalance;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
/**
 * 基于流量保护的启动因子  基于随机权重的轮询策略
 */
@Slf4j
public class WeightRandomLoadBalance extends LoadBalance {
    private final Random rnd;
    /**
     * 定义完全启动的因子，在这里10对应的是 s（秒）
     */
    private static final int FULL_LOAD_FACTOR =10;

    public WeightRandomLoadBalance(){
        this.rnd =new Random();
    }
    @Override
    // 首先判断是不是有健康的urls可用，如果没有则使用所有可用的urls
    public String select(List<String> healthUrls, List<String> allAvailableUrl) {

        List<String> urls =healthUrls;
        if (healthUrls.isEmpty()){
            log.warn("no health server");
            urls =allAvailableUrl;
        }
        // 如果没有可用的urls，那么就直接抛出异常
        if(urls.isEmpty()){
            throw new XzwRpcException("no available server url");
        }
       String weightList =getWeightList(urls);
        return weightList;
    }
    public String getWeightList(List<String> availUrl){
        Map<String,Integer> loadFactorMap =new HashMap<>();
        List<String> resultList =null;
        for (String s:availUrl){
            if(!this.dataMap.containsKey(s)){
                continue;
            }
            long value =Long.parseLong(this.dataMap.getOrDefault(s,"0"));
            /**
             * 热启动流程分析：interval是到现在已经启动了多少分钟
             * 规定当启动十分钟的话就代表完全预热了
             */
            long interval = (System.currentTimeMillis()-value)/1000;
            int loadFactor = FULL_LOAD_FACTOR;
            if(interval<FULL_LOAD_FACTOR){
                // 向上取整
                loadFactor =(int) interval+1;
            }
            loadFactorMap.put(s,loadFactor);
        }
        List<String> keyList =new ArrayList<>();
        Iterator<String> iterator =loadFactorMap.keySet().iterator();
        while(iterator.hasNext()){
            String key =iterator.next();
            keyList.add(key);
        }
        int length = loadFactorMap.size();
        int totalWeight = 0; // 总权重
        boolean sameWeight = true; // 权重是否都一样
        for (int i = 0; i < length; i++){
            int weight = loadFactorMap.get(keyList.get(i));
            totalWeight += weight; // 累计总权重
            if (sameWeight && i > 0 && weight != loadFactorMap.get(keyList.get(i-1))){
                sameWeight = false; // 并判断不同机器的权重是否一样
            }
        }
        if (totalWeight>0&&!sameWeight) {
            // 如果权重不相同且权重大于0则按总权重数随机
            int offset = ThreadLocalRandom.current().nextInt(totalWeight);
            // 并确定随机值落在哪个片断上
            for (int i = 0; i < length; i++) {
                offset -= loadFactorMap.get(keyList.get(i));
                if (offset < 0) {
                    return keyList.get(i);
                }
            }
        }
        // 如果权重相同或权重为0则均等随机
        return keyList.get(ThreadLocalRandom.current().nextInt(length));
    }
}
