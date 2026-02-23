package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.InvalidItemException;
import com.example.order_management.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OrderItem")
class OrderItemTest {

    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final Money VALID_PRICE = new Money(new BigDecimal("5.00"));

    @Nested
    @DisplayName("creation")
    class Creation {
        @Test
        void shouldCreateValidOrderItem() {
            OrderItem item = new OrderItem(PRODUCT_ID, 2, VALID_PRICE);
            assertThat(item.getProductId()).isEqualTo(PRODUCT_ID);
            assertThat(item.getQuantity()).isEqualTo(2);
            assertThat(item.getUnitPrice()).isEqualTo(VALID_PRICE);
            assertThat(item.getLineTotal()).isEqualTo(new Money(new BigDecimal("10.00")));
        }

        @Test
        void shouldRejectZeroQuantity() {
            assertThatThrownBy(() -> new OrderItem(PRODUCT_ID, 0, VALID_PRICE))
                    .isInstanceOf(InvalidItemException.class)
                    .hasMessageContaining("quantity");
        }

        @Test
        void shouldRejectNegativeQuantity() {
            assertThatThrownBy(() -> new OrderItem(PRODUCT_ID, -1, VALID_PRICE))
                    .isInstanceOf(InvalidItemException.class)
                    .hasMessageContaining("quantity");
        }

        @Test
        void shouldRejectNegativeUnitPrice() {
            Money negativePrice = new Money(new BigDecimal("-5.00"));
            assertThatThrownBy(() -> new OrderItem(PRODUCT_ID, 1, negativePrice))
                    .isInstanceOf(InvalidItemException.class)
                    .hasMessageContaining("unitPrice");
        }

        @Test
        void shouldRejectNullProductId() {
            assertThatThrownBy(() -> new OrderItem(null, 1, VALID_PRICE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("productId");
        }

        @Test
        void shouldRejectNullUnitPrice() {
            assertThatThrownBy(() -> new OrderItem(PRODUCT_ID, 1, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("unitPrice");
        }
    }

    @Nested
    @DisplayName("line total calculation")
    class LineTotal {
        @Test
        void shouldCalculateLineTotalCorrectly() {
            OrderItem item = new OrderItem(PRODUCT_ID, 3, new Money(new BigDecimal("2.50")));
            assertThat(item.getLineTotal().getAmount()).isEqualByComparingTo(new BigDecimal("7.50"));
        }
    }
}
