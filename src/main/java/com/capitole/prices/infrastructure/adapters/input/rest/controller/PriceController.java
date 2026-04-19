package com.capitole.prices.infrastructure.adapters.input.rest.controller;

import com.capitole.prices.application.PriceService;
import com.capitole.prices.domain.model.Price;
import com.capitole.prices.domain.model.PriceQuery;
import com.capitole.prices.infrastructure.adapters.input.rest.mapper.PriceRestMapper;
import com.capitole.prices.infrastructure.adapters.input.rest.model.PriceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    public ResponseEntity<PriceResponse> getPrice(OffsetDateTime applicationDate, Long productId, Integer brandId) {
        PriceQuery query = PriceQuery.of(
                applicationDate.toLocalDateTime(),
                productId,
                brandId
        );

        Price price = PriceService.execute(query);
        PriceResponse response = PriceRestMapper.toResponse(price);

        return ResponseEntity.ok(response);
    }
}
