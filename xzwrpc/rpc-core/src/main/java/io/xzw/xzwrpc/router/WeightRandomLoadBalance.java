package io.xzw.xzwrpc.router;

import io.xzw.xzwrpc.exception.XzwRpcException;
import io.xzw.xzwrpc.router.common.LoadBalance;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
/**
 * 基于流量保护的启动因子  基于随机权重的轮询策略
 * @author xzw
 */
@Slf4j
public class WeightRandomLoadBalance extends LoadBalance {
    /**
     * 定义完全启动的因子，在这里10对应的是 s（秒）
     */
    private static final int FULL_LOAD_FACTOR =10;

    public WeightRandomLoadBalance(){}

    /**
     * 首先判断是不是有健康的urls可用，如果没有则使用所有可用的urls
     * @param healthUrls      健康的服务地址
     * @param allAvailableUrl 所有的服务地址
     * @return 返回选择的服务地址
     */
    @Override
    public String select(List<String> healthUrls, List<String> allAvailableUrl) {

        List<String> urls = healthUrls;
        if (healthUrls.isEmpty()){
            log.warn("no health server");
            urls = allAvailableUrl;
        }
        // 如果没有可用的urls，那么就直接抛出异常
        if(urls.isEmpty()){
            throw new XzwRpcException("no available server url");
        }
        return getWeightList(urls);
    }
    public String getWeightList(List<String> availUrl){
        Map<String,Integer> loadFactorMap = new HashMap<>(16);
        for (String s:availUrl){
            if(!this.dataMap.containsKey(s)){
                continue;
            }
            long value = Long.parseLong(this.dataMap.getOrDefault(s,"0"));
            /**
             * 热启动流程分析：interval是到现在已经启动了多少s
             * 规定当启动10s的话就代表完全预热了
             */
            long interval =(System.currentTimeMillis()-value)/1000;
            int loadFactor =FULL_LOAD_FACTOR;
            if(interval<FULL_LOAD_FACTOR){
                // 向上取整
                loadFactor = (int) interval+1;
            }
            loadFactorMap.put(s,loadFactor);
        }
        List<String> keyList = new ArrayList<>();
        keyList.addAll(loadFactorMap.keySet());
        int length = loadFactorMap.size();
        // 总权重
        int totalWeight = 0;
        // 权重是否都一样
        boolean sameWeight = true;
        for (int i = 0; i < length; i++){
            int weight = loadFactorMap.get(keyList.get(i));
            // 累计总权重
            totalWeight += weight;
            if (sameWeight && i > 0 && weight != loadFactorMap.get(keyList.get(i-1))){
                // 并判断不同机器的权重是否一样
                sameWeight = false;
            }
        }
        if (totalWeight > 0 && !sameWeight) {
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
