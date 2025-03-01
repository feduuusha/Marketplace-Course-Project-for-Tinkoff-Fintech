package ru.itis.marketplace.userservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Product(
        Long id,
        String name,
        BigDecimal price,
        String description,
        String requestStatus,
        Long categoryId,
        Long brandId,
        List<ProductPhoto> photos,
        List<ProductSize> sizes,
        LocalDateTime additionDateTime,
        LocalDateTime updateDateTime
) {
}
