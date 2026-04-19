package com.capitole.prices.infrastructure.adapters.output.mapper;

import com.capitole.prices.domain.model.Price;
import com.capitole.prices.infrastructure.adapters.output.entity.PriceEntity;

public class PriceMapper {

    private PriceMapper() {
    }

    public static Price toDomain(PriceEntity entity) {
        return Price.builder()
                .productId(entity.getProductId())
                .brandId(entity.getBrandId())
                .priceList(entity.getPriceList())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .price(entity.getPrice())
                .priority(entity.getPriority())
                .currency(entity.getCurrency())
                .build();
    }
}
