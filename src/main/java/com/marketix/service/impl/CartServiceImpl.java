package com.marketix.service.impl;

import com.marketix.dao.CartItemRepository;
import com.marketix.dao.CartRepository;
import com.marketix.entity.Cart;
import com.marketix.entity.CartItem;
import com.marketix.entity.Product;
import com.marketix.entity.User;
import com.marketix.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    @Autowired
    public CartServiceImpl(CartItemRepository cartItemRepository, CartRepository cartRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
    }

    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        user.setShoppingCart(cart);
        return cartRepository.save(cart);
    }

    @Override
    public Cart addItemToCart(Product product, int quantity, User user) {
        Cart cart = user.getShoppingCart();

        if (cart == null) {
            cart = createCart(user);
        }

        Set<CartItem> cartItems = cart.getCartItems();
        CartItem cartItem = findCartItem(cartItems, product.getId());
        if (cartItems == null) {
            cartItems = new HashSet<>();
            if (cartItem == null) {
                cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setTotalPrice(quantity * product.getPrice());
                cartItem.setQuantity(quantity);
                cartItem.setCart(cart);
                cartItems.add(cartItem);
                cartItemRepository.save(cartItem);
            }
        } else {
            if (cartItem == null) {
                cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setTotalPrice(quantity * product.getPrice());
                cartItem.setQuantity(quantity);
                cartItem.setCart(cart);
                cartItems.add(cartItem);
                cartItemRepository.save(cartItem);
            } else {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItem.setTotalPrice(cartItem.getTotalPrice() + (quantity * product.getPrice()));
                cartItemRepository.save(cartItem);
            }
        }
        cart.setCartItems(cartItems);

        int totalItems = totalItems(cart.getCartItems());
        double totalPrice = totalPrice(cart.getCartItems());

        cart.setTotalPrices(totalPrice);
        cart.setTotalItems(totalItems);
        cart.setUser(user);
        System.out.println("SAVED ITEM INTO CART!");

        return cartRepository.save(cart);
    }

    @Override
    public Cart updateItemInCart(Product product, int quantity, User user) {
        Cart cart = user.getShoppingCart();

        Set<CartItem> cartItems = cart.getCartItems();

        CartItem item = findCartItem(cartItems, product.getId());

        item.setQuantity(quantity);
        item.setTotalPrice(quantity * product.getPrice());

        cartItemRepository.save(item);

        int totalItems = totalItems(cartItems);
        double totalPrice = totalPrice(cartItems);

        cart.setTotalItems(totalItems);
        cart.setTotalPrices(totalPrice);

        return cartRepository.save(cart);
    }

    @Override
    public Cart deleteItemFromCart(Product product, User user) {
        Cart cart = user.getShoppingCart();

        Set<CartItem> cartItems = cart.getCartItems();

        CartItem item = findCartItem(cartItems, product.getId());
        cartItems.remove(item);

        cartItemRepository.delete(item);

        double totalPrice = totalPrice(cartItems);
        int totalItems = totalItems(cartItems);

        cart.setCartItems(cartItems);
        cart.setTotalItems(totalItems);
        cart.setTotalPrices(totalPrice);

        return cartRepository.save(cart);
    }

    @Override
    public Cart clearCart(User user) {
        Cart cart = user.getShoppingCart();
        Set<CartItem> cartItems = cart.getCartItems();
        System.out.println("CLEARING CART");

        cartItemRepository.deleteAll(cartItems);

        cartItems = new HashSet<>();
        double totalPrice = totalPrice(cartItems);
        int totalItems = totalItems(cartItems);

        cart.setCartItems(cartItems);
        cart.setTotalItems(totalItems);
        cart.setTotalPrices(totalPrice);

        return cartRepository.save(cart);
    }

    private CartItem findCartItem(Set<CartItem> cartItems, Long productId) {
        if (cartItems == null) {
            return null;
        }
        CartItem cartItem = null;

        for (CartItem item : cartItems) {
            if (Objects.equals(item.getProduct().getId(), productId)) {
                cartItem = item;
            }
        }
        return cartItem;
    }

    private int totalItems(Set<CartItem> cartItems) {
        int totalItems = 0;
        for (var item : cartItems) {
            totalItems += item.getQuantity();
        }
        return totalItems;
    }

    private double totalPrice(Set<CartItem> cartItems) {
        double totalPrice = 0.0;

        for (CartItem item : cartItems) {
            totalPrice += item.getTotalPrice();
        }

        return totalPrice;
    }

}
