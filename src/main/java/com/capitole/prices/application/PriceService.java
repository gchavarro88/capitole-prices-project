package com.capitole.prices.application;

import com.capitole.prices.domain.exception.PriceNotFoundException;
import com.capitole.prices.domain.model.Price;
import com.capitole.prices.domain.model.PriceQuery;
import com.capitole.prices.domain.port.input.PriceUseCase;
import com.capitole.prices.domain.port.output.PriceRepository;

import java.util.Comparator;
import java.util.List;

public class PriceService implements PriceUseCase {
    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @Override
    public Price execute(PriceQuery query) {
        List<Price> applicablePrices = priceRepository.findApplicablePrices(query);

        return applicablePrices.stream()
                .filter(price -> price.isApplicableAt(query.getApplicationDate()))
                .max(Comparator.comparingInt(Price::getPriority))
                .orElseThrow(() -> new PriceNotFoundException(
                        String.format("No price found for productId=%d, brandId=%d at %s",
                                query.getProductId(), query.getBrandId(), query.getApplicationDate())
                ));
    }
}
