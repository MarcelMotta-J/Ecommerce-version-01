package com.mrcl.store1.payment.controller;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.merchantorder.MerchantOrderClient;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.merchantorder.MerchantOrder;
import com.mercadopago.resources.merchantorder.MerchantOrderPayment;

import com.mercadopago.resources.preference.Preference;

import com.mrcl.store1.dao.OrderRepository;
import com.mrcl.store1.entity.Order;
import com.mrcl.store1.entity.OrderStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mrcl.store1.payment.service.MercadoPagoPaymentClient;
import com.mrcl.store1.payment.dto.MercadoPagoPaymentResponse;

import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final MercadoPagoPaymentClient mercadoPagoPaymentClient;

    private final OrderRepository orderRepository;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.notification-url}")
    private String notificationUrl;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public PaymentController(MercadoPagoPaymentClient mercadoPagoPaymentClient, OrderRepository orderRepository) {
        this.mercadoPagoPaymentClient = mercadoPagoPaymentClient;
        this.orderRepository = orderRepository;
    }

    public record CreatePreferenceRequest(Long orderId) {
    }

    @PostMapping("/create-preference")
    public Map<String, String> createPreference(
            @RequestBody CreatePreferenceRequest requestBody
    ) throws Exception {

        MercadoPagoConfig.setAccessToken(accessToken);

        Order order = orderRepository.findById(requestBody.orderId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + requestBody.orderId()
                        )
                );

        // Prevent the creation of a new payment preference for an order
        // that has already been paid.
        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Order is already paid");
        }

        // Prevent creating multiple Mercado Pago
        // preferences for the same order.
        if (order.getMercadoPagoPreferenceId() != null) {
            throw new IllegalStateException(
                    "Order already has a Mercado Pago preference"
            );
        }

        // The item amount is always obtained from the database.
        // Never trust an amount received from the frontend.
        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .id(order.getId().toString())
                .title("Order " + order.getOrderTrackingNumber())
                .description("Payment for order " + order.getId())
                .quantity(1)
                .unitPrice(order.getTotalPrice())
                .currencyId("BRL")
                .build();

        // These URLs are used after the buyer finishes or leaves checkout.
        PreferenceBackUrlsRequest backUrls =
                PreferenceBackUrlsRequest.builder()
                        .success(frontendUrl + "/payment/success")
                        .failure(frontendUrl + "/payment/failure")
                        .pending(frontendUrl + "/payment/pending")
                        .build();

        PreferenceRequest preferenceRequest =
                PreferenceRequest.builder()
                        .items(List.of(item))
                        .backUrls(backUrls)
                        .autoReturn("approved")
                        .externalReference(order.getId().toString())
                        .notificationUrl(notificationUrl)
                        .build();

        Preference preference =
                new PreferenceClient().create(preferenceRequest);

        System.out.println("Preference ID: " + preference.getId());
        System.out.println(
                "External reference: "
                        + preference.getExternalReference()
        );
        System.out.println(
                "Production init point: "
                        + preference.getInitPoint()
        );
        System.out.println(
                "Sandbox init point: "
                        + preference.getSandboxInitPoint()
        );
        System.out.println(
                "Notification URL: "
                        + preference.getNotificationUrl()
        );

        order.setMercadoPagoPreferenceId(preference.getId());
        orderRepository.save(order);

        return Map.of(
                "orderId", order.getId().toString(),
                "id", preference.getId(),
                "initPoint", preference.getInitPoint(),
                "sandboxInitPoint", preference.getSandboxInitPoint()
        );
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestBody(required = false)
            Map<String, Object> payload,

            // Legacy format
            // ?id=123&topic=payment
            @RequestParam(
                    name = "id",
                    required = false
            )
            String legacyId,

            @RequestParam(
                    name = "topic",
                    required = false
            )
            String legacyTopic,

            // Current format
            // ?data.id=123&type=payment
            @RequestParam(
                    name = "data.id",
                    required = false
            )
            String currentDataId,

            @RequestParam(
                    name = "type",
                    required = false
            )
            String currentType
    ) {

        System.out.println("Mercado Pago webhook received");
        System.out.println("Legacy topic: " + legacyTopic);
        System.out.println("Legacy ID: " + legacyId);
        System.out.println("Current type: " + currentType);
        System.out.println("Current data.id: " + currentDataId);
        System.out.println("Payload: " + payload);

        MercadoPagoConfig.setAccessToken(accessToken);

        try {
            String notificationType = resolveNotificationType(
                    currentType,
                    legacyTopic,
                    payload
            );

            /*
             * MERCHANT ORDER
             *
             * Legacy format:
             * ?id=123&topic=merchant_order
             *
             * We also inspect the payload to keep
             * compatibility with older notifications.
             */
            if (isMerchantOrderNotification(
                    notificationType,
                    payload
            )) {

                String merchantOrderId = firstNonBlank(
                        currentDataId,
                        legacyId,
                        resolveMerchantOrderId(null, payload)
                );

                if (merchantOrderId == null) {
                    System.out.println(
                            "Merchant Order notification without resource ID"
                    );

                    return ResponseEntity.ok().build();
                }

                // Merchant Order does not contain payments yet.
                processMerchantOrder(
                        Long.parseLong(merchantOrderId)
                );

                return ResponseEntity.ok().build();
            }

            /*
             * PAYMENT
             *
             * Current format:
             * ?data.id=123&type=payment
             *
             * Legacy format:
             * ?id=123&topic=payment
             */
            if (isPaymentNotification(
                    notificationType,
                    payload
            )) {

                String paymentId = firstNonBlank(
                        currentDataId,
                        legacyId,
                        extractPaymentId(payload)
                );

                if (paymentId == null) {
                    System.out.println(
                            "Payment notification without resource ID"
                    );

                    return ResponseEntity.ok().build();
                }

                processPayment(
                        Long.parseLong(paymentId)
                );

                return ResponseEntity.ok().build();
            }

            /*
             * Webhooks may contain other event types
             * in the future (such as chargebacks).
             * Unknown resources must not be treated
             * as payment notifications.
             */
            System.out.println(
                    "Unsupported notification type: "
                            + notificationType
            );

            return ResponseEntity.ok().build();

        } catch (NumberFormatException exception) {
            System.out.println(
                    "Invalid Mercado Pago resource ID: "
                            + exception.getMessage()
            );

            return ResponseEntity.badRequest().build();

        } catch (Exception exception) {
            System.out.println(
                    "Error while processing Mercado Pago webhook: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            /*
             * Returning HTTP 5xx indicates an internal error.
             * Mercado Pago may retry delivering
             * this notification.
             */
            return ResponseEntity.internalServerError().build();
        }
    }

    private String resolveNotificationType(
            String currentType,
            String legacyTopic,
            Map<String, Object> payload
    ) {

        String type = firstNonBlank(
                currentType,
                legacyTopic,
                extractString(payload, "type"),
                extractString(payload, "topic")
        );

        if (type == null) {
            return null;
        }

        return type
                .trim()
                .toLowerCase();
    }

    private boolean isPaymentNotification(
            String notificationType,
            Map<String, Object> payload
    ) {

        if ("payment".equals(notificationType)
                || "payments".equals(notificationType)) {
            return true;
        }

        Object action = payload == null
                ? null
                : payload.get("action");

        return action != null
                && action.toString()
                .toLowerCase()
                .startsWith("payment.");
    }

    private boolean isMerchantOrderNotification(
            String notificationType,
            Map<String, Object> payload
    ) {

        if ("merchant_order".equals(notificationType)
                || "merchant_orders".equals(notificationType)) {
            return true;
        }

        return isMerchantOrderPayload(payload);
    }

    private String extractString(
            Map<String, Object> payload,
            String key
    ) {

        if (payload == null) {
            return null;
        }

        Object value = payload.get(key);

        if (value == null) {
            return null;
        }

        String text = value.toString().trim();

        return text.isBlank()
                ? null
                : text;
    }

    private String firstNonBlank(String... values) {

        if (values == null) {
            return null;
        }

        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }

        return null;
    }



    private void processMerchantOrder(
            Long merchantOrderId
    ) throws Exception {

        MerchantOrder merchantOrder =
                new MerchantOrderClient().get(merchantOrderId);

        System.out.println(
                "Merchant Order ID: " + merchantOrder.getId()
        );
        System.out.println(
                "Merchant Order status: "
                        + merchantOrder.getStatus()
        );
        System.out.println(
                "Merchant Order external reference: "
                        + merchantOrder.getExternalReference()
        );

        // Merchant Order does not contain payments yet.
        List<MerchantOrderPayment> payments =
                merchantOrder.getPayments();

        // A Merchant Order may be created before
        // any payment is associated with it.
        if (payments == null || payments.isEmpty()) {
            System.out.println(
                    "Merchant Order does not contain payments yet"
            );
            return;
        }

        for (MerchantOrderPayment payment : payments) {

            if (payment == null || payment.getId() == null) {
                System.out.println(
                        "Merchant Order contains a payment without ID"
                );
                continue;
            }

            System.out.println(
                    "Payment found in Merchant Order: "
                            + payment.getId()
            );

            System.out.println(
                    "Payment summary status: "
                            + payment.getStatus()
            );

            processPayment(payment.getId());
        }
    }


    private void processPayment(Long paymentId) throws Exception {

        if (paymentId == null) {
            System.out.println(
                    "Payment notification does not contain a payment ID"
            );
            return;
        }

        final MercadoPagoPaymentResponse payment;

        try {
            payment = mercadoPagoPaymentClient.getPayment(paymentId);
        } catch (HttpClientErrorException.NotFound exception) {

            // The payment resource may not be available yet.
            // Wait for a future webhook notification.
            System.out.println(
                    "Mercado Pago payment "
                            + paymentId
                            + " is not available yet. "
                            + "Waiting for another webhook."
            );

            return;
        }

        String externalReference = payment.externalReference();

        String paymentStatus = payment.status();

        String statusDetail = payment.statusDetail();

        System.out.println(
                "Mercado Pago payment ID: " + paymentId
        );
        System.out.println(
                "Mercado Pago payment status: " + paymentStatus
        );
        System.out.println(
                "Mercado Pago status detail: " + statusDetail
        );
        System.out.println(
                "Mercado Pago external reference: "
                        + externalReference
        );

        if (externalReference == null
                || externalReference.isBlank()) {

            System.out.println(
                    "Payment does not contain an external reference"
            );
            return;
        }

        final Long orderId;

        try {
            orderId = Long.valueOf(
                    externalReference.trim()
            );
        } catch (NumberFormatException exception) {
            System.out.println(
                    "Invalid external reference: "
                            + externalReference
            );
            return;
        }

        Optional<Order> optionalOrder =
                orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {

            System.out.println(
                    "Order not found: " + orderId
            );

            return;
        }

        Order order = optionalOrder.get();

        if (paymentStatus == null
                || paymentStatus.isBlank()) {

            System.out.println(
                    "Payment does not contain a valid status"
            );
            return;
        }

        String normalizedStatus = paymentStatus
                .trim()
                .toLowerCase();

        OrderStatus currentOrderStatus =
                order.getStatus();

        OrderStatus newOrderStatus =
                resolveOrderStatus(
                        normalizedStatus,
                        currentOrderStatus
                );

        boolean paymentIdChanged =
                !paymentId.toString().equals(
                        order.getMercadoPagoPaymentId()
                );

        boolean orderStatusChanged =
                newOrderStatus != currentOrderStatus;

        if (paymentIdChanged) {
            order.setMercadoPagoPaymentId(
                    paymentId.toString()
            );
        }

        if (orderStatusChanged) {
            order.setStatus(newOrderStatus);
        }

        if (!paymentIdChanged
                && !orderStatusChanged) {

            System.out.println(
                    "Order " + orderId
                            + " is already synchronized"
            );
            return;
        }

        orderRepository.save(order);

        System.out.println(
                "Order " + orderId
                        + " updated from "
                        + currentOrderStatus
                        + " to "
                        + order.getStatus()
        );
    }

    private OrderStatus resolveOrderStatus(
            String paymentStatus,
            OrderStatus currentOrderStatus
    ) {

        return switch (paymentStatus) {

            /*
             * Payment successfully approved.
             */
            case "approved" ->
                    OrderStatus.PAID;

            /*
             * The payment is still being processed.
             *
             * Never downgrade an already paid order
             * back to PENDING because of delayed or
             * duplicated webhook notifications.
             */
            case "pending",
                 "in_process",
                 "authorized" -> {

                if (currentOrderStatus
                        == OrderStatus.PAID) {

                    yield OrderStatus.PAID;
                }

                yield OrderStatus.PENDING;
            }

            /*
             * The payment was not completed.
             *
             * Also protect already paid orders from
             * outdated notifications belonging to
             * previous payment attempts.
             */
            case "rejected",
                 "cancelled",
                 "canceled" -> {

                if (currentOrderStatus
                        == OrderStatus.PAID) {

                    yield OrderStatus.PAID;
                }

                yield OrderStatus.PAYMENT_FAILED;
            }

            /*
             * Unknown Mercado Pago status.
             * Preserve the current order state.
             */
            default -> {
                System.out.println(
                        "Unmapped Mercado Pago payment status: "
                                + paymentStatus
                );

                yield currentOrderStatus;
            }
        };
    }

    private String extractPaymentId(
            Map<String, Object> payload
    ) {

        if (payload == null) {
            return null;
        }

        Object dataObject = payload.get("data");

        if (!(dataObject instanceof Map<?, ?> data)) {
            return null;
        }

        Object paymentId = data.get("id");

        return paymentId != null
                ? paymentId.toString()
                : null;
    }

    private boolean isMerchantOrderPayload(
            Map<String, Object> payload
    ) {

        if (payload == null) {
            return false;
        }

        Object payloadTopic = payload.get("topic");

        if (payloadTopic != null
                && "merchant_order".equalsIgnoreCase(
                payloadTopic.toString()
        )) {
            return true;
        }

        Object resource = payload.get("resource");

        return resource != null
                && resource.toString()
                .contains("/merchant_orders/");
    }

    private String resolveMerchantOrderId(
            String queryId,
            Map<String, Object> payload
    ) {

        if (queryId != null && !queryId.isBlank()) {
            return queryId;
        }

        if (payload == null) {
            return null;
        }

        Object resourceObject = payload.get("resource");

        if (resourceObject == null) {
            return null;
        }

        String resource = resourceObject.toString();

        int lastSlash = resource.lastIndexOf('/');

        if (lastSlash < 0
                || lastSlash == resource.length() - 1) {
            return null;
        }

        return resource.substring(lastSlash + 1);
    }
}