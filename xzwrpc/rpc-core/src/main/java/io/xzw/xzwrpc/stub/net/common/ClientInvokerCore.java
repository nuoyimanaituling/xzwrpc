package io.xzw.xzwrpc.stub.net.common;

import io.xzw.xzwrpc.serializer.RpcSerializer;
import io.xzw.xzwrpc.stub.common.ConnectionManager;
import io.xzw.xzwrpc.stub.net.client.HealthAvailableAnalyzer;
import io.xzw.xzwrpc.stub.net.params.RpcFutureResp;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
public abstract class ClientInvokerCore implements ConnectionManager {

    protected final Map<String,ConnectServer> clientServers =new ConcurrentHashMap<>();

    protected final HealthAvailableAnalyzer availableAnalyzer =new HealthAvailableAnalyzer();
    /**
     * respPool 这个参数比较复杂，在多处地方使用
     */
    protected final Map<String, RpcFutureResp> respPool =new ConcurrentHashMap<>();
    /**
     * 对象锁
     */
    protected final Map<String,Object> lockMap =new HashMap<>();

    protected RpcSerializer serializer;

    public void setSerializer(RpcSerializer rpcSerializer){
        this.serializer=rpcSerializer;
    }

    @Override
    public void removeConn(String conn) {
        if(this.clientServers.containsKey(conn)){
            this.clientServers.get(conn).close();
        }
        this.clientServers.remove(conn);
        this.availableAnalyzer.removeUrl(conn);
    }

    public List<String> removeSubHealthUrl(List<String> urls){

        return availableAnalyzer.filerSubHealth(urls);
    }
    public void invokeFailed(String url){
        availableAnalyzer.invokeFailed(url);
    }

    public void invokeSuccess(String url){
        availableAnalyzer.invokeSuccess(url);
    }
    public void stopClientServer(){
        clientServers.values().forEach(connectServer->{
            connectServer.close();
            log.info("connectServer[{}] close successfully", connectServer);
        });
        /**
         * 清理第一个资源
         */
        clientServers.values().stream().findFirst().ifPresent(ConnectServer::cleanStaticResource);
    }
    public void removeTimeoutRespFromPool(String reqId){
        this.respPool.remove(reqId);
    }

}
