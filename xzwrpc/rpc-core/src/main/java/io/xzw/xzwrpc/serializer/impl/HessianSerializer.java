package io.xzw.xzwrpc.serializer.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import io.xzw.xzwrpc.serializer.RpcSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author xzw
 */
public class HessianSerializer implements RpcSerializer {

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Hessian2Input hessian2Input = new Hessian2Input(inputStream);
        Object object = hessian2Input.readObject();
        return clazz.cast(object);
    }
    @Override
    public <T> byte[] serialize(T obj) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(byteArrayOutputStream);
        hessian2Output.writeObject(obj);
        hessian2Output.flush();
        return byteArrayOutputStream.toByteArray();
    }
}
