package io.xzw.xzwrpc.stub.invoker.factory;


import io.xzw.xzwrpc.router.WeightRandomLoadBalance;
import io.xzw.xzwrpc.router.common.LoadBalance;
import io.xzw.xzwrpc.serializer.impl.HessianSerializer;
import io.xzw.xzwrpc.spi.RpcSpiPluginLoader;
import io.xzw.xzwrpc.stub.invoker.component.InvokerRegisterUnit;
import io.xzw.xzwrpc.stub.invoker.component.InvokerUnit;
import io.xzw.xzwrpc.stub.invoker.refbean.RpcRefBean;
import io.xzw.xzwrpc.stub.net.client.NettyClient;
import io.xzw.xzwrpc.stub.net.client.NettyInvokerClient;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * rpc客户端主启动类
 * @author xzw
 */
public class RpcInvokerFactory {

    private InvokerUnit invokerUnit;

    private InvokerRegisterUnit registerUnit;

    private LoadBalance loadBalance;

    public RpcInvokerFactory(String registerConn){
        start(registerConn);
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    /**
     * 创建调用的本地代理
     */
    private Object createStub(RpcRefBean rpcRefBean){
        Object stub =null;
        try{

            stub =new ByteBuddy().subclass(rpcRefBean.getTargetFace()).method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(new DelegateInvokerMethod(rpcRefBean,loadBalance,invokerUnit)))
                    .make().load(this.getClass().getClassLoader()).getLoaded().newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return stub;
    }

//    /**
//     * 直接通过refBean类型创建对象,这是补充spring功能时的优化
//     */
//    public Object createStubByRefBean(RpcRefBean refBean){
//        List<String> availableUrls =registerUnit.findAvailableUrls(refBean.getTargetFace(),refBean.getVersion());
//        refBean.setAvailUrls(availableUrls);
//    }
    public <T> T createStubByClass(Class<T> clazz,String version){
        List<String> availableUrls =registerUnit.findAvailableUrls(clazz,version);
        List<Class<?>> retryException =new ArrayList<>();
        retryException.add(TimeoutException.class);
        RpcRefBean refBean =RpcRefBean.builder().targetFace(clazz).availUrls(availableUrls)
                .version("1.0").retryTimes(3).timeout(60000).retryExceptions(retryException).build();
        Object stub =createStub(refBean);
        return clazz.cast(stub);
    }
    public void start(String zkConnStr){
        try {
            this.invokerUnit =new InvokerUnit(HessianSerializer.class, NettyInvokerClient.class);
            /**
             * 启动客户端的server
             */
            this.invokerUnit.afterSetProperties();
            /** 启动的时候就启动Spi
             * zk启动
             * 传给invokerRegisterUnit是因为触发删除事件的时候需要根据invokerUnit获得clientCore
             */
            this.registerUnit =new InvokerRegisterUnit(zkConnStr,this.invokerUnit.getClientCore());
            this.registerUnit.afterSetProperties();
            RpcSpiPluginLoader.load();
            this.loadBalance =new WeightRandomLoadBalance();
            this.loadBalance.setDataMap(registerUnit.getZkData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stop(){
        try {
            invokerUnit.stop();
            registerUnit.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}