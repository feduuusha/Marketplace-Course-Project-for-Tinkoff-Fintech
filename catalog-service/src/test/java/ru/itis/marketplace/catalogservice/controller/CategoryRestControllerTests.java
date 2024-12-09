package ru.itis.marketplace.catalogservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.itis.marketplace.catalogservice.config.SecurityBeans;
import ru.itis.marketplace.catalogservice.controller.payload.category.NewCategoryPayload;
import ru.itis.marketplace.catalogservice.controller.payload.category.UpdateCategoryPayload;
import ru.itis.marketplace.catalogservice.entity.Category;
import ru.itis.marketplace.catalogservice.service.CategoryService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CategoryRestController.class})
@Import(SecurityBeans.class)
@ActiveProfiles("test")
public class CategoryRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/categories/{categoryId} should return category")
    @WithMockUser(roles={"SERVICE"})
    public void findCategoryByIdSuccessfulTest() throws Exception {
        // Arrange
        Long categoryId = 2L;
        ObjectMapper mapper = new ObjectMapper();
        Category category = new Category(categoryId, "name");
        when(categoryService.findCategoryById(categoryId)).thenReturn(category);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/categories/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Category actualCategory = mapper.readValue(response, Category.class);
        assertThat(actualCategory).isEqualTo(category);
        verify(categoryService).findCategoryById(categoryId);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/categories/{categoryId} should return 401, because not auth-ed")
    @WithAnonymousUser()
    public void findCategoryByIdUnSuccessfulTest() throws Exception {
        // Arrange
        Long categoryId = 2L;

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/catalog/categories/{categoryId}", categoryId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: PUT Endpoint: api/v1/catalog/categories/{categoryId} should update category")
    @WithMockUser(roles={"SERVICE"})
    public void updateCategoryByIdSuccessfulTest() throws Exception {
        // Arrange
        Long categoryId = 2L;
        String name = "name";
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/catalog/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UpdateCategoryPayload(name))))
                .andExpect(status().isNoContent());
        verify(categoryService).updateCategoryById(categoryId, name);
    }

    @Test
    @DisplayName("Method: PUT Endpoint: api/v1/catalog/categories/{categoryId} should return 401, because not auth-ed")
    @WithAnonymousUser
    public void updateCategoryByIdUnSuccessfulNotAuthenticatedTest() throws Exception {
        // Arrange
        Long categoryId = 2L;
        String name = "name";
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/catalog/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UpdateCategoryPayload(name))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: PUT Endpoint: api/v1/catalog/categories/{categoryId} should return 400, because payload is incorrect")
    @WithMockUser(roles={"SERVICE"})
    public void updateCategoryByIdUnSuccessfulIncorrectPayloadTest() throws Exception {
        // Arrange
        Long categoryId = 2L;
        String name = "    ";
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/catalog/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UpdateCategoryPayload(name))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/catalog/categories/{categoryId} should delete category")
    @WithMockUser(roles={"SERVICE"})
    public void deleteCategoryByIdSuccessfulTest() throws Exception {
        // Arrange
        Long categoryId = 2L;

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/catalog/categories/{categoryId}", categoryId))
                .andExpect(status().isNoContent());
        verify(categoryService).deleteCategoryById(categoryId);
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/catalog/categories/{categoryId} should return 401, because not auth-ed")
    @WithAnonymousUser
    public void deleteCategoryByIdUnSuccessfulTest() throws Exception {
        // Arrange
        Long categoryId = 2L;

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/catalog/categories/{categoryId}", categoryId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/categories should create category")
    @WithMockUser(roles={"SERVICE"})
    public void createCategorySuccessfulTest() throws Exception {
        // Arrange
        Long categoryId = 2L;
        String name = "name";
        Category category = new Category(categoryId, name);
        when(categoryService.createCategory(name)).thenReturn(category);
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        String response = mockMvc.perform(post("/api/v1/catalog/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewCategoryPayload(name))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/catalog/categories/" + category.getId()))
                .andReturn().getResponse().getContentAsString();
        Category actualCategory = mapper.readValue(response, Category.class);
        assertThat(actualCategory).isEqualTo(category);
        verify(categoryService).createCategory(name);
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/categories should return 400, because payload is incorrect")
    @WithMockUser(roles={"SERVICE"})
    public void createCategoryUnSuccessfulIncorrectPayloadTest() throws Exception {
        // Arrange
        Long categoryId = 2L;
        String name = "   ";
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/catalog/categories", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewCategoryPayload(name))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/categories should return 401, because not auth-ed")
    @WithAnonymousUser
    public void createCategoryUnSuccessfulNotAuthenticatedTest() throws Exception {
        // Arrange
        Long categoryId = 2L;
        String name = "   ";
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/catalog/categories", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewCategoryPayload(name))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/categories should all categories")
    @WithMockUser(roles = {"SERVICE"})
    public void findCategoriesSuccessfulTest() throws Exception {
        // Arrange
        List<Category> categories = List.of(
                new Category(1L, "name1"),
                new Category(2L, "name2"),
                new Category(3L, "name3")
        );
        when(categoryService.findAllCategories()).thenReturn(categories);
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/categories"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Category> actualCategories = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualCategories).isEqualTo(categories);
        verify(categoryService).findAllCategories();
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/categories should return 401,because not auth-ed")
    @WithAnonymousUser
    public void findCategoriesUnSuccessfulTest() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/api/v1/catalog/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/categories/search should return categories by name")
    @WithMockUser(roles = {"SERVICE"})
    public void findCategoriesByNameLikeSuccessfulTest() throws Exception {
        // Arrange
        List<Category> categories = List.of(
                new Category(1L, "name"),
                new Category(2L, "name"),
                new Category(3L, "name")
        );
        String name = "name";
        when(categoryService.findCategoryByNameLike(name)).thenReturn(categories);
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/categories/search?name={name}", name))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Category> actualCategories = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualCategories).isEqualTo(categories);
        verify(categoryService).findCategoryByNameLike(name);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/categories/search should return 401, because not auth-ed")
    @WithAnonymousUser
    public void findCategoriesByNameLikeUnSuccessfulTest() throws Exception {
        // Arrange
        String name = "name";

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/catalog/categories/search?name={name}", name))
                .andExpect(status().isUnauthorized());
    }
}
