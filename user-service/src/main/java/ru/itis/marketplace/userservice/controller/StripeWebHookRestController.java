package ru.itis.marketplace.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.marketplace.userservice.service.WebHookService;

import java.util.Optional;

@Tag(name = "Stripe Web Hook Rest Controller", description = "Controller for handle Stripe webhooks")
@RestController
@RequestMapping("api/v1/webhooks/orders")
@RequiredArgsConstructor
public class StripeWebHookRestController {

    private final WebHookService webHookService;

    @Operation(
            summary = "Endpoint for receiving Stripe webhooks and change order status, not secured",
            responses = {
                    @ApiResponse(description = "WebHook successful handled", responseCode = "200", useReturnTypeSchema = true),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified or stripe signature is incorrect", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping("/change-order-status")
    public ResponseEntity<Void> catchPaymentIntentWebHook(@RequestHeader(name = "Stripe-Signature") String signature, @RequestBody String body) {
        webHookService.handlePaymentIntentWebHook(signature, body);
        return ResponseEntity.ok().build();
    }
}
