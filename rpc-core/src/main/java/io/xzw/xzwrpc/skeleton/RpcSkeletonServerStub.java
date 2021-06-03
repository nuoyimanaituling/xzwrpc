package io.xzw.xzwrpc.skeleton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.xzw.xzwrpc.api.RpcResolve;
import io.xzw.xzwrpc.pojo.RpcFxReq;
import io.xzw.xzwrpc.pojo.RpcFxResp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class RpcSkeletonServerStub {


    private RpcResolve resolve;

    public RpcSkeletonServerStub(RpcResolve resolve) {
        this.resolve = resolve;
    }

    public RpcFxResp invoke(RpcFxReq req) throws InvocationTargetException, IllegalAccessException {

        String classname =req.getServiceClass();
        Object[] objects = req.getArgs();
        String methodName =req.getMethod();
        Object service =resolve.resolve(classname);
        Method method =getMethodFromClass(service.getClass(),methodName);
       Object objectvalue=method.invoke(service,objects);
        return RpcFxResp.ok(JSON.toJSONString(objectvalue));

    }

    private Method getMethodFromClass(Class<?> clazz , String methodName) {

        return Arrays.stream(clazz.getMethods()).filter(m->methodName.equals(m.getName())).findFirst().get();

    }

    public  void  register(Object obj){

        Arrays.stream(obj.getClass().getInterfaces()).forEach(o->resolve.register(o.getName(),obj));

    }


}
