package in.natchapol.deliveryfoodapi.service;


import in.natchapol.deliveryfoodapi.entity.CartEntity;
import in.natchapol.deliveryfoodapi.io.CartRequest;
import in.natchapol.deliveryfoodapi.io.CartResponse;
import in.natchapol.deliveryfoodapi.repository.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {
    @Autowired
    private final CartRepository cartRepository;
    private final UserService userService;

    @Override
    public CartResponse addToCart(CartRequest request) {
        String logginUserId = userService.findUserId();
        Optional<CartEntity> cartOptional = cartRepository.findByUserId(logginUserId);
        CartEntity cart = cartOptional.orElseGet(() -> new CartEntity(logginUserId, new HashMap<>()));
        Map<String, Integer> cartItems = cart.getItems();
        cartItems.put(request.getFoodId(), cartItems.getOrDefault(request.getFoodId(), 0) + 1);
        cart.setItems(cartItems);
        cart = cartRepository.save(cart);
        return covertToResponse(cart);

    }

    @Override
    public CartResponse getCart() {
        String logginUserId = userService.findUserId();
        CartEntity entity = cartRepository.findByUserId(logginUserId)
                .orElse(new CartEntity(null,logginUserId,new HashMap<>()));
        return covertToResponse(entity);

    }

    @Override
    public void clearCart() {
        String logginUserId = userService.findUserId();
        cartRepository.deleteByUserId(logginUserId);
    }

    @Override
    public CartResponse removeFromCart(CartRequest cartRequest) {
        String logginUserId = userService.findUserId();
       CartEntity entity = cartRepository.findByUserId(logginUserId)
                .orElseThrow(()-> new RuntimeException("Cart is not found"));
        Map<String,Integer> cartItems = entity.getItems();
        if(cartItems.containsKey(cartRequest.getFoodId())){
            int currentQty = cartItems.get(cartRequest.getFoodId());
            if (currentQty > 0){
                cartItems.put(cartRequest.getFoodId(),currentQty - 1);
            }else {
                cartItems.remove(cartRequest.getFoodId());
            }
            entity = cartRepository.save(entity);
        }
            return covertToResponse(entity);

    }

    private CartResponse covertToResponse(CartEntity cartEntity) {
        return CartResponse.builder()
                .id(cartEntity.getId())
                .userId(cartEntity.getUserId())
                .items(cartEntity.getItems())
                .build();
    }
}
