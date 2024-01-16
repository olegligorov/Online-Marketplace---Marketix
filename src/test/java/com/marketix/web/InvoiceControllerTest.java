package com.marketix.web;

import com.marketix.entity.*;
import com.marketix.service.CartService;
import com.marketix.service.InvoiceService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InvoiceControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(invoiceController).build();
    }

    @Test
    void testGetAllUserInvoices() throws Exception {
        User mockUser = new User("testuser", "testuser@gmail.com", "password", Gender.MALE, Role.ADMIN, "image", "test user");
        Set<Invoice> mockInvoices = new HashSet<>();

        Set<InvoiceCartItem> mockCartItems = new HashSet<>();
        mockCartItems.add(new InvoiceCartItem(1, 1, "test product", "test product", 2L));
        mockInvoices.add(new Invoice(1L, mockCartItems, mockUser, "test street", "city", "state", "zipcode", 1500, LocalDateTime.now(), LocalDateTime.now()));
        mockInvoices.add(new Invoice(2L, mockCartItems, mockUser, "test street", "city", "state", "zipcode", 1500, LocalDateTime.now(), LocalDateTime.now()));

        mockUser.setInvoices(mockInvoices);
        when(invoiceService.getAllUserInvoices(mockUser)).thenReturn(mockInvoices);

        mockMvc.perform(get("/marketix/invoices/").sessionAttr("user", mockUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("invoices"))
                .andExpect(view().name("invoices"));
    }

    @Test
    void testGetAllInvoices() throws Exception {
        User mockUser = new User("testuser", "testuser@gmail.com", "password", Gender.MALE, Role.ADMIN, "image", "test user");
        List<Invoice> mockInvoices = new ArrayList<>();

        Set<InvoiceCartItem> mockCartItems = new HashSet<>();
        mockCartItems.add(new InvoiceCartItem(1, 1, "test product", "test product", 2L));
        mockInvoices.add(new Invoice(1L, mockCartItems, mockUser, "test street", "city", "state", "zipcode", 1500, LocalDateTime.now(), LocalDateTime.now()));
        mockInvoices.add(new Invoice(2L, mockCartItems, mockUser, "test street", "city", "state", "zipcode", 1500, LocalDateTime.now(), LocalDateTime.now()));

        when(userService.getUserByEmail(anyString())).thenReturn(mockUser);
        when(invoiceService.getAllInvoices()).thenReturn(mockInvoices);

        mockMvc.perform(get("/marketix/invoices/all/").sessionAttr("user", mockUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("invoices"))
                .andExpect(view().name("all-invoices"));

        verify(invoiceService, times(1)).getAllInvoices();
        verifyNoMoreInteractions(invoiceService);
    }

}