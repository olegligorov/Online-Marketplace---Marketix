package com.marketix.web;

import com.marketix.entity.Product;
import com.marketix.entity.User;
import com.marketix.service.ProductService;
import com.marketix.util.MultiPartFile;
import com.marketix.util.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/marketix/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String getAllProducts(Model products) {
        products.addAttribute("products", productService.getAllProducts());
        return "products";
    }

    @GetMapping("/product-form")
    public String getProductForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            redirectAttributes.addAttribute("redirectUrl", "/marketix/products/product-form");
            return "redirect:/marketix/auth/login";
        }
        if (!model.containsAttribute("product")) {
            model.addAttribute("product", new Product());
        }
        model.addAttribute("path", "product-form");
        model.addAttribute("title", "Add new product");
        return "product-form";
    }

    @GetMapping("/{productId:\\d+}")
    public String getProduct(@PathVariable("productId") Long productId, Model model, HttpSession session) {
        var product = productService.getProductById(productId);
        model.addAttribute("product", product);
        return "product";
    }

    @GetMapping("/edit/{productId:\\d+}")
    public String editProduct(@PathVariable("productId") Long productId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            var redirectStr = String.format("/marketix/products/edit/%d", productId);
            redirectAttributes.addAttribute("redirectUrl", redirectStr);
            return "redirect:/marketix/auth/login";
        }

        var product = productService.getProductById(productId);
        model.addAttribute("product", product);
        return "edit-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Long productId, Model model,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            redirectAttributes.addAttribute("redirectUrl", "marketix/products");
            return "redirect:/marketix/auth/login";
        }

        var seller = (User) session.getAttribute(("user"));
        var product = productService.getProductById(productId);
        //only the seller and the admin can delete the product
        if (product.getSeller().equals(seller.getEmail()) || seller.getRole() == Role.ADMIN) {
            productService.deleteById(productId);
        }

        return "redirect:/marketix/products";
    }

    @PostMapping("/edit/{productId:\\d+}")
    public String editProduct(@Valid @ModelAttribute("product") Product product, BindingResult binding,
                              RedirectAttributes redirectAttributes, HttpServletRequest request,
                              HttpSession session, Model model, @RequestParam("file") MultipartFile file) {

        if (session.getAttribute("user") == null) {
            return "redirect:/marketix/auth/login";
        }

        var seller = (User) session.getAttribute(("user"));
        //only the seller and the admin can delete the product
        if (!product.getSeller().equals(seller.getEmail()) && seller.getRole() != Role.ADMIN) {
            return "redirect:/marketix/auth/login";
        }

        if (binding.hasErrors()) {
            model.addAttribute("fileError", null);
            return "edit-form";
        }
        if (!file.isEmpty() && file.getOriginalFilename().length() > 0) {
            if (Pattern.matches("\\w+\\.(jpeg|jpg|png)", file.getOriginalFilename())) {
                MultiPartFile.handleMultipartFile(file);
                product.setImageUrl("/uploads/" + file.getOriginalFilename());
            } else {
                model.addAttribute("fileError", "Submit picture [.jpeg, .jpg, .png]");
                return "product-form";
            }
        }

        product.setModified(LocalDateTime.now());
        productService.update(product);

        return "redirect:/marketix/products";
    }

    @PostMapping("/product-form")
    public String createNewProduct(@Valid @ModelAttribute("product") Product product, BindingResult binding,
                                   RedirectAttributes redirectAttributes, HttpServletRequest request,
                                   HttpSession session, Model model, @RequestParam("file") MultipartFile file) {
        if (session.getAttribute("user") == null) {
            redirectAttributes.addAttribute("redirectUrl", "marketix/products");
            return "redirect:/marketix/auth/login";
        }

        if (binding.hasErrors()) {
            model.addAttribute("fileError", null);
            return "product-form";
        }
        var seller = (User) session.getAttribute(("user"));
        product.setSeller(seller.getEmail());
        System.out.println("Posting new product");
        if (!file.isEmpty() && file.getOriginalFilename().length() > 0) {
            if (Pattern.matches("\\w+\\.(jpeg|jpg|png)", file.getOriginalFilename())) {
                MultiPartFile.handleMultipartFile(file);
                product.setImageUrl("/uploads/" + file.getOriginalFilename());
            } else {
                model.addAttribute("fileError", "Submit picture [.jpeg, .jpg, .png]");
                return "product-form";
            }
        }
        if (product.getId() == null) {
            productService.add(product);
        } else {
            product.setModified(LocalDateTime.now());
            productService.update(product);
        }
        return "redirect:/marketix/products";
    }
}
