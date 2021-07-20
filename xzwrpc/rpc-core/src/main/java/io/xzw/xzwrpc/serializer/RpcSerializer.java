package io.xzw.xzwrpc.serializer;

import java.io.IOException;

/** 序列化抽象接口
 * @author xzw
 */
public interface RpcSerializer {

    /** 序列化
     * @param obj java类型
     * @param <T> 泛型
     * @return
     * @throws IOException
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     * @param bytes 反序列化所需字节数组
     * @param clazz 类型
     * @param <T>   泛型
     * @return 返回序列化对象
     * @throws IOException
     */
    <T> T deserialize(byte[] bytes,Class<T> clazz) throws IOException;

}
