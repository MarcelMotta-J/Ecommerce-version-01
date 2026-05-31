package com.mrcl.store1.payment.controller;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @PostMapping("/create-preference")
    public Map<String, String> createPreference() throws Exception {
        MercadoPagoConfig.setAccessToken(accessToken);

        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title("Ecommerce Test Order")
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .currencyId("BRL")
                .build();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(frontendUrl + "/payment/success")
                .failure(frontendUrl + "/payment/failure")
                .pending(frontendUrl + "/payment/pending")
                .build();


        PreferenceRequest request = PreferenceRequest.builder()
                .items(List.of(item))
                .backUrls(backUrls)
                .build();

        Preference preference = new PreferenceClient().create(request);

        return Map.of(
                "id", preference.getId(),
                "initPoint", preference.getInitPoint(),
                "sandboxInitPoint", preference.getSandboxInitPoint()
        );
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody(required = false) Map<String, Object> payload) {
        System.out.println("📩 Mercado Pago webhook:");
        System.out.println(payload);

        return ResponseEntity.ok().build();
    }


}
