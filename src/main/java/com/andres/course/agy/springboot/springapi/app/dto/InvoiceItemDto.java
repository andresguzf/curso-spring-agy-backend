package com.andres.course.agy.springboot.springapi.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record InvoiceItemDto(
    Long id,
    @NotBlank(message = "Description is mandatory")
    String description,
    @NotNull(message = "Quantity is mandatory")
    Integer quantity,
    @NotNull(message = "Unit price is mandatory")
    BigDecimal unitPrice,
    @NotNull(message = "Total price is mandatory")
    BigDecimal totalPrice
) {}
