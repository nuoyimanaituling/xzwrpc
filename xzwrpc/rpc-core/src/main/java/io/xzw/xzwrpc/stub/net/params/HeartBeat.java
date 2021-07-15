package io.xzw.xzwrpc.stub.net.params;

import io.xzw.xzwrpc.stub.net.NetConstant;


import java.math.BigDecimal;

public final class HeartBeat {
    public static final int BEAT_INTERVAL =30;

    // 发送健康监测请求包
    public static RpcRequest healthReq(){
        return RpcRequest.builder().requestId(NetConstant.HEART_BEAT_REQ_ID).createTimeMills(System.currentTimeMillis())
                .build();
    }
    // 服务端返回自己的健康信息
    public static RpcResponse healthResp(BigDecimal cpuLoad,BigDecimal memLoad,long latency){

        return RpcResponse.builder().code(0).requestId(NetConstant.HEART_BEAT_RESP_ID).data(new SystemHealthInfo(latency,cpuLoad,memLoad))
                .build();
    }

    public static RpcRequest channelCloseReq(){

        return RpcRequest.builder().requestId(NetConstant.IDLE_CHANNEL_CLOSE_REQ_ID)
                .createTimeMills(System.currentTimeMillis()).build();
    }

    public static RpcResponse channelCloseRespSuccess() {

        return RpcResponse.builder().code(0)
                .requestId(NetConstant.IDLE_CHANNEL_CLOSE_RESP_ID).build();
    }

    public static RpcResponse channelCloseRespFailed(){

        return RpcResponse.builder().code(-1).requestId(NetConstant.IDLE_CHANNEL_CLOSE_RESP_ID).build();
        
    }






}
