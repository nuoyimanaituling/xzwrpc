package io.xzw.xzwrpc.exception;



/**
 * 自定义异常
 * @author xzw
 */
public class XzwRpcException extends RuntimeException{

    public XzwRpcException(String msg){
        super(msg);
    }

    @Override
    public String getMessage(){
        return super.getMessage();
    }
}
