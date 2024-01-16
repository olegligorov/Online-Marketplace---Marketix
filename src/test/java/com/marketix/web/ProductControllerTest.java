package com.marketix.web;

import com.marketix.entity.Product;
import com.marketix.entity.User;
import com.marketix.service.ProductService;
import com.marketix.util.Gender;
import com.marketix.util.Role;
import com.marketix.web.ProductController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void testGetProduct() throws Exception {
        when(productService.getProductById(anyLong())).thenReturn(new Product("product1", "testing product", "testuser", 1200, 4));

        mockMvc.perform(get("/marketix/products/{productId}/", 1L))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("product"))
                .andExpect(view().name("product"));

        verify(productService, times(1)).getProductById(1L);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void testGetAllProducts() throws Exception {
        List<Product> productList = new ArrayList<>();
        productList.add(new Product("product1", "testing product", "testuser", 1200, 4));
        productList.add(new Product("product2", "testing product 2", "testuser", 1000, 5));

        when(productService.getAllProducts()).thenReturn(productList);

        mockMvc.perform(get("/marketix/products/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("products"))
                .andExpect(view().name("products"));

        verify(productService, times(1)).getAllProducts();
        verifyNoMoreInteractions(productService);
    }

    @Test
    void testGetProductForm() throws Exception {
        // Set up a mock session with a user attribute
        User mockUser = new User("testuser", "testuser@gmail.com", "password", Gender.MALE, Role.ADMIN, "image", "test user");
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("user", mockUser);

        mockMvc.perform(get("/marketix/products/product-form/").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("product"))
                .andExpect(view().name("product-form"));
    }

    @Test
    void testEditProductForm() throws Exception {
        User mockUser = new User("testuser", "testuser@gmail.com", "password", Gender.MALE, Role.ADMIN, "image", "test user");
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("user", mockUser);

        when(productService.getProductById(anyLong())).thenReturn(new Product("product1", "testing product", "testuser", 1200, 4));

        mockMvc.perform(get("/marketix/products/edit/{productId}/", 1L).session(mockSession))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("product"))
                .andExpect(view().name("edit-form"));

        verify(productService, times(1)).getProductById(1L);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void testDeleteProduct() throws Exception {
        User mockUser = new User("testuser", "testuser@gmail.com", "password", Gender.MALE, Role.ADMIN, "image", "test user");

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("user", mockUser);

        when(productService.getProductById(anyLong())).thenReturn(new Product("product1", "testing product", "testuser", 1200, 4));

        mockMvc.perform(get("/marketix/products/delete/{id}/", 1L).session(mockSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/marketix/products"));

        verify(productService, times(1)).getProductById(1L);
        verify(productService, times(1)).deleteById(1L);
        verifyNoMoreInteractions(productService);
    }

}