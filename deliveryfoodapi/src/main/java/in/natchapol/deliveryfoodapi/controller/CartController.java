package in.natchapol.deliveryfoodapi.controller;

import in.natchapol.deliveryfoodapi.io.CartRequest;
import in.natchapol.deliveryfoodapi.io.CartResponse;
import in.natchapol.deliveryfoodapi.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public CartResponse addToCart(@RequestBody CartRequest request) {
        String foodId = request.getFoodId();
        if (foodId == null || foodId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ไม่พบรหัสสินค้า");
        }
        return cartService.addToCart(request);

    }

    @GetMapping
    public CartResponse getCart() {
        return cartService.getCart();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart() {
        cartService.clearCart();
    }

    @PostMapping("/remove")
    public CartResponse removeFromCart(@RequestBody CartRequest request) {
        String foodId = request.getFoodId();
        if (foodId == null || foodId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ไม่พบรหัสสินค้า");
        }
        return cartService.removeFromCart(request);

    }

    //ยังไม่ได้เพิ่มส่วนclient
    @PostMapping("/removefoodid")
    public void RemoveFoodIdFromCart(@RequestBody CartRequest foodId){
        cartService.removeFoodIdFromCart(foodId);
    }

}
