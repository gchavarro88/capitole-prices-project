package com.capitole.prices.infrastructure.adapters.input.rest.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class PriceResponse {
    private Long productId;
    private Integer brandId;
    private Integer priceList;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private double price;
    private String currency;
}
