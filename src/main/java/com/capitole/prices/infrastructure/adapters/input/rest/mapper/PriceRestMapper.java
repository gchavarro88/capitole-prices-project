package com.capitole.prices.infrastructure.adapters.input.rest.mapper;

import com.capitole.prices.domain.model.Price;
import com.capitole.prices.infrastructure.adapters.input.rest.model.PriceResponse;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@NoArgsConstructor
public class PriceRestMapper {
    public static PriceResponse toResponse(Price price) {
        PriceResponse response = new PriceResponse();
        response.setProductId(price.getProductId());
        response.setBrandId(price.getBrandId());
        response.setPriceList(price.getPriceList());
        response.setStartDate(OffsetDateTime.of(price.getStartDate(), ZoneOffset.UTC));
        response.setEndDate(OffsetDateTime.of(price.getEndDate(), ZoneOffset.UTC));
        response.setPrice(price.getPrice().doubleValue());
        response.setCurrency(price.getCurrency());
        return response;
    }
}