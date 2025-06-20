package in.natchapol.deliveryfoodapi.io;

import com.stripe.model.climate.Order;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class OrderResponse {
    private String id;
    private String userId;
    private String userAddress;
    private String phoneNumber;
    private String email;
    private double amount;
    private String orderStatus;
    private List<OrderItem> orderItems;
    private String status;
    private String message;
    private String sessionId;
    private String sessionUrl;

}
