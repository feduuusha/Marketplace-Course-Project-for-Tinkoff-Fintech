package ru.itis.marketplace.userservice.model;

import java.time.LocalDateTime;
import java.util.List;

public record Product(
        Long id,
        String name,
        Double price,
        String description,
        String requestStatus,
        Category category,
        Brand brand,
        List<ProductPhoto> photos,
        List<ProductSize> sizes,
        LocalDateTime additionDateTime,
        LocalDateTime updateDateTime
) {
}
