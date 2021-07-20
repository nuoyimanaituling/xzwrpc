package io.xzw.xzwrpc.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @author xzw
 */
@Data
@Builder
public class Order implements Serializable {

    private Long orderId;

    private String customName;

    private Date createTime;

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customName='" + customName + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
