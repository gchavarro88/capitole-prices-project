package com.capitole.prices.domain.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class PriceQuery {
    private final LocalDateTime applicationDate;
    private final Long productId;
    private final Integer brandId;

    PriceQuery(LocalDateTime applicationDate, Long productId, Integer brandId) {
        this.applicationDate = Objects.requireNonNull(applicationDate, "applicationDate cannot be null");
        this.productId = Objects.requireNonNull(productId, "productId cannot be null");
        this.brandId = Objects.requireNonNull(brandId, "brandId cannot be null");
    }

    public static PriceQuery of(LocalDateTime applicationDate, Long productId, Integer brandId) {
        return new PriceQuery(applicationDate, productId, brandId);
    }
}