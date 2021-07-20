package io.xzw.xzwrpc.stub.net.params;


import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

/**
 * @author xzw
 */
@Data
@Builder
public class RpcResponse implements Serializable {
    /**
     * 根据requestId 来进行通信识别
     */
    private String requestId;

    /**
     * 设置响应状态码
     */
    private int code;

    /**
     * 设置响应数据
     */
    private Object data;

    /**
     * 设置响应信息
     */
    private String msg;

    /**
     * 设置异常
     */
    private Exception exception;


}
