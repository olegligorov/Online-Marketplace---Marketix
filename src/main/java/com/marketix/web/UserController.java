package com.marketix.web;

import com.marketix.entity.User;
import com.marketix.service.AuthService;
import com.marketix.service.UserService;
import com.marketix.util.Gender;
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
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/marketix/auth")
public class UserController {
    public static final String UPLOADS_DIR = "uploads";
    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping
    public String getUsers(Model products, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/marketix/products";
        }

        products.addAttribute("users", userService.getAllUsers());
        return "manage-users";
    }

    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "auth-register";
    }

    @PostMapping("/register")
    public String registerNewUser(@Valid @ModelAttribute("user") User user, Model model,
                                  BindingResult binding, RedirectAttributes redirectAttributes, MultipartFile file) {
        if (binding.hasErrors()) {
            System.out.println("Error registering user: " + binding.getAllErrors());
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:register";
        }
        try {
            if (user.getImageUrl() == null || user.getImageUrl().isBlank()) {
                if (user.getGender() == Gender.MALE) {
                    user.setImageUrl("/uploads/male_icon.png");
                } else {
                    user.setImageUrl("/uploads/female_icon.png");
                }
            }
            user.setRole(Role.USER);

            if (file != null && !file.isEmpty() && file.getOriginalFilename().length() > 0) {
                if (Pattern.matches("\\w+\\.(jpeg|jpg|png)", file.getOriginalFilename())) {
                    MultiPartFile.handleMultipartFile(file);
                    user.setImageUrl("/uploads/" + file.getOriginalFilename());
                } else {
                    model.addAttribute("fileError", "Submit picture [.jpeg, .jpg, .png]");
                    return "user-edit-form";
                }
            }
            authService.register(user);
        } catch (Exception e) {
            System.out.println("Error registering user");
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:register";
        }
        return "redirect:login";
    }

    @GetMapping("/login")
    public String getLoginForm(Model model) {
        if (!model.containsAttribute("email")) {
            model.addAttribute("email", "");
        }
        if (!model.containsAttribute("password")) {
            model.addAttribute("password", "");
        }
        return "auth-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        @ModelAttribute("redirectUrl") String redirectUrl,
                        RedirectAttributes redirectAttributes,
                        HttpSession session) {
        User loggedUser = authService.login(email, password);
        if (loggedUser == null) {
            String error = "Invalid username or password.";
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("errors", error);
            redirectAttributes.addAttribute("redirectUrl", redirectUrl);
            return "redirect:login";
        }
        session.setAttribute("user", loggedUser);
        if (redirectUrl != null && redirectUrl.length() > 0) {
            return "redirect:" + redirectUrl;
        }
        return "redirect:/marketix/products";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/marketix/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Long userId, Model model,
                             HttpSession session, RedirectAttributes redirectAttributes) {
        //#TODO check whether the user is an admin
        if (session.getAttribute("user") == null) {
            return "redirect:/marketix/products";
        }

        userService.deleteUser(userId);
        return "redirect:/marketix/auth";
    }

    @GetMapping("/edit/{userId:\\d+}")
    public String editUser(@PathVariable("userId") Long userId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            return "redirect:/marketix/products";
        }

        var user = userService.getUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        return "user-edit-form";
    }

    @PostMapping("/edit/{userId:\\d+}")
    public String editUser(@Valid @ModelAttribute("user") User user, BindingResult binding,
                           RedirectAttributes redirectAttributes, HttpServletRequest request,
                           HttpSession session, Model model, MultipartFile file) {
        if (binding.hasErrors()) {
            model.addAttribute("fileError", null);
            return "user-edit-form";
        }

        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        if (!file.isEmpty() && file.getOriginalFilename().length() > 0) {
            if (Pattern.matches("\\w+\\.(jpeg|jpg|png)", file.getOriginalFilename())) {
                MultiPartFile.handleMultipartFile(file);
                user.setImageUrl("/uploads/" + file.getOriginalFilename());
            } else {
                model.addAttribute("fileError", "Submit picture [.jpeg, .jpg, .png]");
                return "user-edit-form";
            }
        }

        user.setModified(LocalDateTime.now());
        userService.updateUser(user);
        if (user.getRole() == Role.ADMIN) {
            return "redirect:/marketix/auth";

        }
        return "redirect:/marketix/products";
    }
}
