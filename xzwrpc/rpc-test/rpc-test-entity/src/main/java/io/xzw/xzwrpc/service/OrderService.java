package io.xzw.xzwrpc.service;

import io.xzw.xzwrpc.entity.Order;


/**
 * @author xzw
 */
public interface OrderService {

    /**
     * 插入订单
     * @param order 订单实体
     */
    void insertOrder(Order order);


    /** 根据id查找订单
     * @param id id
     * @return 订单
     */
    Order findById(Long id);
}
