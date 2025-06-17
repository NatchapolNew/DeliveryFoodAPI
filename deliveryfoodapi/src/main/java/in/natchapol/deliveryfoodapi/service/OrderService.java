package in.natchapol.deliveryfoodapi.service;

import com.stripe.net.StripeResponse;
import in.natchapol.deliveryfoodapi.io.OrderRequest;
import in.natchapol.deliveryfoodapi.io.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrderWithPayment(OrderRequest request);
    List<OrderResponse> getUserOrders();
    void removeOrder(String orderId);
    List<OrderResponse> getAllUserOrders();
    void updateOrderStatus(String orderId,String status);
}
