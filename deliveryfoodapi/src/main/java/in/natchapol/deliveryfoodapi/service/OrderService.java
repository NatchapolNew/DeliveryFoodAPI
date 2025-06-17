package in.natchapol.deliveryfoodapi.service;

import com.stripe.net.StripeResponse;
import in.natchapol.deliveryfoodapi.io.OrderRequest;
import in.natchapol.deliveryfoodapi.io.OrderResponse;

public interface OrderService {
    OrderResponse createOrderWithPayment(OrderRequest request);
}
