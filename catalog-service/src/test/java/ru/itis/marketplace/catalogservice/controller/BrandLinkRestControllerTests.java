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
import ru.itis.marketplace.catalogservice.entity.BrandLink;
import ru.itis.marketplace.catalogservice.service.BrandLinkService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {BrandLinkRestController.class})
@Import(SecurityBeans.class)
@ActiveProfiles("test")
class BrandLinkRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrandLinkService brandLinkService;

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/brands/{brandId}/links should return all brand links")
    @WithMockUser(roles={"SERVICE"})
    void findAllBrandLinksSuccessfulTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        ObjectMapper mapper = new ObjectMapper();
        List<BrandLink> brandLinks = List.of(
                new BrandLink(1L, "url", "name", brandId),
                new BrandLink(2L, "url2", "name2", brandId),
                new BrandLink(3L, "url3", "name3", brandId)
        );
        when(brandLinkService.findAllBrandLinks(brandId)).thenReturn(brandLinks);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/brands/{brandId}/links", brandId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<BrandLink> actualBrandLinks = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualBrandLinks).isEqualTo(brandLinks);
        verify(brandLinkService).findAllBrandLinks(brandId);
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
    @DisplayName("Method: POST Endpoint: api/v1/catalog/brands/{brandId}/links should create brand link")
    @WithMockUser(roles={"SERVICE"})
    void createBrandLinkSuccessfulTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        String url = "url";
        String name = "name";
        ObjectMapper mapper = new ObjectMapper();
        BrandLink brandLink = new BrandLink(100L, url, name, brandId);
        when(brandLinkService.createBrandLink(brandId, url, name)).thenReturn(brandLink);

        // Act
        // Assert
        String response = mockMvc.perform(post("/api/v1/catalog/brands/{brandId}/links", brandId)
                        .content("{\"url\" : \"url\", \"name\" : \"name\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/catalog/brands/2/links/" + brandLink.getId()))
                .andReturn().getResponse().getContentAsString();
        BrandLink actualBrandLink = mapper.readValue(response, BrandLink.class);
        assertThat(actualBrandLink).isEqualTo(brandLink);
        verify(brandLinkService).createBrandLink(brandId, url, name);
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/brands/{brandId}/links should return 400, because incorrect payload")
    @WithMockUser(roles={"SERVICE"})
    void createBrandLinkUnSuccessfulIncorrectPayloadTest() throws Exception {
        // Arrange
        Long brandId = 2L;

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/catalog/brands/{brandId}/links", brandId)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/brands/{brandId}/links should return 401, because not auth-ed")
    @WithAnonymousUser
    void createBrandLinkUnSuccessfulNotAuthorizedTest() throws Exception {
        // Arrange
        Long brandId = 2L;

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/catalog/brands/{brandId}/links", brandId)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/catalog/brands/{brandId}/links/{linkIds} should delete link with specified ids")
    @WithMockUser(roles={"SERVICE"})
    void deleteAllBrandLinkByIdSuccessfulTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        List<Long> linkIds = List.of(1L, 2L, 3L, 4L);

        // Act
        // Assert
        mockMvc.perform(
                delete("/api/v1/catalog/brands/{brandId}/links/{linkIds}",
                        brandId, String.join(",", linkIds.stream().map(String::valueOf).toList())))
                .andExpect(status().isNoContent());
        verify(brandLinkService).deleteAllBrandLinkById(linkIds);
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/catalog/brands/{brandId}/links/{linkIds} return 401, because not auth-ed")
    @WithAnonymousUser
    void deleteAllBrandLinkByIdUnSuccessfulNotAuthorizedTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        List<Long> linkIds = List.of(1L, 2L, 3L, 4L);

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/catalog/brands/{brandId}/links{linkIds}", brandId,
                        String.join(",", linkIds.stream().map(String::valueOf).toList()))
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
