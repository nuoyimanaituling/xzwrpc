package io.xzw.xzwrpc.stub.net.params;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author xzw
 */
@Data
@Builder
public class RpcRequest implements Serializable {
    /**
     * 定义本次请求id
     */
    private String requestId;
    /**
     * 请求调用的className
     */
    private String className;
    /**
     * 请求调用的方法名
     */
    private String methodName;
    /**
     * 方法参数
     */
    private Object[] params;
    /**
     * 定义请求的接口类型
     */
    private Class<?> classType;
    /**
     * 定义创建的时间
     */
    private long createTimeMills;
    /**
     * 定义超时时间
     */
    private long timeout;
    @Override
    public String toString() {
        return "RpcRequest{" +
                "requestId='" + requestId + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", params=" + Arrays.toString(params) +
                ", classType=" + classType +
                ", createTimeMills=" + createTimeMills +
                "" +
                ", timeout=" + timeout +
                '}';
    }
}
