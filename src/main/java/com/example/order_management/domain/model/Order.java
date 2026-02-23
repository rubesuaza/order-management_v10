package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.InvalidItemException;
import com.example.order_management.domain.exception.InvalidOrderStateException;
import com.example.order_management.domain.valueobject.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root for the Order Management context.
 * Invariants: at least one item, totalAmount = sum(line totals), minimum 10 USD to place.
 * State transitions: PENDING -> PAID -> SHIPPED -> DELIVERED; PENDING/PAID -> CANCELLED.
 */
public final class Order {

    private static final Money MINIMUM_ORDER_AMOUNT = new Money(new BigDecimal("10.00"));

    private final OrderId id;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    private final UUID customerId;
    private final Money totalAmount;

    private Order(OrderId id, OrderStatus status, LocalDateTime createdAt, List<OrderItem> items, UUID customerId) {
        this.id = id;
        this.status = status;
        this.createdAt = createdAt;
        this.items = List.copyOf(items);
        this.customerId = customerId;
        this.totalAmount = calculateTotal(items);
    }

    /**
     * Reconstructs an Order from persistence (bypasses create validation).
     */
    public static Order reconstruct(OrderId id, OrderStatus status, LocalDateTime createdAt, UUID customerId, List<OrderItem> items) {
        return new Order(id, status, createdAt, items, customerId);
    }

    public static Order create(UUID customerId, List<OrderItem> items) {
        if (customerId == null) {
            throw new IllegalArgumentException("customerId cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new InvalidItemException("An Order must have at least one OrderItem to be created");
        }
        return new Order(
                OrderId.generate(),
                OrderStatus.PENDING,
                LocalDateTime.now(),
                items,
                customerId
        );
    }

    private static Money calculateTotal(List<OrderItem> items) {
        Money total = Money.zero("USD");
        for (OrderItem item : items) {
            total = total.add(item.getLineTotal());
        }
        return total;
    }

    public void place() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(
                    "Order can only be placed when status is PENDING. Current: " + status);
        }
        if (totalAmount.isLessThan(MINIMUM_ORDER_AMOUNT)) {
            throw new InvalidOrderStateException(
                    "Order cannot be placed: total amount (" + totalAmount.getAmount() + " USD) is less than minimum 10.00 USD");
        }
        this.status = OrderStatus.PAID;
    }

    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new InvalidOrderStateException(
                    "Order can only be shipped when status is PAID. Current: " + status);
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException(
                    "Order can only be delivered when status is SHIPPED. Current: " + status);
        }
        this.status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException(
                    "Order cannot be cancelled when status is " + status + ". Only PENDING or PAID orders can be cancelled.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public OrderId getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }
}
