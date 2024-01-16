package com.marketix.web;

import com.marketix.entity.Cart;
import com.marketix.entity.Product;
import com.marketix.entity.User;
import com.marketix.service.CartService;
import com.marketix.service.ProductService;
import com.marketix.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/marketix/cart")
public class CartController {
    private final UserService userService;
    private final CartService cartService;
    private final ProductService productService;

    @Autowired
    public CartController(UserService userService, CartService cartService, ProductService productService) {
        this.userService = userService;
        this.cartService = cartService;
        this.productService = productService;
    }

    @GetMapping
    public String getCart(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            redirectAttributes.addAttribute("redirectUrl", "/marketix/cart");
            return "redirect:/marketix/auth/login";
        }
        User user = (User) session.getAttribute(("user"));
        Cart cart = user.getShoppingCart();

        if (cart == null) {
            cart = cartService.createCart(user);
//            model.addAttribute("clear", "T");
//            return "cart";
        }

        session.setAttribute("cart", cart);
        session.setAttribute("cartItems", cart.getCartItems());
        model.addAttribute("totalItems", cart.getTotalItems());
        model.addAttribute("totalPrices", cart.getTotalPrices());
        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cart.getCartItems());
        return "cart";
    }

    @GetMapping("/add-to-cart")
    public String addToCart(@RequestParam("id") Long productId,
                            @RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity,
                            HttpSession session, HttpServletRequest request) {
        if (session.getAttribute("user") == null) {
            return "redirect:/marketix/auth/login";
        }
        Product product = productService.getProductById(productId);
        User user = (User) session.getAttribute(("user"));

        Cart cart = cartService.addItemToCart(product, quantity, user);
        return "redirect:/marketix/products/" + productId;
    }

    @GetMapping("/update-cart")
    public String updateCart(@RequestParam("id") Long productId,
                             @RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity,
                             HttpSession session, HttpServletRequest request, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/marketix/auth/login";
        }

        Product product = productService.getProductById(productId);
        User user = (User) session.getAttribute(("user"));
        Cart cart = cartService.updateItemInCart(product, quantity, user);
        model.addAttribute("cart", cart);
        return "redirect:/marketix/cart";
    }

    @GetMapping("/delete-item/{id}")
    public String deleteItemFromCart(@PathVariable(name = "id") Long productId,
                                     Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/marketix/auth/login";
        }

        Product product = productService.getProductById(productId);
        User user = (User) session.getAttribute(("user"));
        Cart cart = cartService.deleteItemFromCart(product, user);
        return "redirect:/marketix/cart";
    }

    @GetMapping("/clear")
    public String emptyCart(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/marketix/auth/login";
        }
        User user = (User) session.getAttribute(("user"));
        Cart cart = cartService.clearCart(user);
        return "redirect:/marketix/products";
    }
}
