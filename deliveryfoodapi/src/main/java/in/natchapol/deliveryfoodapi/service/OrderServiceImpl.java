package in.natchapol.deliveryfoodapi.service;


import com.stripe.exception.StripeException;

import in.natchapol.deliveryfoodapi.entity.OrderEntity;

import in.natchapol.deliveryfoodapi.io.OrderRequest;
import in.natchapol.deliveryfoodapi.io.OrderResponse;

import in.natchapol.deliveryfoodapi.repository.OrderRepository;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserService userService;
    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrderWithPayment(OrderRequest request){

       OrderEntity newOrder = convertToEntity(request);
       newOrder = orderRepository.save(newOrder);

       //StripePayment แปะไว้ก่อน


        String logginUserId = userService.findUserId();
        newOrder.setUserId(logginUserId);
        newOrder = orderRepository.save(newOrder);

        return convertToResponse(newOrder);


    }

    @Override
    public List<OrderResponse> getUserOrders() {
        String logginUserId = userService.findUserId();
         List<OrderEntity>list = orderRepository.findByUserId(logginUserId);
         return list.stream().map(entity->convertToResponse(entity)).collect(Collectors.toList());

    }

    @Override
    public void removeOrder(String orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<OrderResponse> getAllUserOrders() {
        List<OrderEntity>list = orderRepository.findAll();
        return list.stream().map(entity ->convertToResponse(entity)).collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
       OrderEntity entity = orderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("Order not found"));
       entity.setOrderStatus(status);
       orderRepository.save(entity);
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




