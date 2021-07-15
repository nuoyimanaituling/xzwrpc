package io.xzw.xzwrpc.stub.net;

public interface Server {

    // 启动服务器
    void start();
    // 停止服务器
    void stop();
    // 判断服务器是否处于激活状态
    boolean isActive();


}
