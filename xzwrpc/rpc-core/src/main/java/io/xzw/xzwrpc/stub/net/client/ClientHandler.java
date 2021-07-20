package io.xzw.xzwrpc.stub.net.client;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.xzw.xzwrpc.stub.net.NetConstant;
import io.xzw.xzwrpc.stub.net.common.ConnectServer;
import io.xzw.xzwrpc.stub.net.params.HeartBeat;
import io.xzw.xzwrpc.stub.net.params.FutureResp;
import io.xzw.xzwrpc.stub.net.params.RpcResponse;
import io.xzw.xzwrpc.stub.net.params.SystemHealthInfo;
import io.xzw.xzwrpc.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xzw
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    /**
     * 这个参数用来控制心跳结果
     */
    private volatile boolean beatReturn;

    /**
     * 控制已经空闲次数
     */
    private final AtomicInteger idleHeartBeatTimes;

    /**
     * 设置条件等待队列
     */
    private final Lock lock =new ReentrantLock();

    /**
     * 设置条件等待队列
     */
    private final Condition beatReturnCond =lock.newCondition();


    /**
     * 唤醒客户端请求等待线程
     */
    private final Map<String, FutureResp> respPool;


    /**
     * 客户端连接器
     */
    private final Map<String, ConnectServer> clientServers;

    /**
     * 健康分析器
     */
    private final HealthAnalyzer availableAnalyzer;

    public ClientHandler(Map<String, FutureResp> respPool, Map<String, ConnectServer> clientServers, HealthAnalyzer availableAnalyzer){

        this.respPool = respPool;
        this.clientServers = clientServers;
        this.idleHeartBeatTimes = new AtomicInteger();
        this.availableAnalyzer= availableAnalyzer;

    }

    /**
     * 触发控线检测的事件，在超过30s，客户端没有接受或者发送数据，那么客户端就发送心跳检测信息，同时如果在30*10，在
     * 300s内客户端没有发送消息，那么客户端就会自动关闭连接
     * 而发送的心跳检测消息恢复的消息中就是服务端的系统分析报告
     * @param ctx 当前handler上下文
     * @param evt 触发事件类型
     * @throws Exception 抛出异常
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if(evt instanceof IdleStateEvent){
            String serverAddr = ctx.channel().remoteAddress().toString().substring(1);
            if(idleHeartBeatTimes.get() < 10){
                log.debug("[xzw-rpc] send heart beat request");
                ctx.channel().writeAndFlush(HeartBeat.healthReq());
            }
            else {
                log.debug("[xzw-rpc] send idle channel close request");
                if (idleHeartBeatTimes.get() == 10){
                    this.clientServers.remove(serverAddr);
                    this.availableAnalyzer.removeUrl(serverAddr);
                }
                ctx.channel().writeAndFlush(HeartBeat.channelCloseReq());
            }
            /**
             * 下面这个异步线程实际上完成的就是针对超时没有获取心跳检测结果的处理步骤
             * 在发送心跳检测信息之后线程进入阻塞状态，然后等待3s，如果超时没有获取心跳检测结果那么就加入到
             * 不健康的url列表中
             */
            ThreadPoolUtil.defaultRpcClientExecutor().execute(()->{
                heartBeatTimeoutCheck(serverAddr);
            });
        }
        super.userEventTriggered(ctx,evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("xzw-rpc client catch a exception ,ctx is closing",cause);
        String serverAddr = ctx.channel().remoteAddress().toString().substring(1);
        this.clientServers.remove(serverAddr);
        this.availableAnalyzer.removeUrl(serverAddr);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {

        ThreadPoolUtil.defaultRpcClientExecutor().execute(()->{
            String serverAddr = ctx.channel().remoteAddress().toString().substring(1);
            System.out.println(serverAddr);
            // 相当于此时传过来的信息时心跳检测信息
            if (!messagePreHandleFilter(ctx.channel(),msg,serverAddr)){
                return;
            }
            // 正常消息,每收到一次正常消息就将心跳检测次数置为0
            idleHeartBeatTimes.set(0);
            // 收到数据后就可以唤醒等待线程
            FutureResp resp = respPool.get(msg.getRequestId());
            if(resp != null){
                // 唤醒阻塞线程
                resp.RespBackBellRing(msg);
            }
        });
    }
    public boolean messagePreHandleFilter(Channel channel,RpcResponse msg,String serverAddr){
        /**
         * 当beatReturn为false的时候，每发送一次心跳检测就对心跳检测次数加1，
         * 到达10次就主动关闭连接
         */
        if(msg.getRequestId().startsWith(NetConstant.HEART_BEAT_RESP_ID) && !beatReturn){
            idleHeartBeatTimes.getAndIncrement();
            wakeBeatTimeoutChecker();
            availableAnalyzer.analyzeHeartBeatRes((SystemHealthInfo)msg.getData(),serverAddr);
            return false;
        }
        if (msg.getRequestId().startsWith(NetConstant.IDLE_CHANNEL_CLOSE_RESP_ID)){
            wakeBeatTimeoutChecker();
            if(msg.getCode() == 0){
                channel.close();
                log.debug("[xzw-rpc] idle-channel[{}] close!", serverAddr);
            }
            return false;
        }
        return true;
    }

    public void wakeBeatTimeoutChecker(){
        // 说明已经有心跳返回结果了
        beatReturn = true;
        lock.lock();
        try{
            this.beatReturnCond.signalAll();
        }finally {
            lock.unlock();
        }
    }
    private void heartBeatTimeoutCheck(String serverAddress){
        beatReturn = false;
        lock.lock();
        try{
           while(!beatReturn){
               boolean await = this.beatReturnCond.await(3, TimeUnit.SECONDS);
               if(!await){
                   break;
               }
           }
           // 超时处理
            if(!beatReturn){
                beatReturn = true;
                availableAnalyzer.heartBeatTimeout(serverAddress);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }
}
