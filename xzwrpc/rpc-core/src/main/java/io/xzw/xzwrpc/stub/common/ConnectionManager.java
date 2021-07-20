package io.xzw.xzwrpc.stub.common;

import io.xzw.xzwrpc.stub.net.Client;


/**
 * 获得客户端
 * 删除连接
 * @author xzw
 */
public interface ConnectionManager {

    /**
     * 获得客户端连接
     * @param addr 客户端连接地址
     * @return 返回客户端连接
     */
    Client getClient(String addr);


    /**
     * 删除连接
     * @param conn 需要删除连接的地址
     */
    void removeConn(String conn);

}
