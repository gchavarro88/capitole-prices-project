package com.capitole.prices.application;

import com.capitole.prices.domain.exception.PriceNotFoundException;
import com.capitole.prices.domain.model.Price;
import com.capitole.prices.domain.model.PriceQuery;
import com.capitole.prices.domain.port.output.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private PriceService priceService;

    private Price basePrice;
    private Price promotionPrice;

    @BeforeEach
    void setUp() {
        basePrice = Price.builder()
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .price(new BigDecimal("35.50"))
                .priority(0)
                .currency("EUR")
                .build();

        promotionPrice = Price.builder()
                .productId(35455L)
                .brandId(1)
                .priceList(2)
                .startDate(LocalDateTime.of(2020, 6, 14, 15, 0))
                .endDate(LocalDateTime.of(2020, 6, 14, 18, 30))
                .price(new BigDecimal("25.45"))
                .priority(1)
                .currency("EUR")
                .build();
    }

    /**
     * Should return the base price when applicationDate is not in a promotion date range with a higher priority
     */
    @Test
    void shouldReturnBasePriceWhenNoOverlap() {
        // Test 1 scenario: 10:00 on 14th June
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
        PriceQuery query = PriceQuery.of(applicationDate, 35455L, 1);

        when(priceRepository.findApplicablePrices(query))
                .thenReturn(Collections.singletonList(basePrice));

        Price result = priceService.execute(query);

        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(35455L);
        assertThat(result.getBrandId()).isEqualTo(1);
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(result.getPriceList()).isEqualTo(1);
    }

    /**
     * Should return the promotion price when applicationDate is in a promotion date range with a higher priority
     */
    @Test
    void shouldReturnHigherPriorityPriceWhenMultipleApplicable() {
        // Test 2 scenario: 16:00 on 14th June (overlapping prices)
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 16, 0);
        PriceQuery query = PriceQuery.of(applicationDate, 35455L, 1);

        when(priceRepository.findApplicablePrices(query))
                .thenReturn(Arrays.asList(basePrice, promotionPrice));

        Price result = priceService.execute(query);

        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(35455L);
        assertThat(result.getBrandId()).isEqualTo(1);
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("25.45"));
        assertThat(result.getPriceList()).isEqualTo(2);
        assertThat(result.getPriority()).isEqualTo(1);
    }

    /**
     * Should return the base price when applicationDate is not after the promotion dates range
     */
    @Test
    void shouldReturnBasePriceAfterPromotionEnds() {
        // Test 3 scenario: 21:00 on 14th June (after promotion)
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 21, 0);
        PriceQuery query = PriceQuery.of(applicationDate, 35455L, 1);

        when(priceRepository.findApplicablePrices(query))
                .thenReturn(Collections.singletonList(basePrice));

        Price result = priceService.execute(query);

        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(result.getPriceList()).isEqualTo(1);
    }

    /**
     * Should return and error with price not found message
     */
    @Test
    void shouldThrowExceptionWhenNoPriceFound() {
        LocalDateTime applicationDate = LocalDateTime.of(2021, 1, 1, 10, 0);
        PriceQuery query = PriceQuery.of(applicationDate, 99999L, 1);

        when(priceRepository.findApplicablePrices(query))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> priceService.execute(query))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining("No price found");
    }

    /**
     * Validates that the application date is inside of the range of start date and end date
     */

    @Test
    void shouldFilterApplicablePricesByDateRange() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 16, 0);
        PriceQuery query = PriceQuery.of(applicationDate, 35455L, 1);

        List<Price> prices = Arrays.asList(basePrice, promotionPrice);
        when(priceRepository.findApplicablePrices(query)).thenReturn(prices);

        Price result = priceService.execute(query);

        assertThat(result.isApplicableAt(applicationDate)).isTrue();
    }
}
