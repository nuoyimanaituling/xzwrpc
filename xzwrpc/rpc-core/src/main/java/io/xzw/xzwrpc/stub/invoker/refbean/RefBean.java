package io.xzw.xzwrpc.stub.invoker.refbean;


import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * @author xzw
 */
@Data
@Builder
public class RefBean {

    /**
     * 代理接口
     */
    private Class<?> targetFace;

    /**
     * 可用地址
     */
    private List<String> availUrls;

    /**
     * 进入token
     */
    private String accessToken;

    /**
     * 超时时间
     */
    private long timeout;

    /**
     * 版本
     */
    private String version;

    /**
     * 重试异常
     */
    private List<Class<?>> retryExceptions;

    /**
     * 选择路由器
     */
    private Class<?> routerClass;

    /**
     * 重试次数
     */
    private Integer retryTimes;




}
