package in.natchapol.deliveryfoodapi.service;

import in.natchapol.deliveryfoodapi.io.CartRequest;
import in.natchapol.deliveryfoodapi.io.CartResponse;

public interface CartService {
    CartResponse addToCart(CartRequest request);
    CartResponse getCart();
    void clearCart();
    CartResponse removeFromCart(CartRequest cartRequest);
    void removeFoodIdFromCart(CartRequest foodId);
}
