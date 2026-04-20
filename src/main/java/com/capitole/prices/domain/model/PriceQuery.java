package com.capitole.prices.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class PriceQuery {
    private final LocalDateTime applicationDate;
    private final Long productId;
    private final Integer brandId;

    PriceQuery(LocalDateTime applicationDate, Long productId, Integer brandId) {
        this.applicationDate = Objects.requireNonNull(applicationDate, "applicationDate cannot be null");
        this.productId = Objects.requireNonNull(productId, "productId cannot be null");
        this.brandId = Objects.requireNonNull(brandId, "brandId cannot be null");
        validateNullValue(applicationDate, "applicationDate");
        validateNullValue(productId, "productId");
        isGreaterThanZero(productId, "productId");
        validateNullValue(brandId, "brandId");
        isGreaterThanZero(brandId, "brandId");
    }

    public static PriceQuery of(LocalDateTime applicationDate, Long productId, Integer brandId) {
        return new PriceQuery(applicationDate, productId, brandId);
    }

    // Getters
    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void isGreaterThanZero(Number number, String fieldName) {
        if (number.longValue() <= 0) {
            throw new IllegalArgumentException(String.format("%s should be greater than zero", fieldName));
        }
    }

    public void validateNullValue(Object field, String fieldName) {
        if (Objects.isNull(field)) {
            throw new IllegalArgumentException(String.format("%s shouldn't be null", fieldName));
        }
    }
}