package com.oms.order.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotNull(message = "productId is required") Long productId,
        @Min(value = 1, message = "quantity must be at least 1") int quantity,
        @NotBlank(message = "customerEmail is required")
        @Email(message = "customerEmail must be a valid email") String customerEmail) {
}
