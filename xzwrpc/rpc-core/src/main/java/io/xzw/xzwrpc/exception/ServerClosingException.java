package io.xzw.xzwrpc.exception;


/**
 * 捕获异常处理
 * @author xzw
 */
public class ServerClosingException extends XzwRpcException {
    public ServerClosingException() {
        super("channel is closing");
    }


}
