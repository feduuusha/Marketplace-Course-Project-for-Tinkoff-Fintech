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
import ru.itis.marketplace.catalogservice.controller.payload.brand.NewBrandPayload;
import ru.itis.marketplace.catalogservice.controller.payload.brand.UpdateBrandPayload;
import ru.itis.marketplace.catalogservice.controller.payload.brand.UpdateBrandStatusPayload;
import ru.itis.marketplace.catalogservice.entity.Brand;
import ru.itis.marketplace.catalogservice.service.BrandService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BrandRestController.class})
@Import(SecurityBeans.class)
@ActiveProfiles("test")
class BrandRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrandService brandService;

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/brands/{brandId} should return brand")
    @WithMockUser(roles={"SERVICE"})
    void findBrandByIdSuccessfulTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        ObjectMapper mapper = new ObjectMapper();
        Brand brand = new Brand(2L, "name", "description", "link", "status", null, null);
        when(brandService.findBrandById(brandId)).thenReturn(brand);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/brands/{brandId}", brandId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Brand actualBrand = mapper.readValue(response, Brand.class);
        assertThat(actualBrand).isEqualTo(brand);
        verify(brandService).findBrandById(brandId);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/brands/{brandId}/links should return 401 because not auth-ed")
    @WithAnonymousUser
    void findAllBrandLinksUnSuccessfulTest() throws Exception {
        // Arrange
        Long brandId = 2L;

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/catalog/brands/{brandId}/links", brandId))
                .andExpect(status().is(401));
    }

    @Test
    @DisplayName("Method: PUT Endpoint: api/v1/catalog/brands/{brandId} should update brand")
    @WithMockUser(roles={"SERVICE"})
    void updateBrandByIdSuccessfulTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        String name = "name";
        String description = "description";
        String linkToLogo = "link";
        String requestStatus = "status";
        ObjectMapper mapper = new ObjectMapper();
        Brand brand = new Brand(2L, "name", "description", "link", "status", null, null);
        when(brandService.findBrandById(brandId)).thenReturn(brand);

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/catalog/brands/{brandId}", brandId)
                        .content(mapper.writeValueAsString(
                                new UpdateBrandPayload(name, description, linkToLogo, requestStatus)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(brandService).updateBrandById(brandId, name, description, linkToLogo, requestStatus);
    }

    @Test
    @DisplayName("Method: PUT Endpoint: api/v1/catalog/brands/{brandId} should return 400, because payload is incorrect")
    @WithMockUser(roles={"SERVICE"})
    void updateBrandByIdUnSuccessfulIncorrectPayloadTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        String name = "   ";
        String description = "description";
        String linkToLogo = "link";
        String requestStatus = "status";
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/catalog/brands/{brandId}", brandId)
                        .content(mapper.writeValueAsString(
                                new UpdateBrandPayload(name, description, linkToLogo, requestStatus)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: PUT Endpoint: api/v1/catalog/brands/{brandId} should return 401, because not auth-ed")
    @WithAnonymousUser
    void updateBrandByIdUnSuccessfulNotAuthenticatedTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        String name = null;
        String description = "description";
        String linkToLogo = "link";
        String requestStatus = "status";
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/catalog/brands/{brandId}", brandId)
                        .content(mapper.writeValueAsString(
                                new UpdateBrandPayload(name, description, linkToLogo, requestStatus)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: PATCH Endpoint: api/v1/catalog/brands/{brandId} should little bit update brand")
    @WithMockUser(roles={"SERVICE"})
    void updateBrandStatusByIdSuccessfulTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        String requestStatus = "status";
        ObjectMapper mapper = new ObjectMapper();
        Brand brand = new Brand(2L, "name", "description", "link", "status", null, null);
        when(brandService.findBrandById(brandId)).thenReturn(brand);

        // Act
        // Assert
        mockMvc.perform(patch("/api/v1/catalog/brands/{brandId}", brandId)
                        .content(mapper.writeValueAsString(
                                new UpdateBrandStatusPayload(requestStatus)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(brandService).updateBrandStatusById(brandId, requestStatus);
    }

    @Test
    @DisplayName("Method: PATCH Endpoint: api/v1/catalog/brands/{brandId} should return 400, because payload is incorrect")
    @WithMockUser(roles={"SERVICE"})
    void updateBrandStatusByIdUnSuccessfulPayloadIncorrectTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        String requestStatus = "   ";
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(patch("/api/v1/catalog/brands/{brandId}", brandId)
                        .content(mapper.writeValueAsString(
                                new UpdateBrandStatusPayload(requestStatus)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/catalog/brands/{brandId} should delete brand")
    @WithMockUser(roles = {"SERVICE"})
    void deleteBrandByIdSuccessfulTest() throws Exception {
        // Arrange
        Long brandId = 2L;

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/catalog/brands/{brandId}", brandId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(brandService).deleteBrandById(brandId);
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/catalog/brands/{brandId} should return 401, because not auth-ed")
    @WithAnonymousUser
    void deleteBrandByIdUnSuccessfulTest() throws Exception {
        // Arrange
        Long brandId = 2L;

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/catalog/brands/{brandId}", brandId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/brands should return all brands, because no parameters is provided")
    @WithMockUser(roles = {"SERVICE"})
    void findBrandsSuccessfulTest() throws Exception {
        // Arrange
        List<Brand> brands = List.of(
                new Brand(1L, "name" , "description", "link", null, null, null),
                new Brand(2L, "name" , "description", "link", null, null, null),
                new Brand(3L, "name" , "description", "link", null, null, null)
        );
        when(brandService.findAllBrands(null, null, null, null)).thenReturn(brands);
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/brands")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Brand> actualBrands = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualBrands).isEqualTo(brands);
        verify(brandService).findAllBrands(null, null, null, null);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/brands should return not all brands, because all parameters are provided")
    @WithMockUser(roles = {"SERVICE"})
    void findBrandsSuccessfulAllParametersTest() throws Exception {
        // Arrange
        String status = "status";
        Integer pageSize = 2;
        Integer page = 1;
        String sortedBy = "name";
        List<Brand> brands = List.of(
                new Brand(1L, "name" , "description", "link", null, null, null),
                new Brand(3L, "name" , "description", "link", null, null, null)
        );
        when(brandService.findAllBrands(status, pageSize, page, sortedBy)).thenReturn(brands);
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/brands"
                        + "?status={status}&page-size={page-size}&page={page}&sorted-by={sortedBy}", status, pageSize, page, sortedBy)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Brand> actualBrands = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualBrands).isEqualTo(brands);
        verify(brandService).findAllBrands(status, pageSize, page, sortedBy);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/brands should return 401, because not auth-ed")
    @WithAnonymousUser
    void findBrandsUnSuccessfulNotAuthenticatedTest() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/api/v1/catalog/brands")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/brands should create brand, because all ok")
    @WithMockUser(roles = {"SERVICE"})
    void createBrandSuccessfulTest() throws Exception {
        // Arrange
        String name = "name";
        String description = "description";
        String linkToLogo = "linkToLogo";
        Brand brand = new Brand(1L, name, description, linkToLogo, null, null, null);
        when(brandService.createBrand(name, description, linkToLogo)).thenReturn(brand);
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        String response = mockMvc.perform(post("/api/v1/catalog/brands")
                        .content(mapper.writeValueAsString(new NewBrandPayload(name, description, linkToLogo)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/catalog/brands/" + brand.getId()))
                .andReturn().getResponse().getContentAsString();
        Brand actualBrand = mapper.readValue(response, Brand.class);
        assertThat(actualBrand).isEqualTo(brand);
        verify(brandService).createBrand(name, description, linkToLogo);
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/brands should return 400, because parameter is incorrect")
    @WithMockUser(roles = {"SERVICE"})
    void createBrandUnSuccessfulIncorrectPayloadTest() throws Exception {
        // Arrange
        String name = "   ";
        String description = "description";
        String linkToLogo = "linkToLogo";
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/catalog/brands")
                        .content(mapper.writeValueAsString(new NewBrandPayload(name, description, linkToLogo)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/brands should return 401, because not auth-ed")
    @WithAnonymousUser
    void createBrandUnSuccessfulNotAuthenticatedTest() throws Exception {
        // Arrange
        String name = "   ";
        String description = "description";
        String linkToLogo = "linkToLogo";
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/catalog/brands")
                        .content(mapper.writeValueAsString(new NewBrandPayload(name, description, linkToLogo)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/brands/by-ids/{brandIds} should return brands by ids")
    @WithMockUser(roles = {"SERVICE"})
    void findAllBrandByIdsSuccessfulTest() throws Exception {
        // Arrange
        List<Long> brandIds = List.of(1L, 2L, 3L, 4L);
        List<Brand> brands = List.of(
            new Brand(1L, "name", "name", "name", null, null, null),
            new Brand(2L, "name", "name", "name", null, null, null),
            new Brand(3L, "name", "name", "name", null, null, null)
        );
        when(brandService.findAllBrandByIds(brandIds)).thenReturn(brands);
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/brands/by-ids/{brandIds}",
                String.join(",", brandIds.stream().map(String::valueOf).toList())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Brand> actualBrands = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualBrands).isEqualTo(brands);
        verify(brandService).findAllBrandByIds(brandIds);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/brands/by-ids/{brandIds} should return 401, because not auth-ed")
    @WithAnonymousUser
    void findAllBrandByIdsUnSuccessfulTest() throws Exception {
        // Arrange
        List<Long> brandIds = List.of(1L, 2L, 3L, 4L);

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/catalog/brands/by-ids/{brandIds}",
                String.join(",", brandIds.stream().map(String::valueOf).toList())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/brands/search should return brands by name")
    @WithMockUser(roles = {"SERVICE"})
    void findBrandsByNameLikeSuccessfulTest() throws Exception {
        // Arrange
        String name = "name";
        List<Brand> brands = List.of(
                new Brand(1L, "name", "name", "name", null, null, null),
                new Brand(2L, "name", "name", "name", null, null, null),
                new Brand(3L, "name", "name", "name", null, null, null)
        );
        when(brandService.findBrandsByNameLike(name)).thenReturn(brands);
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/brands/search?name={name}", name))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Brand> actualBrands = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualBrands).isEqualTo(brands);
        verify(brandService).findBrandsByNameLike(name);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/brands/search should return 401, because not auth-ed")
    @WithAnonymousUser
    void findBrandsByNameLikeUnSuccessfulTest() throws Exception {
        // Arrange
        String name = "name";

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/catalog/brands/search?name={name}", name))
                .andExpect(status().isUnauthorized());
    }
}
