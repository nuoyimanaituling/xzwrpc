package io.xzw.xzwrpc.stub.invoker.factory;


import io.xzw.xzwrpc.router.common.Filter;
import io.xzw.xzwrpc.router.common.LoadBalance;
import io.xzw.xzwrpc.router.common.Router;
import io.xzw.xzwrpc.spi.RpcSpiPluginLoader;
import io.xzw.xzwrpc.stub.invoker.component.InvokerUnit;
import io.xzw.xzwrpc.stub.invoker.refbean.RpcRefBean;
import io.xzw.xzwrpc.stub.net.Client;
import io.xzw.xzwrpc.stub.net.NetConstant;
import io.xzw.xzwrpc.stub.net.params.RpcFutureResp;
import io.xzw.xzwrpc.stub.net.params.RpcRequest;
import io.xzw.xzwrpc.stub.net.params.RpcResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import org.omg.CORBA.INTERNAL;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DelegateInvokerMethod {

    private final InvokerUnit invokerUnit;

    private final LoadBalance loadBalance;

    private final Class<?> routerClass;

    private List<String> urls;

    private final long timeout;

    private final Integer retryTimes;

    private final List<Class<?>> retryException;

    public DelegateInvokerMethod(RpcRefBean refBean,LoadBalance balance,InvokerUnit invokerUnit){
        this.invokerUnit =invokerUnit;
        this.loadBalance =balance;

        this.routerClass =refBean.getRouterClass();
        this.urls =refBean.getAvailUrls();
        this.timeout =refBean.getTimeout();
        this.retryTimes =refBean.getRetryTimes();
        this.retryException =refBean.getRetryExceptions();
        /**
         * 在下面的逻辑中有
         */
        this.retryException.addAll(NetConstant.RPC_NEED_RETRY_EXS);
    }

    @RuntimeType
    public Object interceptor(@This Object target, @AllArguments Object[] args, @Origin Method method){
        /**
         * 先走过滤和路由逻辑，再走负载均衡逻辑
         */
        Class<?> iface =target.getClass().getInterfaces()[0];
        RpcRequest request =RpcRequest.builder().methodName(method.getName()).classType(iface)
                .params(args).className(iface.getCanonicalName()).timeout(this.timeout).build();
        /**
         * filterAndRoute返回的是亚健康的url，过滤亚健康状态的url，自动实现故障转移
         */
        this.urls =filterAndRoute(urls,routerClass,method,args);
        List<String> healthUrls =invokerUnit.filterSubHealth(this.urls);
        RpcResponse rpcResponse=null;
        Client client=null;
        int retryTimes =0;
        try{
            while(rpcResponse == null || rpcResponse.getException() != null){
//                String url =loadBalance.select(healthUrls,this.urls);
                String url ="127.0.0.1:7248";
                client =invokerUnit.getInvokeClient(url);
                String requestId = UUID.randomUUID().toString().replace("-","");
                request.setRequestId(requestId);
                request.setCreateTimeMills(System.currentTimeMillis());
                /**
                 * 这里是核心逻辑，这里就是异步获取返回结果，这一段是核心逻辑
                 * 异步返回，返回后的结果可能是空的，因为客户端是异步发送的
                 */
                RpcFutureResp rpcFutureResp =client.send(request);
                rpcResponse =rpcFutureResp.get(request.getTimeout(), TimeUnit.MILLISECONDS);
                /**
                 * 当返回结果异常为空时，说明rpcResponse的结果不为null，则退出循环
                 * 当返回结果抛出异常那么就抛出retryTimes++，此时如果小于retrytimes，并且异常类型是retryException
                 * 中的类型时才会尝试重试，否则直接结束掉
                 * 或者超过三次也直接结束掉
                 * 不满足下面三个条件，才会进行重试
                 */
                if(rpcResponse.getException()==null || retryTimes++>=this.retryTimes
                || !this.retryException.contains(rpcResponse.getException().getClass())){
                   if(retryTimes>0){
                       log.debug("[xzw-rpc] rpc auto failover failed invoke");
                   }
                   if (retryTimes<this.retryTimes){
                       invokerUnit.getClientCore().invokeSuccess(url);
                   }
                   break;
                }
                // 之后进入重试阶段
                cleanAfterInvokeFailed(requestId,url,healthUrls);
            }
            if(rpcResponse.getException()!=null){
                throw new ExecutionException(request.getClassName()+"invoke failed",rpcResponse.getException());
            }
            if (!void.class.equals(method.getReturnType()) && rpcResponse.getData()!=null){
                return method.getReturnType().cast(rpcResponse.getData());
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }finally {
            if (client!=null){

                /**
                 * 从future池中删除请求信息
                 */
                client.cleanAfterInvoke(request);
            }
        }
        return null;
    }
    private void cleanAfterInvokeFailed(String requestId,String url,List<String> healthUrls){
        invokerUnit.getClientCore().removeTimeoutRespFromPool(requestId);
        invokerUnit.getClientCore().invokeFailed(url);
        healthUrls.remove(url);
    }
    private List<String> filterAndRoute(List<String> urls,Class<?> clazz,Method method,Object[] args){

        /**
         * filterList相当于过滤链
         */
        List<Filter> filterList = RpcSpiPluginLoader.getFilterList();
        List<String> res =new ArrayList<>();
        if (!filterList.isEmpty()){
            for (Filter filter :filterList) {
                for (String url:urls){
                    /**
                     * 添加过滤后的url
                     */
                    if (filter.filter(url,method,args)){
                        res.add(url);
                    }
                }
            }
        }
        else {
            res =urls;
        }
        Router router = RpcSpiPluginLoader.getRouterByClass(clazz);
        if (router!=null){
            res=router.route(res,method,args);
        }
        return res;
    }




}
