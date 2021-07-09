package io.xzw.xzwrpc.stub.provider.annotation;


import io.xzw.xzwrpc.stub.provider.common.ServerRunnable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcService {
    Class<? extends ServerRunnable>[] init() default {};
    String version() default "";
}
