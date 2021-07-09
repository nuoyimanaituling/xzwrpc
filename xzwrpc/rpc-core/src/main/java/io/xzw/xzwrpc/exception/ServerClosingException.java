package io.xzw.xzwrpc.exception;


// 捕获异常处理
public class ServerClosingException extends XzwRpcException {
    public ServerClosingException() {
        super("channel is closing");
    }
}
