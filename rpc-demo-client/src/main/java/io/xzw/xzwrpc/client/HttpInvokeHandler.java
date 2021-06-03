package io.xzw.xzwrpc.client;

import com.alibaba.fastjson.JSON;
import io.xzw.xzwrpc.api.RpcCgLibInvokeHandler;
import io.xzw.xzwrpc.pojo.RpcFxReq;
import io.xzw.xzwrpc.pojo.RpcFxResp;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.Method;


public class HttpInvokeHandler implements RpcCgLibInvokeHandler {

    private OkHttpClient okHttpClient;


    private Enhancer enhancer;


    private Class<?> proxyclass;

    MediaType JSONTYPE = MediaType.get("application/json;charset=utf-8");

    public HttpInvokeHandler() {
        this.okHttpClient = new OkHttpClient();
        this.enhancer = new Enhancer();
    }

    @Override
    public <T> T create(Class<T> klass) {
       enhancer.setSuperclass(klass);
       enhancer.setCallback(this);
        this.proxyclass =klass;
       return klass.cast(enhancer.create());

    }
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {


        RpcFxReq request =new RpcFxReq();
        request.setServiceClass(this.proxyclass.getName());
        request.setMethod(method.getName());
        request.setArgs(objects);
        RpcFxResp response =post(request,"http://127.0.0.1:8080");
        // 返回的对象类型必须是对象
        Object obj = JSON.parseObject(response.getData().toString(),method.getReturnType());
        return obj;
    }

    private RpcFxResp post(RpcFxReq request, String url) throws IOException {

        String reqJson =JSON.toJSONString(request);
        OkHttpClient client =new OkHttpClient();
        System.out.println("req:"+reqJson);
        // 以json方式发送请求
        final Request request1 =new Request.Builder().url(url)
                //将自定义request放到请求体中
                .post(RequestBody.create(JSONTYPE,reqJson))
                .build();
        String rep =client.newCall(request1).execute().body().string();
        System.out.println("resp json: "+rep);
        // 根据http请求拿到的字符串进行反序列化
        return JSON.parseObject(rep,RpcFxResp.class);


    }
}
