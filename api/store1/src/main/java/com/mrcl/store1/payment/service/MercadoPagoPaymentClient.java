package com.mrcl.store1.payment.service;

import com.mrcl.store1.payment.dto.MercadoPagoPaymentResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

@Service
public class MercadoPagoPaymentClient {

    private final RestClient restClient;

    public MercadoPagoPaymentClient(
            @Value("${mercadopago.access-token}") String accessToken
    ) {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.mercadopago.com")
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken
                )
                .build();
    }

    public MercadoPagoPaymentResponse getPayment(Long paymentId) {
        return restClient.get()
                .uri("/v1/payments/{id}", paymentId)
                .retrieve()
                .body(MercadoPagoPaymentResponse.class);
    }
}