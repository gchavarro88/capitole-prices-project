package com.capitole.prices.infrastructure.adapters.input.rest.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ErrorResponse {
    private String message;
    private OffsetDateTime timestamp;
}
