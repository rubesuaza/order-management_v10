package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.InvalidItemException;
import com.example.order_management.domain.exception.InvalidOrderStateException;
import com.example.order_management.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Order Aggregate Root")
class OrderTest {

    private UUID customerId;
    private OrderItem validItem;
    private List<OrderItem> validItems;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        validItem = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("5.00")));
        validItems = List.of(validItem);
    }

    @Nested
    @DisplayName("creation")
    class Creation {
        @Test
        void shouldCreateOrderWithValidItems() {
            Order order = Order.create(customerId, validItems);
            assertThat(order.getId()).isNotNull();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(order.getCustomerId()).isEqualTo(customerId);
            assertThat(order.getItems()).hasSize(1);
            assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("10.00"));
        }

        @Test
        void shouldRejectNullCustomerId() {
            assertThatThrownBy(() -> Order.create(null, validItems))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("customerId");
        }

        @Test
        void shouldRejectNullItems() {
            assertThatThrownBy(() -> Order.create(customerId, null))
                    .isInstanceOf(InvalidItemException.class)
                    .hasMessageContaining("at least one");
        }

        @Test
        void shouldRejectEmptyOrder() {
            assertThatThrownBy(() -> Order.create(customerId, List.of()))
                    .isInstanceOf(InvalidItemException.class)
                    .hasMessageContaining("at least one");
        }

        @Test
        void shouldCalculateTotalFromMultipleItems() {
            OrderItem item1 = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("3.00")));
            OrderItem item2 = new OrderItem(UUID.randomUUID(), 1, new Money(new BigDecimal("5.00")));
            Order order = Order.create(customerId, List.of(item1, item2));
            assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("11.00"));
        }
    }

    @Nested
    @DisplayName("place order (PENDING -> PAID)")
    class PlaceOrder {
        @Test
        void shouldPlaceOrderWhenTotalMeetsMinimum() {
            Order order = Order.create(customerId, validItems);
            order.place();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        }

        @Test
        void shouldRejectPlaceWhenTotalBelowMinimum() {
            OrderItem cheapItem = new OrderItem(UUID.randomUUID(), 1, new Money(new BigDecimal("5.00")));
            Order order = Order.create(customerId, List.of(cheapItem));
            assertThatThrownBy(order::place)
                    .isInstanceOf(InvalidOrderStateException.class)
                    .hasMessageContaining("10");
        }

        @Test
        void shouldAllowPlaceExactlyAtMinimum() {
            OrderItem item = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("5.00")));
            Order order = Order.create(customerId, List.of(item));
            order.place();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        }
    }

    @Nested
    @DisplayName("ship order (PAID -> SHIPPED)")
    class ShipOrder {
        @Test
        void shouldShipPaidOrder() {
            Order order = Order.create(customerId, validItems);
            order.place();
            order.ship();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        }

        @Test
        void shouldRejectShipPendingOrder() {
            Order order = Order.create(customerId, validItems);
            assertThatThrownBy(order::ship)
                    .isInstanceOf(InvalidOrderStateException.class)
                    .hasMessageContaining("PAID");
        }
    }

    @Nested
    @DisplayName("deliver order (SHIPPED -> DELIVERED)")
    class DeliverOrder {
        @Test
        void shouldDeliverShippedOrder() {
            Order order = Order.create(customerId, validItems);
            order.place();
            order.ship();
            order.deliver();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        void shouldRejectDeliverPendingOrder() {
            Order order = Order.create(customerId, validItems);
            assertThatThrownBy(order::deliver)
                    .isInstanceOf(InvalidOrderStateException.class);
        }
    }

    @Nested
    @DisplayName("cancel order")
    class CancelOrder {
        @Test
        void shouldCancelPendingOrder() {
            Order order = Order.create(customerId, validItems);
            order.cancel();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        void shouldCancelPaidOrder() {
            Order order = Order.create(customerId, validItems);
            order.place();
            order.cancel();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        void shouldRejectCancelShippedOrder() {
            Order order = Order.create(customerId, validItems);
            order.place();
            order.ship();
            assertThatThrownBy(order::cancel)
                    .isInstanceOf(InvalidOrderStateException.class)
                    .hasMessageContaining("SHIPPED");
        }

        @Test
        void shouldRejectCancelDeliveredOrder() {
            Order order = Order.create(customerId, validItems);
            order.place();
            order.ship();
            order.deliver();
            assertThatThrownBy(order::cancel)
                    .isInstanceOf(InvalidOrderStateException.class);
        }
    }
}
