package com.capitole.prices.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceTest {

    @Test
    void shouldCreateValidPrice() {
        Price price = Price.builder()
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .price(new BigDecimal("35.50"))
                .priority(0)
                .currency("EUR")
                .build();

        assertThat(price).isNotNull();
        assertThat(price.getProductId()).isEqualTo(35455L);
        assertThat(price.getBrandId()).isEqualTo(1);
        assertThat(price.getPrice()).isEqualByComparingTo(new BigDecimal("35.50"));
    }

    @Test
    void shouldThrowExceptionWhenEndDateBeforeStartDate() {
        assertThatThrownBy(() -> Price.builder()
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(LocalDateTime.of(2020, 6, 14, 23, 59))
                .endDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .price(new BigDecimal("35.50"))
                .priority(0)
                .currency("EUR")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("endDate must be after or equal to startDate");
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNegative() {
        assertThatThrownBy(() -> Price.builder()
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .price(new BigDecimal("-10.00"))
                .priority(0)
                .currency("EUR")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price cannot be negative");
    }

    @Test
    void shouldReturnTrueWhenApplicationDateIsApplicable() {
        Price price = Price.builder()
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 6, 14, 23, 59))
                .price(new BigDecimal("35.50"))
                .priority(0)
                .currency("EUR")
                .build();

        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        assertThat(price.isApplicableAt(applicationDate)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenApplicationDateIsBeforeStartDate() {
        Price price = Price.builder()
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(LocalDateTime.of(2020, 6, 14, 15, 0))
                .endDate(LocalDateTime.of(2020, 6, 14, 23, 59))
                .price(new BigDecimal("35.50"))
                .priority(0)
                .currency("EUR")
                .build();

        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        assertThat(price.isApplicableAt(applicationDate)).isFalse();
    }

    @Test
    void shouldReturnFalseWhenApplicationDateIsAfterEndDate() {
        Price price = Price.builder()
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 6, 14, 18, 30))
                .price(new BigDecimal("35.50"))
                .priority(0)
                .currency("EUR")
                .build();

        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 21, 0);

        assertThat(price.isApplicableAt(applicationDate)).isFalse();
    }

    @Test
    void shouldReturnTrueWhenHasHigherPriorityThanOther() {
        Price highPriorityPrice = Price.builder()
                .productId(35455L)
                .brandId(1)
                .priceList(2)
                .startDate(LocalDateTime.of(2020, 6, 14, 15, 0))
                .endDate(LocalDateTime.of(2020, 6, 14, 18, 30))
                .price(new BigDecimal("25.45"))
                .priority(1)
                .currency("EUR")
                .build();

        Price lowPriorityPrice = Price.builder()
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .price(new BigDecimal("35.50"))
                .priority(0)
                .currency("EUR")
                .build();

        assertThat(highPriorityPrice.hasHigherPriorityThan(lowPriorityPrice)).isTrue();
        assertThat(lowPriorityPrice.hasHigherPriorityThan(highPriorityPrice)).isFalse();
    }
}
