package io.xzw.xzwrpc.entity;

import lombok.Data;


@Data
public class ReturnT<T> {

    private int status;

    private String msg;

    private T data;

    public static <T> io.xzw.xzwrpc.entity.ReturnT<T> ok(T data) {
        io.xzw.xzwrpc.entity.ReturnT<T> res = new io.xzw.xzwrpc.entity.ReturnT<>();
        res.setMsg("ok");
        res.setData(data);
        res.setStatus(0);
        return res;
    }

    public static <T> io.xzw.xzwrpc.entity.ReturnT<T> ok() {
        return ok(null);
    }
}
