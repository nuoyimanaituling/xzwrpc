package io.xzw.xzwrpc.pojo;

import lombok.Data;

/**
 * Created by Daiwei on 2021/3/19
 */
@Data
public class RpcFxResp {

    private int code;

    private Object data;

    private Exception exception;

    public static io.xzw.xzwrpc.pojo.RpcFxResp ok(Object data) {
        io.xzw.xzwrpc.pojo.RpcFxResp resp = new io.xzw.xzwrpc.pojo.RpcFxResp();
        resp.setData(data);
        resp.setCode(0);
        return resp;
    }

    public static io.xzw.xzwrpc.pojo.RpcFxResp fail(Exception e) {
        io.xzw.xzwrpc.pojo.RpcFxResp resp = new io.xzw.xzwrpc.pojo.RpcFxResp();
        resp.setException(e);
        resp.setCode(-1);
        return resp;
    }
}
