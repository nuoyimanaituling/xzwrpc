package io.xzw.xzwrpc.serializer.impl;

import io.xzw.xzwrpc.serializer.RpcSerializer;

/**
 * 使用原生的序列化方式好像还没有实现，可以考虑hessian序列化
 * @author xzw
 */

public class DefaultSerializer implements RpcSerializer {
    @Override
    public <T> byte[] serialize(T obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return null;
    }
}
