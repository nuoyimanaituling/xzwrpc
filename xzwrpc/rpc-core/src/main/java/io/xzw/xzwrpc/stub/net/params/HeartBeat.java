package io.xzw.xzwrpc.stub.net.params;

import io.xzw.xzwrpc.stub.net.NetConstant;


import java.math.BigDecimal;

/**
 * @author xzw
 */
public final class HeartBeat {
    public static final int BEAT_INTERVAL = 30;

    /**
     * 健康监测请求
     * @return 发送健康监测请求包
     */
    public static RpcRequest healthReq(){
        return RpcRequest.builder().requestId(NetConstant.HEART_BEAT_REQ_ID).createTimeMills(System.currentTimeMillis())
                .build();
    }

    /**
     * 服务端返回自己的健康信息
     * @param cpuLoad  cpu负载
     * @param memLoad  内存使用率
     * @param latency 延迟
     * @return 服务端返回自己的健康信息
     */
    public static RpcResponse healthResp(BigDecimal cpuLoad,BigDecimal memLoad,long latency){
        return RpcResponse.builder().code(0).requestId(NetConstant.HEART_BEAT_RESP_ID)
                .data(new SystemHealthInfo(latency,cpuLoad,memLoad)).build();
    }

    /**
     * @return 连接关闭请求
     */
    public static RpcRequest channelCloseReq(){
        return RpcRequest.builder().requestId(NetConstant.IDLE_CHANNEL_CLOSE_REQ_ID)
                .createTimeMills(System.currentTimeMillis()).build();
    }

    /**
     * @return 响应连接关闭成功
     */
    public static RpcResponse channelCloseRespSuccess() {
        return RpcResponse.builder().code(0)
                .requestId(NetConstant.IDLE_CHANNEL_CLOSE_RESP_ID).build();
    }

    /**
     * @return 响应连接关闭失败
     */
    public static RpcResponse channelCloseRespFailed(){
        return RpcResponse.builder().code(-1).requestId(NetConstant.IDLE_CHANNEL_CLOSE_RESP_ID).build();
    }






}
