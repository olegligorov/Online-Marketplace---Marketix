package com.marketix.service;

import com.marketix.entity.Cart;
import com.marketix.entity.Product;
import com.marketix.entity.User;

public interface CartService {

    Cart createCart(User user);
    Cart addItemToCart(Product product, int quantity, User user);

    Cart updateItemInCart(Product product, int quantity, User user);

    Cart deleteItemFromCart(Product product, User user);

    Cart clearCart(User user);
}
