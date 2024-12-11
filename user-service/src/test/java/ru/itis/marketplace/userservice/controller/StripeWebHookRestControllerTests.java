package ru.itis.marketplace.userservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.itis.marketplace.userservice.config.SecurityBeans;
import ru.itis.marketplace.userservice.service.WebHookService;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {StripeWebHookRestController.class})
@ActiveProfiles("test")
@Import(SecurityBeans.class)
class StripeWebHookRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebHookService webHookService;

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/webhooks/orders/change-order-status should change order status with specified payment id")
    @WithAnonymousUser
    void catchPaymentIntentWebHookSuccessfulTest() throws Exception {
        // Arrange
        String signature = "signature";
        String body = "body";

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/webhooks/orders/change-order-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Stripe-Signature", signature))
                .andExpect(status().isOk());
        verify(webHookService).handlePaymentIntentWebHook(signature, body);
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/webhooks/orders/change-order-status should return 400, because signature is not provided ")
    @WithAnonymousUser
    void catchPaymentIntentWebHookUnSuccessfulTest() throws Exception {
        // Arrange
        String body = "body";

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/webhooks/orders/change-order-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
