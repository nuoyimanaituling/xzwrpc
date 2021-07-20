package io.xzw.xzwrpc.stub.provider.boot;


import io.xzw.xzwrpc.exception.XzwRpcException;
import io.xzw.xzwrpc.stub.provider.component.ProviderRegisterCenter;
import io.xzw.xzwrpc.stub.provider.component.ProviderServerCenter;
import io.xzw.xzwrpc.util.NetUtil;
import io.xzw.xzwrpc.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xzw
 */
@Slf4j
public class RpcServerMain {

    private final ProviderRegisterCenter registerUnit;

    private final ProviderServerCenter serverStubUnit;

    private final int availablePort;

    private final List<RegisterService> clazzList;

    private RpcServerMain(int port, String zkConnStr){
        this.availablePort = port;
        this.clazzList =new ArrayList<>();
        this.registerUnit  = new ProviderRegisterCenter(zkConnStr,this.availablePort);
        this.serverStubUnit = new ProviderServerCenter(port);
    }
    private RpcServerMain(String zkConnStr) {
        int defaultPort = 7248;
        this.availablePort = NetUtil.findAvailablePort(defaultPort);
        this.clazzList = new ArrayList<>();
        this.registerUnit  = new ProviderRegisterCenter(zkConnStr,this.availablePort);
        this.serverStubUnit = new ProviderServerCenter(availablePort);
    }
    public void run(){
        start();
    }
    public void stop(){
        this.registerUnit.stop();
        this.serverStubUnit.stop();
    }
    public void start(){
        // 启动NettyServer服务器
        serverStubUnit.afterSetProperties();
        try{
            for (RegisterService service:clazzList) {
                registerUnit.registerInvokeProxy(service.getClazz());
                registerUnit.registerService(this.availablePort,service.getClazz(),service.getVersion());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 在jvm中增加一个关闭的钩子，当jvm关闭的时候，会执行系统中已经设置的所有通过方法addShutdownHook添加的钩子，
         * 当系统执行完这些钩子后，jvm才会关闭。所以这些钩子可以在jvm关闭的时候进行内存清理、对象销毁等操作。
         */
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void runAsync(){
        ThreadPoolUtil.defaultRpcServerExecutor().execute(this::start);
    }
    public static ServerBuilder builder(){
        return new ServerBuilder();
    }


    /**
     * 通过ServerBuilder构建启动类
     */
    public static class ServerBuilder{

        private RpcServerMain rpcServerMain;

        private ServerBuilder(){}

        public ServerBuilder init(int serverPort,String zkConnStr){
            if(this.rpcServerMain !=null){
                throw new XzwRpcException("server already initialized");
            }
            rpcServerMain =new RpcServerMain(serverPort,zkConnStr);
            return this;
        }
        public ServerBuilder init(String zkConnStr) {
            if (this.rpcServerMain != null) {
                throw new XzwRpcException("server already initialized!");
            }
            rpcServerMain = new RpcServerMain(zkConnStr);
            return this;
        }
        public ServerBuilder registerService(Class<?> clazz,String version){
            this.rpcServerMain.clazzList.add(new RegisterService(clazz,version));
            return this;
        }
        public RpcServerMain build(){
            return this.rpcServerMain;
        }
    }
    private static class RegisterService{
        private final Class<?> clazz;
        private final String version;
        public RegisterService(Class<?> clazz,String version){
            this.clazz =clazz;
            this.version =version;
        }
        public Class<?> getClazz(){
            return clazz;
        }
        public String getVersion(){
            return version;
        }
    }
}
