package com.example.order_management.application.service;

import com.example.order_management.application.port.out.OrderRepository;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderId;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.valueobject.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service orchestrating order use cases.
 * Transactions are managed at this layer for atomic aggregate modifications.
 */
@Service
public class OrderApplicationService {

    private final OrderRepository orderRepository;

    public OrderApplicationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order createOrder(UUID customerId, List<OrderItemInput> items) {
        List<OrderItem> domainItems = items.stream()
                .map(i -> new OrderItem(
                        i.productId(),
                        i.quantity(),
                        new Money(i.unitPrice(), i.currency()))
                )
                .collect(Collectors.toList());
        Order order = Order.create(customerId, domainItems);
        return orderRepository.save(order);
    }

    public Order getOrder(OrderId orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId.getValue()));
    }

    @Transactional
    public Order payOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId.getValue()));
        order.place();
        return orderRepository.save(order);
    }

    public record OrderItemInput(UUID productId, int quantity, BigDecimal unitPrice, String currency) {
        public static OrderItemInput of(UUID productId, int quantity, BigDecimal unitPrice) {
            return new OrderItemInput(productId, quantity, unitPrice, "USD");
        }
    }
}
