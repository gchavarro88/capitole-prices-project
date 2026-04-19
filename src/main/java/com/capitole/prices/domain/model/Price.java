package com.capitole.prices.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Price {
    private final Long productId;
    private final Integer brandId;
    private final Integer priceList;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final BigDecimal price;
    private final Integer priority;
    private final String currency;

    Price(Builder builder) {
        this.productId = Objects.requireNonNull(builder.productId, "productId cannot be null");
        this.brandId = Objects.requireNonNull(builder.brandId, "brandId cannot be null");
        this.priceList = Objects.requireNonNull(builder.priceList, "priceList cannot be null");
        this.startDate = Objects.requireNonNull(builder.startDate, "startDate cannot be null");
        this.endDate = Objects.requireNonNull(builder.endDate, "endDate cannot be null");
        this.price = Objects.requireNonNull(builder.price, "price cannot be null");
        this.priority = Objects.requireNonNull(builder.priority, "priority cannot be null");
        this.currency = Objects.requireNonNull(builder.currency, "currency cannot be null");

        validateDateRange();
        validatePrice();
    }

    private void validateDateRange() {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must be after or equal to startDate");
        }
    }

    private void validatePrice() {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("price cannot be negative");
        }
    }

    public boolean isApplicableAt(LocalDateTime applicationDate) {
        return !applicationDate.isBefore(startDate) && !applicationDate.isAfter(endDate);
    }

    public boolean hasHigherPriorityThan(Price other) {
        return this.priority > other.priority;
    }

    // Getters
    public Long getProductId() {
        return productId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public Integer getPriceList() {
        return priceList;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getPriority() {
        return priority;
    }

    public String getCurrency() {
        return currency;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long productId;
        private Integer brandId;
        private Integer priceList;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private BigDecimal price;
        private Integer priority;
        private String currency;

        public Builder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder brandId(Integer brandId) {
            this.brandId = brandId;
            return this;
        }

        public Builder priceList(Integer priceList) {
            this.priceList = priceList;
            return this;
        }

        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder priority(Integer priority) {
            this.priority = priority;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Price build() {
            return new Price(this);
        }
    }
}
