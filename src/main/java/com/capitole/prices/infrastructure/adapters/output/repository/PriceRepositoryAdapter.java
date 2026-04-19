package com.capitole.prices.infrastructure.adapters.output.repository;

import com.capitole.prices.domain.model.Price;
import com.capitole.prices.domain.model.PriceQuery;
import com.capitole.prices.domain.port.output.PriceRepository;
import com.capitole.prices.infrastructure.adapters.output.mapper.PriceMapper;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PriceRepositoryAdapter implements PriceRepository {

    private final JpaPriceRepository jpaPriceRepository;

    public PriceRepositoryAdapter(JpaPriceRepository jpaPriceRepository) {
        this.jpaPriceRepository = jpaPriceRepository;
    }

    @Override
    public List<Price> findApplicablePrices(PriceQuery query) {
        return jpaPriceRepository.findApplicablePrices(
                        query.getProductId(),
                        query.getBrandId(),
                        query.getApplicationDate()
                ).stream()
                .map(PriceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
