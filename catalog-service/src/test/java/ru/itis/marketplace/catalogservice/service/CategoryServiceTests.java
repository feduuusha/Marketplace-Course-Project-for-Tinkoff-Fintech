package ru.itis.marketplace.catalogservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.itis.marketplace.catalogservice.entity.Category;
import ru.itis.marketplace.catalogservice.entity.Product;
import ru.itis.marketplace.catalogservice.entity.ProductSize;
import ru.itis.marketplace.catalogservice.exception.BadRequestException;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.kafka.KafkaProducer;
import ru.itis.marketplace.catalogservice.repository.CategoryRepository;
import ru.itis.marketplace.catalogservice.service.impl.CategoryServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {CategoryServiceImpl.class})
@ActiveProfiles("test")
class CategoryServiceTests {

    @Autowired
    private CategoryService categoryService;

    @MockBean
    private CategoryRepository categoryRepository;
    @MockBean
    private ProductService productService;
    @MockBean
    private KafkaProducer kafkaProducer;
    @MockBean
    private MeterRegistry meterRegistry;
    @MockBean
    private Counter counter;

    @Test
    @DisplayName("findCategoryById should return category, because categoryId is correct")
    void findCategoryByIdSuccessfulTest() {
        // Arrange
        Long categoryId = 2L;
        String categoryName = "name";
        Category category = new Category(categoryId, categoryName);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        Category actualCategory = categoryService.findCategoryById(categoryId);

        // Assert
        assertThat(actualCategory).isEqualTo(category);
    }

    @Test
    @DisplayName("findCategoryById should throw NotFoundException, because categoryId is incorrect")
    void findCategoryByIdUnSuccessfulTest() {
        // Arrange
        Long categoryId = 2L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> categoryService.findCategoryById(categoryId))
                .withMessage("Category with ID: "  + categoryId + " not found");
    }

    @Test
    @DisplayName("updateCategoryById should update category, because categoryId is correct")
    void updateCategoryByIdSuccessfulTest() {
        // Arrange
        Long id = 2L;
        String name = "name";
        Category category = Mockito.mock();
        when(category.getName()).thenReturn("oldName");
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName(name)).thenReturn(Optional.empty());

        // Act
        categoryService.updateCategoryById(id, name);

        // Assert
        verify(category).setName(name);
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("updateCategoryById should throw NotFoundException, because categoryId is incorrect")
    void updateCategoryByIdUnSuccessfulIncorrectCategoryIdTest() {
        // Arrange
        Long id = 2L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> categoryService.updateCategoryById(id, "name"))
                .withMessage("Category with ID: " + id + " not found");
    }

    @Test
    @DisplayName("updateCategoryById should throw BadRequestException, because category with provided name already exist")
    void updateCategoryByIdUnSuccessfulAlreadyExistNameTest() {
        // Arrange
        Long id = 2L;
        String name = "name";
        Category category = Mockito.mock();
        Category categoryWithSameName = new Category(name);
        when(category.getName()).thenReturn("oldName");
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName(name)).thenReturn(Optional.of(categoryWithSameName));

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> categoryService.updateCategoryById(id, "name"))
                .withMessage("Category with name: " + name + " already exist");
    }

    @Test
    @DisplayName("updateCategoryById should do nothing, because provided name is same with stored")
    void updateCategoryByIdSuccessfulSameNameTest() {
        // Arrange
        Long id = 2L;
        String name = "name";
        Category category = Mockito.mock();
        when(category.getName()).thenReturn(name);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        // Act
        categoryService.updateCategoryById(id, name);

        // Assert
        verify(category).setName(name);
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("deleteCategoryById should delete category and send messages in kafka, because categoryId is correct")
    void deleteCategoryByIdSuccessfulTest() {
        // Arrange
        Long categoryId = 2L;
        List<Product> productList = List.of(
                new Product(1L, null, null, null, null, categoryId, null, null,
                        List.of(new ProductSize(1L, null, 1L), new ProductSize(2L, null, 1L)), null, null),
                new Product(2L, null, null, null, null, categoryId, null, null,
                        List.of(new ProductSize(3L, null, 2L), new ProductSize(4L, null, 2L)), null, null));
        when(productService.findAllProducts(null, null, null, null,
                null, null, null, null, categoryId))
                .thenReturn(productList);

        // Act
        categoryService.deleteCategoryById(categoryId);

        // Assert
        verify(kafkaProducer).sendSizeIds(List.of(1L, 2L, 3L, 4L));
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    @DisplayName("createCategory should create category, because provided name is free")
    void createCategorySuccessfulTest() {
        // Arrange
        String name = "name";
        when(categoryRepository.findByName(name)).thenReturn(Optional.empty());
        when(meterRegistry.counter(anyString())).thenReturn(counter);
        when(categoryRepository.save(any())).thenReturn(new Category(name));

        // Act
        Category category = categoryService.createCategory(name);

        // Assert
        assertThat(category.getName()).isEqualTo(name);
        verify(categoryRepository).save(any());
    }

    @Test
    @DisplayName("createCategory should throw BadRequestException, because provided name is already exist")
    void createCategoryUnSuccessfulTest() {
        // Arrange
        String name = "name";
        when(categoryRepository.findByName(name)).thenReturn(Optional.of(new Category(name)));

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> categoryService.createCategory(name))
                .withMessage("Category with name: " + name + " already exist");
    }


    @Test
    @DisplayName("findAllCategories should call categoryRepository.findAll")
    void findAllCategoriesSuccessfulTest() {
        // Arrange
        // Act
        categoryService.findAllCategories();

        // Assert
        verify(categoryRepository).findAll(Sort.by("name"));
    }

    @Test
    @DisplayName("findCategoryByNameLike should call categoryRepository.findByNameLikeIgnoreCase")
    void findCategoryByNameLikeSuccessfulTest() {
        // Arrange
        String name = "name";
        // Act
        categoryService.findCategoryByNameLike(name);

        // Assert
        verify(categoryRepository).findByNameLikeIgnoreCase(name);
    }
}
