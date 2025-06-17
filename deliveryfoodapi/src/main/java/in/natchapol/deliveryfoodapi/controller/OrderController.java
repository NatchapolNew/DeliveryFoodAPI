package in.natchapol.deliveryfoodapi.controller;

import in.natchapol.deliveryfoodapi.io.OrderRequest;
import in.natchapol.deliveryfoodapi.io.OrderResponse;
import in.natchapol.deliveryfoodapi.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping("/create")
    public OrderResponse createOrderWithPayment(@RequestBody OrderRequest request){
        OrderResponse res = orderService.createOrderWithPayment(request);
        return res;
    }
}
