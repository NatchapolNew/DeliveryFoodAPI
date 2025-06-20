package in.natchapol.deliveryfoodapi.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import in.natchapol.deliveryfoodapi.config.AWSConfig;
import in.natchapol.deliveryfoodapi.config.StripeConfig;
import in.natchapol.deliveryfoodapi.entity.OrderEntity;

import in.natchapol.deliveryfoodapi.io.OrderItem;
import in.natchapol.deliveryfoodapi.io.OrderRequest;
import in.natchapol.deliveryfoodapi.io.OrderResponse;

import in.natchapol.deliveryfoodapi.io.StripeResponse;
import in.natchapol.deliveryfoodapi.repository.OrderRepository;
import lombok.AllArgsConstructor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public OrderResponse createOrderWithPayment(OrderRequest request) {

        OrderEntity newOrder = convertToEntity(request);
        newOrder = orderRepository.save(newOrder);
        String logginUserId = userService.findUserId();
        newOrder.setUserId(logginUserId);
        newOrder = orderRepository.save(newOrder);

        return convertToResponse(newOrder);


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
    public StripeResponse stripeCheckOut(OrderRequest request) {
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
                .build();


        Session session = null;
            try {
                session =  Session.create(params);
            }catch(StripeException ex){
                throw new RuntimeException("Stripe session creation failed: " + ex.getMessage());

            }
        return StripeResponse.builder()
                .status("SUCCESS")
                .message("Payment session created")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
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
                .status(newOrder.getStripeStatus())
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




