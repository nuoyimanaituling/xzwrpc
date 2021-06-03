package io.xzw.xzwrpc.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Daiwei on 2021/3/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcFxReq {

    private String serviceClass;

    private String method;

    private Object[] args;

}
