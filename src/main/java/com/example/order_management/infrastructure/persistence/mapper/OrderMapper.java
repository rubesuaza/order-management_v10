package com.example.order_management.infrastructure.persistence.mapper;

import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderId;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import com.example.order_management.domain.valueobject.Money;
import com.example.order_management.infrastructure.persistence.entity.OrderEntity;
import com.example.order_management.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity(
                order.getId().getValue(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency(),
                order.getCreatedAt()
        );
        List<OrderItemEntity> itemEntities = order.getItems().stream()
                .map(item -> new OrderItemEntity(
                        UUID.randomUUID(),
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice().getAmount()
                ))
                .collect(Collectors.toList());
        entity.setItems(itemEntities);
        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(i -> new OrderItem(
                        i.getProductId(),
                        i.getQuantity(),
                        new Money(i.getUnitPrice(), entity.getCurrency())
                ))
                .collect(Collectors.toList());

        return reconstructOrder(
                entity.getId(),
                OrderStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getCustomerId(),
                items
        );
    }

    private Order reconstructOrder(UUID id, OrderStatus status, java.time.LocalDateTime createdAt, UUID customerId, List<OrderItem> items) {
        return Order.reconstruct(new OrderId(id), status, createdAt, customerId, items);
    }
}
