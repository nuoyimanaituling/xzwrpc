package io.xzw.xzwrpc.stub.common;

import io.xzw.xzwrpc.stub.net.Client;


/**
 * 获得客户端
 * 删除连接
 */
public interface ConnectionManager {

    Client getClient(String addr);

    void removeConn(String conn);

}
