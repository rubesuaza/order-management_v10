package com.example.order_management.application.port.out;

import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderId;

import java.util.Optional;

/**
 * Output port for persisting and retrieving Order aggregates.
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(OrderId orderId);
}
