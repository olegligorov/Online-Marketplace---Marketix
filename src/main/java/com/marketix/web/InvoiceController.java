package com.marketix.web;

import com.marketix.entity.Cart;
import com.marketix.entity.Invoice;
import com.marketix.entity.InvoiceCartItem;
import com.marketix.entity.User;
import com.marketix.service.CartService;
import com.marketix.service.InvoiceService;
import com.marketix.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/marketix")
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final CartService cartService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService, CartService cartService) {
        this.invoiceService = invoiceService;
        this.cartService = cartService;
    }

    @GetMapping("/invoices")
    public String getAllUserInvoices(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            redirectAttributes.addAttribute("redirectUrl", "/marketix/invoices");
            return "redirect:/marketix/auth/login";
        }
        User user = (User) session.getAttribute(("user"));
        var invoices = user.getInvoices();
        if (invoices == null) {
            model.addAttribute("check", "Your invoices are empty");
        }
        model.addAttribute("invoices", invoices);
        return "invoices";
    }

    @GetMapping("/invoices/all")
    public String getAllInvoices(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/marketix/auth/login";
        }
        User user = (User) session.getAttribute(("user"));
        if (user.getRole() != Role.ADMIN) {
            return "redirect:/marketix/products";
        }
        model.addAttribute("invoices", invoiceService.getAllInvoices());
        return "all-invoices";
    }


    @GetMapping("/check-out")
    public String getCashoutForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            redirectAttributes.addAttribute("redirectUrl", "/marketix/products");
            return "redirect:/marketix/auth/login";
        }

        if (!model.containsAttribute("invoice")) {
            model.addAttribute("invoice", new Invoice());
        }

        var cart = (Cart) session.getAttribute(("cart"));
        model.addAttribute("totalPrices", cart.getTotalPrices());
        model.addAttribute("path", "check-out");
        model.addAttribute("title", "Cash out");
        return "check-out";
    }

    @PostMapping("/check-out")
    public String createNewInvoice(@Valid @ModelAttribute("invoice") Invoice invoice, BindingResult binding,
                                   RedirectAttributes redirectAttributes, HttpServletRequest request,
                                   HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/marketix/auth/login";
        }

        if (binding.hasErrors()) {
            model.addAttribute("fileError", null);
            return "check-out";
        }

        var user = (User) session.getAttribute(("user"));
        var cart = (Cart) session.getAttribute(("cart"));
        invoice.setUser(user);

        Set<InvoiceCartItem> invoiceCartItems = new HashSet<>();

        for (var cartItem : cart.getCartItems()) {
            var invoiceCartItem = new InvoiceCartItem();
            invoiceCartItem.setQuantity(cartItem.getQuantity());
            invoiceCartItem.setTotalPrice(cartItem.getTotalPrice());
            invoiceCartItem.setProductId(cartItem.getProduct().getId());
            invoiceCartItem.setProductDescription(cartItem.getProduct().getDescription());
            invoiceCartItem.setProductName(cartItem.getProduct().getName());
            invoiceCartItems.add(invoiceCartItem);
        }

        invoice.setCartItems(invoiceCartItems);
        invoice.setTotalPrice(cart.getTotalPrices());
        invoiceService.add(invoice);
        cartService.clearCart(user);

        return "redirect:/marketix/invoices/" + invoice.getId();
    }

    @GetMapping("/invoices/{invoiceId:\\d+}")
    public String getInvoice(@PathVariable("invoiceId") Long invoiceId, HttpSession session, Model model, HttpSession httpSession) {
        if (session.getAttribute("user") == null) {
            return "redirect:/marketix/auth/login";
        }

        User user = (User) session.getAttribute(("user"));
        var invoice = invoiceService.getInvoiceById(invoiceId);

        if (user.getRole() != Role.ADMIN && invoice.getUser() != user) {
            return "redirect:/marketix/products";
        }

        model.addAttribute("invoice", invoice);
        return "invoice";
    }

    @GetMapping("/invoices/delete/{invoiceId:\\d+}")
    public String deleteInvoice(@PathVariable("invoiceId") Long invoiceId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            return "redirect:/marketix/auth/login";
        }

        User user = (User) session.getAttribute(("user"));
    //only admins can delete invoices
        if (user.getRole() != Role.ADMIN) {
            return "redirect:/marketix/products";
        }

        invoiceService.deleteById(invoiceId);
        return "redirect:/marketix/invoices/all";
    }
}
