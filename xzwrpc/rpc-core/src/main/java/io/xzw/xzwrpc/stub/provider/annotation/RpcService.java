package io.xzw.xzwrpc.stub.provider.annotation;


import io.xzw.xzwrpc.stub.provider.common.ServerInitRunnable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcService {
    Class<? extends ServerInitRunnable>[] init() default {};
    String version() default "";
}
