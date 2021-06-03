package server;

import io.xzw.xzwrpc.demo.service.impl.UserServiceImpl;
import io.xzw.xzwrpc.pojo.RpcFxReq;
import io.xzw.xzwrpc.pojo.RpcFxResp;
import io.xzw.xzwrpc.skeleton.RpcSkeletonServerStub;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import resolve.AnnotationResolve;
import resolve.MapResolve;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;


@RestController
@SpringBootApplication
public class mainclass {

    public static void main(String[] args) {
        SpringApplication.run(mainclass.class,args);
    }


    @Bean
    public RpcSkeletonServerStub stub() {

        AnnotationResolve annotationResolve =new AnnotationResolve("io.xzw");
        RpcSkeletonServerStub rpcServerStub = new RpcSkeletonServerStub(annotationResolve);
        annotationResolve.register();
//        rpcServerStub.register(new UserServiceImpl());

        return rpcServerStub;
    }



    @Resource
    private RpcSkeletonServerStub stub;

    @PostMapping("/")
    public RpcFxResp getService(@RequestBody RpcFxReq rpcFxReq) throws InvocationTargetException, IllegalAccessException {

        return stub.invoke(rpcFxReq);



    }









}
