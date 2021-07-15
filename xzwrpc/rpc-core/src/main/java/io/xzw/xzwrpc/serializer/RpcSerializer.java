package io.xzw.xzwrpc.serializer;

import java.io.IOException;

public interface RpcSerializer {

    <T> byte[] serialize(T obj) throws IOException;

    <T> T deserialize(byte[] bytes,Class<T> clazz) throws IOException;

}
