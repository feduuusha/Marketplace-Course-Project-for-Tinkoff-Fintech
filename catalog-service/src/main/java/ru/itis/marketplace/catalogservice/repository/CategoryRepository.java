package ru.itis.marketplace.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.marketplace.catalogservice.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
