package io.xzw.xzwrpc.stub.net;


import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import io.xzw.xzwrpc.exception.ServerClosingException;

import java.math.BigDecimal;


import java.util.Collections;
import java.util.List;

// 定义网络通信常量：网络层通信，通信常量
public class NetConstant {


    private NetConstant() {}

    public static  final String FILE_SEPARATOR ="/";

    public static final BigDecimal SUB_HEALTH_AVAILABLE_RATE =new BigDecimal("0.8");

    public static final String HEART_BEAT_REQ_ID ="HEART_BEAT_PING";

    public static final String HEART_BEAT_RESP_ID ="HEART_BEAT_PONG";

    public static final String IDLE_CHANNEL_CLOSE_REQ_ID ="idle_close_ask";

    public static final String IDLE_CHANNEL_CLOSE_RESP_ID ="idle_close_check";

    public static final List<Class<?>> RPC_NEED_RETRY_EXS = Collections.singletonList(ServerClosingException.class);







}
