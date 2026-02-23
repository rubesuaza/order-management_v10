package com.example.order_management.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OrderId")
class OrderIdTest {

    @Nested
    @DisplayName("creation")
    class Creation {
        @Test
        void shouldCreateOrderIdWithValidUuid() {
            UUID value = UUID.randomUUID();
            OrderId orderId = new OrderId(value);
            assertThat(orderId.getValue()).isEqualTo(value);
        }

        @Test
        void shouldRejectNullValue() {
            assertThatThrownBy(() -> new OrderId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        void shouldGenerateNewOrderId() {
            OrderId orderId = OrderId.generate();
            assertThat(orderId).isNotNull();
            assertThat(orderId.getValue()).isNotNull();
        }
    }

    @Nested
    @DisplayName("equality")
    class Equality {
        @Test
        void shouldBeEqualWhenSameValue() {
            UUID value = UUID.randomUUID();
            OrderId id1 = new OrderId(value);
            OrderId id2 = new OrderId(value);
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        void shouldNotBeEqualWhenDifferentValues() {
            OrderId id1 = new OrderId(UUID.randomUUID());
            OrderId id2 = new OrderId(UUID.randomUUID());
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
