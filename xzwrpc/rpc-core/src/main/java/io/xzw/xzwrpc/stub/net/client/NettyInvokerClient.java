package io.xzw.xzwrpc.stub.net.client;
import io.xzw.xzwrpc.stub.net.Client;
import io.xzw.xzwrpc.stub.net.common.ClientInvokerCore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 *  clientInvokerCore 又实现了ConnectManage
 */
public class NettyInvokerClient extends ClientInvokerCore {
    private final Lock lock =new ReentrantLock();
    @Override
    public Client getClient(String addr) {
        /**
         * 先创建clientServer然后在创建NettyClient，在ClientInvokerCore中传入地址和respPoll这个respPool
         * 是用来记录连续发送信息的，然后使用clientServers（ConnectServer），可以用来发送信息
         */
        initClientServerIfAbsent(addr);
        return new NettyClient(this.clientServers.get(addr),respPool);
    }
    private void initClientServerIfAbsent(String addr){
        /**
         * 说明已经初始化过了
         */
        if(this.clientServers.containsKey(addr)){
            return;
        }
        setLockIfAbsent(addr);
        /**
         * 避免重复初始化
         */
        synchronized (this.lockMap.get(addr)){
            if (this.clientServers.containsKey(addr)){
                return;
            }
            NettyClientServer clientServer =new NettyClientServer(this.serializer);
            /**
             * 执行init方法相当于在进行启动客户端连接的过程。
             */
            clientServer.init(addr,new ClientHandler(respPool,this.clientServers,this.availableAnalyzer));
            this.clientServers.put(addr,clientServer);
        }
    }

    private void setLockIfAbsent(String addr){

        // 因为在并发添加，所以此时需要在添加的时候加锁
        if(this.lockMap.containsKey(addr)){
            return;
        }
        lock.lock();
        try{
            if(!this.lockMap.containsKey(addr)){
                this.lockMap.put(addr,new Object());
            }
        }finally {
            lock.unlock();
        }

    }
}
