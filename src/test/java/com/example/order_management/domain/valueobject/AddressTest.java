package com.example.order_management.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Address Value Object")
class AddressTest {

    @Nested
    @DisplayName("creation")
    class Creation {
        @Test
        void shouldCreateAddressWithAllFields() {
            Address address = new Address("123 Main St", "New York", "10001", "USA");
            assertThat(address.getStreet()).isEqualTo("123 Main St");
            assertThat(address.getCity()).isEqualTo("New York");
            assertThat(address.getZipCode()).isEqualTo("10001");
            assertThat(address.getCountry()).isEqualTo("USA");
        }
    }

    @Nested
    @DisplayName("equality")
    class Equality {
        @Test
        void shouldBeEqualWhenAllFieldsMatch() {
            Address a1 = new Address("123 Main St", "New York", "10001", "USA");
            Address a2 = new Address("123 Main St", "New York", "10001", "USA");
            assertThat(a1).isEqualTo(a2);
            assertThat(a1.hashCode()).isEqualTo(a2.hashCode());
        }

        @Test
        void shouldNotBeEqualWhenFieldsDiffer() {
            Address a1 = new Address("123 Main St", "New York", "10001", "USA");
            Address a2 = new Address("456 Oak Ave", "Boston", "02101", "USA");
            assertThat(a1).isNotEqualTo(a2);
        }

        @Test
        void shouldBeEqualToSelf() {
            Address address = new Address("123 Main St", "New York", "10001", "USA");
            assertThat(address).isEqualTo(address);
        }

        @Test
        void shouldNotBeEqualToNull() {
            Address address = new Address("123 Main St", "New York", "10001", "USA");
            assertThat(address).isNotEqualTo(null);
        }
    }
}
