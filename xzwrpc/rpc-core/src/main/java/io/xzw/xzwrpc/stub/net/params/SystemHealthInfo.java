package io.xzw.xzwrpc.stub.net.params;

import lombok.Data;

import java.math.BigDecimal;


/**
 * @author xzw
 */
@Data
public class SystemHealthInfo {

    /**
     * 延迟参数
     */
    private long latency;
    /**
     * cpu使用率
     */
    private BigDecimal cpuLoadPercent;

    /**
     * 内存使用率
     */
    private BigDecimal memLoadPercent;

    /**
     * 响应时间
     */
    private long respSendTime;

    public SystemHealthInfo(){}

    public SystemHealthInfo(long latency, BigDecimal cpuLoadPercent, BigDecimal memLoadPercent) {
        this.latency = latency;
        this.cpuLoadPercent = cpuLoadPercent;
        this.memLoadPercent = memLoadPercent;
        this.respSendTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "SystemHealthInfo{" +
                "latency=" + latency +
                ", cpuLoadPercent=" + cpuLoadPercent +
                ", memLoadPercent=" + memLoadPercent +
                ", respSendTime=" + respSendTime +
                '}';
    }
}
