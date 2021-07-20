package io.xzw.xzwrpc.stub.invoker.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rpc消费者参数配置
 * @author xzw
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcRef {

    long timeout() default 60 *1000;
    int retryTimes() default  3;

    Class<?>[] retryExceptions() default {};

    Class<?> routerClass() default Void.class;

    String version() default "";


}
