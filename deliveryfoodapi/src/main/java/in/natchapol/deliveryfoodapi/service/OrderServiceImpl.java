package in.natchapol.deliveryfoodapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;
import com.stripe.Stripe;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;


import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import in.natchapol.deliveryfoodapi.config.StripeConfig;
import in.natchapol.deliveryfoodapi.entity.OrderEntity;

import in.natchapol.deliveryfoodapi.io.OrderItem;
import in.natchapol.deliveryfoodapi.io.OrderRequest;
import in.natchapol.deliveryfoodapi.io.OrderResponse;

import in.natchapol.deliveryfoodapi.io.StripeResponse;
import in.natchapol.deliveryfoodapi.repository.OrderRepository;
import lombok.AllArgsConstructor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;



import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;



@Slf4j
@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired
    private final UserService userService;
    @Autowired
    private final OrderRepository orderRepository;
    @Autowired
    private StripeConfig stripeKey;


    @Override
    public StripeResponse createOrderWithPayment(OrderRequest request) {

//        OrderEntity newOrder = convertToEntity(request);
        String logginUserId = userService.findUserId();
        String orderItems;
        try {

            orderItems = new ObjectMapper().writeValueAsString(request.getOrderedItem());
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Failed to json");
        }


        //stripe payment
        Stripe.apiKey = stripeKey.getStripeKey();

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        for (OrderItem item : request.getOrderedItem()) {
            SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(item.getName())
                    .build();

            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("thb")
                    .setUnitAmount((long) item.getPrice() * 100)
                    .setProductData(productData)
                    .build();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity((long) item.getQuantity())
                    .setPriceData(priceData).build();

            lineItems.add(lineItem);
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success")
                .setCancelUrl("http://localhost:8080/cancel")
                .addAllLineItem(lineItems)
                .putMetadata("userId", logginUserId)
                .putMetadata("userAddress", request.getUserAddress())
                .putMetadata("amount", String.valueOf(request.getAmount()))
                .putMetadata("email", request.getEmail())
                .putMetadata("phoneNumber", request.getPhoneNumber())
                .putMetadata("orderStatus", request.getOrderStatus())
                .putMetadata("orderedItem", orderItems)
                .build();


        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException ex) {
            throw new RuntimeException("Stripe session creation failed: " + ex.getMessage());
        }

        StripeResponse stripeResponse = new StripeResponse();

        stripeResponse.setStatus(session.getStatus());
        stripeResponse.setSessionUrl(session.getUrl());
        stripeResponse.setSessionId(session.getId());
        stripeResponse.setMessage("Payment session created");


        return stripeResponse;


    }

    @Override
    public List<OrderResponse> getUserOrders() {
        String logginUserId = userService.findUserId();
        List<OrderEntity> list = orderRepository.findByUserId(logginUserId);
        return list.stream().map(entity -> convertToResponse(entity)).collect(Collectors.toList());

    }

    @Override
    public void removeOrder(String orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<OrderResponse> getAllUserOrders() {
        List<OrderEntity> list = orderRepository.findAll();
        return list.stream().map(entity -> convertToResponse(entity)).collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        OrderEntity entity = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        entity.setOrderStatus(status);
        orderRepository.save(entity);
    }

    @Override
    public String stripeCheckOut(String payload, String sigHeader) {
        String endpointSecret = stripeKey.getEndpointSecret();
        Event event = null;
        try {
            event = ApiResource.GSON.fromJson(payload,Event.class);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e);
        }

        try {
         event = Webhook.constructEvent(payload,sigHeader,endpointSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }

        JsonNode root = null;
       ObjectMapper objectMapper = new ObjectMapper();
        try {
            root = objectMapper.readTree(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if("checkout.session.completed".equals(event.getType())){
            String sessionId = root.path("data").path("object").path("id").asText();
            Session session = null;
            try {
                session = Session.retrieve(sessionId);
            } catch (StripeException e) {
                throw new RuntimeException(e);
            }
            log.info(session.getMetadata().get("userId"));

            Map<String,String> metadata = session.getMetadata();

            String userId = metadata.get("userId");
            String userAddress = metadata.get("userAddress");
            String amount = metadata.get("amount");
            String email = metadata.get("email");
            String phoneNumber = metadata.get("phoneNumber");
            String orderStatus = metadata.get("orderStatus");
            String orderedItemJson = metadata.get("orderedItem");

            List<OrderItem> orderedItem = null;

            try {
                orderedItem = objectMapper.readValue(orderedItemJson, new TypeReference<List<OrderItem>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            OrderEntity order = new OrderEntity();

            order.setUserId(userId);
            order.setUserAddress(userAddress);
            order.setAmount(Double.parseDouble(amount));
            order.setEmail(email);
            order.setPhoneNumber(phoneNumber);
            order.setOrderStatus(orderStatus);
            order.setOrderedItems(orderedItem);

            orderRepository.save(order);


        }

        return null;
    }


    private OrderResponse convertToResponse(OrderEntity newOrder) {
        return OrderResponse.builder()
                .id(newOrder.getId())
                .amount(newOrder.getAmount())
                .userId(newOrder.getUserId())
                .userAddress(newOrder.getUserAddress())
                .phoneNumber(newOrder.getPhoneNumber())
                .email(newOrder.getEmail())
                .orderStatus(newOrder.getOrderStatus())
                .orderItems(newOrder.getOrderedItems())
                .sessionUrl(newOrder.getSessionUrl())
                .sessionId(newOrder.getSessionId())
                .stripeStatus(newOrder.getStripeStatus())
                .message(newOrder.getMessage())
                .build();
    }


    private OrderEntity convertToEntity(OrderRequest request) {
        return OrderEntity.builder()
                .userAddress(request.getUserAddress())
                .amount(request.getAmount())
                .orderedItems(request.getOrderedItem())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .orderStatus(request.getOrderStatus())
                .build();
    }
}




