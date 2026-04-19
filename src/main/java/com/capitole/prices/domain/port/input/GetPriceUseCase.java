package com.capitole.prices.domain.port.input;

import com.capitole.prices.domain.model.Price;
import com.capitole.prices.domain.model.PriceQuery;

public interface GetPriceUseCase {
    Price execute(PriceQuery query);
}
