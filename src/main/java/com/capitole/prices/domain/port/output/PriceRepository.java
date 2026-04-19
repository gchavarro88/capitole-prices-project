package com.capitole.prices.domain.port.output;

import com.capitole.prices.domain.model.Price;
import com.capitole.prices.domain.model.PriceQuery;

import java.util.List;

public interface PriceRepository {
    List<Price> findApplicablePrices(PriceQuery query);
}
