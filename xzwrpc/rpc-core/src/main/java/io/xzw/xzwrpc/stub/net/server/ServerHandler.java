package io.xzw.xzwrpc.stub.net.server;


import com.sun.org.apache.regexp.internal.RE;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.xzw.xzwrpc.exception.ServerClosingException;
import io.xzw.xzwrpc.stub.net.NetConstant;
import io.xzw.xzwrpc.stub.net.common.ProviderInvokerCore;
import io.xzw.xzwrpc.stub.net.params.HeartBeat;
import io.xzw.xzwrpc.stub.net.params.RpcRequest;
import io.xzw.xzwrpc.stub.net.params.RpcResponse;
import io.xzw.xzwrpc.util.OSHealthUtil;
import io.xzw.xzwrpc.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final ProviderInvokerCore invokerCore;
    private final ChannelGroup channels;
    private static volatile boolean SERVER_STATUS;
    private volatile boolean channelClosing;
    private volatile long msgTimeout;
    private final Set<String> reqIds;
    private final AtomicInteger globalReqNums;
    public ServerHandler(ProviderInvokerCore invokerCore,ChannelGroup channels,AtomicInteger globalReqNums){
        this.invokerCore =invokerCore;
        this.channels =channels;
        this.reqIds =new HashSet<>();
        this.msgTimeout = System.currentTimeMillis();
        this.globalReqNums =globalReqNums;
    }
    @Override
    /**
     * 处理请求
     */
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        ThreadPoolUtil.defaultRpcClientExecutor().execute(()->{
            try {
                if (invokerCore.valid(msg)) {
                    Channel channel = ctx.channel();
                    if (messagePreHandleFilter(channel, msg)) {
                        globalReqNums.incrementAndGet();
                        collectMessageData(channel, msg);
                        RpcResponse response = invokerCore.requestComingBellRing(msg);
                        channel.writeAndFlush(response);
                        /**
                         * reqIds.remove代表此时服务治理已经收集完了可以删除了
                         */
                        reqIds.remove(msg.getRequestId());
                        /**
                         * 这里引入globalReqNums实际上是判断是不是线程池还在处理请求，如果不为0，则代表还有全局请求
                         */
                        globalReqNums.decrementAndGet();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    private boolean messagePreHandleFilter(Channel channel,RpcRequest msg){
        if(msg.getRequestId().startsWith(NetConstant.HEART_BEAT_REQ_ID)){
            log.debug("[xzw-rpc] heart beat request from {}", channel.remoteAddress());
            channel.writeAndFlush(replyHealthCheck(msg));
            return false;
        }else if(msg.getRequestId().startsWith(NetConstant.IDLE_CHANNEL_CLOSE_REQ_ID)){
            channelClosing =true;
            /**
             * 说明已经超时
             * 如果msgTimeout小于当前时间的话，那么就代表已经超时，就说明最近一次的信息已经到达那么可以正常关闭了，或者没有在进行服务治理信息收集时才会赞成关闭
             */
            boolean approval =msgTimeout<System.currentTimeMillis() || reqIds.isEmpty();
            /**
             * 实现自动断开连接的功能
             */
            log.debug("[xzw-rpc] idle-channel close asking form {} and server says [{}]", channel.remoteAddress(), approval ? "ok" : "nope");
            if(approval){
                channel.writeAndFlush(HeartBeat.channelCloseRespSuccess());
                channels.find(channel.id()).close();
            }else{
                channel.writeAndFlush(HeartBeat.channelCloseRespFailed());
            }
            return false;
        }
        /***
         * 如果服务端调用关闭handler，那么此时消息预处理就会经过此层逻辑，即服务端关闭
         */
        else if(channelClosing || !ServerHandler.SERVER_STATUS){

            if (channel.isActive()&& channel.isWritable()){
                channel.writeAndFlush(RpcResponse.builder().code(-1).exception(new ServerClosingException()).build());
                return false;
            }
        }
        return true;
    }
    /**
     * 返回健康检测信息
     * @return
     */
    private RpcResponse replyHealthCheck(RpcRequest msg){
        long latency =System.currentTimeMillis() -msg.getCreateTimeMills();
        BigDecimal cpuLoad = OSHealthUtil.getCpuLoad();
        BigDecimal memLoad =OSHealthUtil.getMemLoad();
        return HeartBeat.healthResp(cpuLoad,memLoad,latency);
    }


    /**
     * 收集message的一些信息用于简单的服务治理
     * @param channel
     * @param msg
     */
    private void collectMessageData(Channel channel,RpcRequest msg){

        reqIds.add(msg.getRequestId());
        channelClosing =false;
        channels.add(channel);
        long reqTimeOut =msg.getTimeout()+msg.getCreateTimeMills();
        /**
         * 收集最近一次的超时时间
         */
        if(msgTimeout<reqTimeOut){
            msgTimeout =reqTimeOut;
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("xzw-rpc server catch a exception, ctx is closing", cause);
        ctx.close();
    }
    public static void serverHandlerOpen(){
        SERVER_STATUS =true;
    }

    public static void serverHandlerClose() {
        SERVER_STATUS = false;
    }



}
