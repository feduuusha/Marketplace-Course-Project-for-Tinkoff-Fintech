//package ru.itis.catalog.controller;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import ru.itis.catalog.service.BrandService;
//
//import java.util.List;
//
//import static org.mockito.Mockito.doReturn;
//
//@ExtendWith(MockitoExtension.class)
//public class BrandRestControllerTest {
//    @Mock
//    private BrandService brandService;
//
//    @InjectMocks
//    private BrandRestController brandRestController;
//
//
//    @Test
//    @DisplayName("GET /api/v1/catalog/brands возращает HTTP-ответ со статусом 200 и списоком брендов")
//    void getAllBrandsEndpointTest() {
//        // given
//        List<BrandDto> brands = List.of(
//                new BrandDto(1L, "Puma", "Brother of Nike", "https://link.jpg", List.of(), List.of()),
//                new BrandDto(2L, "Nike", "Brother of Puma", "https://link.png", List.of(), List.of())
//        );
//        doReturn(ResponseEntity.ok(brands)).when(this.brandService).findAllBrands();
//
//        // when
//
//        ResponseEntity<?> response = this.brandRestController.getAllBrand();
//
//        // then
//        Assertions.assertNotNull(response);
//        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
//        Assertions.assertEquals(brands, response.getBody());
//    }
//
//    @Test
//    @DisplayName("GET /api/v1/catalog/brands/{id} возращает HTTP-ответ со статусом 200 и бренд с id из url")
//    void getBrandByIdEndpointTest() {
//        // given
//        BrandDto brandDto = new BrandDto(
//                1L, "name", "description", "https://link.ru", List.of(), List.of()
//        );
//        doReturn(ResponseEntity.ok(brandDto)).when(this.brandService).findBrandById(1L);
//
//        // when
//        ResponseEntity<?> response = brandRestController.findBrandById(1L);
//
//        //then
//        Assertions.assertNotNull(response);
//        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
//        Assertions.assertEquals(brandDto, response.getBody());
//
//    }
//}
