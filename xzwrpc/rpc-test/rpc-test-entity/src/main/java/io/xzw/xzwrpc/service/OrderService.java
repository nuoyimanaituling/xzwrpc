package io.xzw.xzwrpc.service;

import io.xzw.xzwrpc.entity.Order;


public interface OrderService {

    void insertOrder(Order order);

    Order findById(Long id);
}
