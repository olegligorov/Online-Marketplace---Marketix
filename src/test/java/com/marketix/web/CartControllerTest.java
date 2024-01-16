package com.marketix.web;

import com.marketix.entity.Cart;
import com.marketix.entity.Product;
import com.marketix.entity.User;
import com.marketix.service.CartService;
import com.marketix.service.ProductService;
import com.marketix.service.UserService;
import com.marketix.util.Gender;
import com.marketix.util.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.HashSet;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CartControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartController cartController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @Test
    void testGetCart() throws Exception {
        User mockUser = new User("testuser", "testuser@gmail.com", "password", Gender.MALE, Role.ADMIN, "image", "test user");
        when(userService.getUserByEmail(anyString())).thenReturn(mockUser);
        Cart mockCart = new Cart(1L, 0, 0, mockUser, new HashSet<>());
        when(cartService.createCart(mockUser)).thenReturn(mockCart);

        mockMvc.perform(get("/marketix/cart/").sessionAttr("user", mockUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cart"))
                .andExpect(view().name("cart"));
    }

    @Test
    void testAddToCart() throws Exception {
        User mockUser = new User("testuser", "testuser@gmail.com", "password", Gender.MALE, Role.ADMIN, "image", "test user");
        Product mockProduct = new Product("product1", "testing product", "testuser", 1200, 4);
        when(productService.getProductById(anyLong())).thenReturn(mockProduct);
        when(userService.getUserByEmail(anyString())).thenReturn(mockUser);

        mockMvc.perform(get("/marketix/cart/add-to-cart")
                        .param("id", "1")
                        .param("quantity", "2")
                        .sessionAttr("user", mockUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/marketix/products/1"));

        verify(cartService, times(1)).addItemToCart(mockProduct, 2, mockUser);
        verifyNoMoreInteractions(cartService);
    }

    @Test
    void testUpdateCart() throws Exception {
        User mockUser = new User("testuser", "testuser@gmail.com", "password", Gender.MALE, Role.ADMIN, "image", "test user");
        Product mockProduct = new Product("product1", "testing product", "testuser", 1200, 4);

        when(productService.getProductById(anyLong())).thenReturn(mockProduct);
        when(userService.getUserByEmail(anyString())).thenReturn(mockUser);

        mockMvc.perform(get("/marketix/cart/update-cart")
                        .param("id", "1")
                        .param("quantity", "3")
                        .sessionAttr("user", mockUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/marketix/cart"));

        verify(cartService, times(1)).updateItemInCart(mockProduct, 3, mockUser);
        verifyNoMoreInteractions(cartService);
    }

    @Test
    void testDeleteItemFromCart() throws Exception {
        User mockUser = new User("testuser", "testuser@gmail.com", "password", Gender.MALE, Role.ADMIN, "image", "test user");
        Product mockProduct = new Product("product1", "testing product", "testuser", 1200, 4);

        when(productService.getProductById(anyLong())).thenReturn(mockProduct);
        when(userService.getUserByEmail(anyString())).thenReturn(mockUser);

        mockMvc.perform(get("/marketix/cart/delete-item/1")
                        .sessionAttr("user", mockUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/marketix/cart"));

        verify(cartService, times(1)).deleteItemFromCart(mockProduct, mockUser);
        verifyNoMoreInteractions(cartService);
    }

    @Test
    void testEmptyCart() throws Exception {
        User mockUser = new User("testuser", "testuser@gmail.com", "password", Gender.MALE, Role.ADMIN, "image", "test user");

        when(userService.getUserByEmail(anyString())).thenReturn(mockUser);

        mockMvc.perform(get("/marketix/cart/clear")
                        .sessionAttr("user", mockUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/marketix/products"));

        verify(cartService, times(1)).clearCart(mockUser);
        verifyNoMoreInteractions(cartService);
    }

    // Add more tests as needed for other methods in the controller...

}
