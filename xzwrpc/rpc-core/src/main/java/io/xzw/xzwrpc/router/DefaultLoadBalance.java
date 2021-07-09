package io.xzw.xzwrpc.router;

import io.xzw.xzwrpc.exception.XzwRpcException;
import io.xzw.xzwrpc.router.common.LoadBalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


// 使用的是轮询的负载均衡方式
public class DefaultLoadBalance extends LoadBalance {


    private final AtomicInteger cnt =new AtomicInteger();
    @Override
    public String select(List<String> healthUrls, List<String> allAvailableUrl) {
        //  获取健康的路由表，如果健康路由表为空的话，那么就整体使用所有可用的路由表
        List<String> urls =healthUrls.isEmpty()? allAvailableUrl:healthUrls;
        if(!urls.isEmpty()){
            return urls.get(cnt.getAndIncrement()%urls.size());
        }
        else{
            throw new XzwRpcException("no available server url");
        }
    }
}
