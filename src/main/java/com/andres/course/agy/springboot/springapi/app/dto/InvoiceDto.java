package com.andres.course.agy.springboot.springapi.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record InvoiceDto(
    Long id,
    @NotNull(message = "Customer ID is mandatory")
    Long customerId,
    @NotNull(message = "Amount is mandatory")
    BigDecimal amount,
    String description,
    @NotBlank(message = "Status is mandatory")
    String status,
    List<InvoiceItemDto> items
) {}
