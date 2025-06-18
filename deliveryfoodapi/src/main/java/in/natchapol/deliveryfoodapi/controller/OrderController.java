package in.natchapol.deliveryfoodapi.controller;

import in.natchapol.deliveryfoodapi.io.OrderRequest;
import in.natchapol.deliveryfoodapi.io.OrderResponse;
import in.natchapol.deliveryfoodapi.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrderWithPayment(@RequestBody OrderRequest request) {
        OrderResponse res = orderService.createOrderWithPayment(request);
        return res;
    }

    @GetMapping
    public List<OrderResponse> getOrders() {
    return orderService.getUserOrders();
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String orderId){
        orderService.removeOrder(orderId);
    }

    //adminPanel
    @GetMapping("/all")
    public List<OrderResponse>getAllUserOrders(){
        return orderService.getAllUserOrders();
    }

    //adminPane
    @PatchMapping("/status/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOrderStatus(@PathVariable String orderId,@RequestParam String status){
        orderService.updateOrderStatus(orderId,status);

    }
}

