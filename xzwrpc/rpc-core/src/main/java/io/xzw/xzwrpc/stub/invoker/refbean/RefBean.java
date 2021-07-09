package io.xzw.xzwrpc.stub.invoker.refbean;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RefBean {
    private Class<?> targetFace;

    private List<String> availUrls;

    private String accessToken;

    private long timeout;

    private String version;

    private List<Class<?>> retryExceptions;

    private Class<?> routerClass;

    private Integer retryTimes;




}
