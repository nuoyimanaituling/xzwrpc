package io.xzw.xzwrpc.stub.net.client;


import io.xzw.xzwrpc.stub.net.NetConstant;
import io.xzw.xzwrpc.stub.net.params.HeartBeat;
import io.xzw.xzwrpc.stub.net.params.SystemHealthInfo;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 健康可用性分析
 * 分析逻辑：
 * 健康分析分为 2 个部分，可用率分析 和 心跳结果分析
 * 心跳分析：心跳判断为不健康的状态，需要后续心跳进行重新激活, 激活后可用统计将重新开始
 * 可用分析：可用率如果小于0.8为不健康的状态2，可以通过心跳进行激活，同样激活后可用统计将重新开始
 */

@Slf4j
public class HealthAnalyzer {
    // 记录调用成功的次数
    private final Map<String, AtomicInteger> invokeSuccessCnt;
    // 记录调用失败的次数
    private final Map<String,AtomicInteger>  invokeFailedCnt;

    // 记录不可用的地址
    private final Set<String> subHealthUrl;

    public HealthAnalyzer(){
        this.invokeSuccessCnt =new ConcurrentHashMap<>();
        this.invokeFailedCnt =new ConcurrentHashMap<>();
        this.subHealthUrl =new CopyOnWriteArraySet<>();
    }


    public void invokeSuccess(String url){
        incrCnt(url,this.invokeSuccessCnt);
        calcAvailableRate(url);
    }
    public void invokeFailed(String url){
        incrCnt(url,this.invokeFailedCnt);
        calcAvailableRate(url);
    }


    public void incrCnt(String url,Map<String,AtomicInteger> cntMap){
        if(cntMap.containsKey(url)){
            cntMap.get(url).incrementAndGet();
        }
        else{
            cntMap.put(url,new AtomicInteger());
        }
    }

    /**
     * 计算可用率，然后判断是否从不可用列表中添加，删除
     * @param url
     */
    public void calcAvailableRate(String url){
        BigDecimal availableRate =BigDecimal.ZERO;
        if(!invokeFailedCnt.containsKey(url)){
            availableRate=BigDecimal.ONE;
        }else {
            //发现此时的url已经添加到不健康的记录当中，此时需要重新计算可用率
            int healthCnt =invokeSuccessCnt.get(url).get();

            /**
             * healthcnt/(faithcnt+healthcnt)
             */
            availableRate =new BigDecimal(healthCnt).divide(new BigDecimal(invokeFailedCnt.get(url).get()+healthCnt)
            ,2,BigDecimal.ROUND_HALF_UP);
        }
        log.trace("[xzw-rpc] server[{}] current health available rate is {}", url, availableRate);
        if (NetConstant.SUB_HEALTH_AVAILABLE_RATE.compareTo(availableRate)>0){
            this.subHealthUrl.add(url);
        }else {
            this.subHealthUrl.remove(url);
        }
    }

    public void analyzeHeartBeatRes(SystemHealthInfo healthInfo,String url){

        // 响应时间+心跳检测间隔时间< 当前时间，那么代表这是上一次的心跳检测，此时不做任何处理
        if(healthInfo.getRespSendTime()+ TimeUnit.MILLISECONDS.convert(HeartBeat.BEAT_INTERVAL,TimeUnit.SECONDS)<System.currentTimeMillis()){
            return;
        }
        /**
         * 健康判断准则：
         * 1：延迟小于500ms
         * 2：cpu负载率小于0.9
         * 3：内存使用率低于0.8
         */
        boolean health =healthInfo.getLatency()<500 && healthInfo.getCpuLoadPercent().compareTo(new BigDecimal("0.9"))<0
                && healthInfo.getMemLoadPercent().compareTo(new BigDecimal("0.8"))<0;
        if(health){
            if(this.subHealthUrl.contains(url)){
                // 在这里健康与不健康的url次数，被更新
                this.subHealthUrl.remove(url);
                this.invokeSuccessCnt.put(url,new AtomicInteger());
                this.invokeFailedCnt.put(url,new AtomicInteger());
            }
        }else {
            this.subHealthUrl.add(url);
        }
        log.debug("[xzw-rpc] remote server[{}], health status[{}]", url, health);
    }

    public void heartBeatTimeout(String url){
        this.subHealthUrl.add(url);
    }


    /**
     * 这个url是由过滤链与路由之后的url，在这个逻辑中移除其中亚健康的url
     * @param url
     * @return
     */
    public List<String> filerSubHealth(List<String> url){
        List<String> all =new ArrayList<>(url);
        all.removeAll(this.subHealthUrl);
        return all;
    }
    public void removeUrl(String url){
        this.subHealthUrl.remove(url);
        this.invokeFailedCnt.remove(url);
        this.invokeSuccessCnt.remove(url);
    }

}
