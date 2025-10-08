package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.CartService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebController {
    
    private final UserService userService;
    private final CartService cartService;
    
    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        if (authentication != null) {
            User user = getCurrentUser(authentication);
            model.addAttribute("user", user);
            model.addAttribute("cartItemCount", cartService.getCartItemCount(user.getId()));
        }
        return "index";
    }
    
    @GetMapping("/products")
    public String products(Model model, Authentication authentication) {
        if (authentication != null) {
            User user = getCurrentUser(authentication);
            model.addAttribute("user", user);
            model.addAttribute("cartItemCount", cartService.getCartItemCount(user.getId()));
        }
        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetails(@PathVariable Long id, Model model, Authentication authentication) {
        if (authentication != null) {
            User user = getCurrentUser(authentication);
            model.addAttribute("user", user);
            model.addAttribute("cartItemCount", cartService.getCartItemCount(user.getId()));
        }
        model.addAttribute("productId", id);
        return "products"; // reuse products page (or create a dedicated details page later)
    }
    
    @GetMapping("/cart")
    public String cart(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        
        User user = getCurrentUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute("cartItemCount", cartService.getCartItemCount(user.getId()));
        
        return "cart";
    }
    
    @GetMapping("/checkout")
    public String checkout(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        
        User user = getCurrentUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute("cartItemCount", cartService.getCartItemCount(user.getId()));
        
        return "checkout";
    }
    
    @GetMapping("/orders")
    public String orders(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        
        User user = getCurrentUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute("cartItemCount", cartService.getCartItemCount(user.getId()));
        
        return "orders";
    }
    
    @GetMapping("/orders/{orderId}")
    public String orderDetails(@PathVariable Long orderId, Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        
        User user = getCurrentUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute("cartItemCount", cartService.getCartItemCount(user.getId()));
        model.addAttribute("orderId", orderId);
        
        return "order-details";
    }
    
    @GetMapping("/login")
    public String login(Authentication authentication) {
        // Nếu đã đăng nhập thì redirect về trang chủ
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        return "login";
    }
    
    @GetMapping("/register")
    public String register(Authentication authentication) {
        // Nếu đã đăng nhập thì redirect về trang chủ
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String ten,
            @RequestParam String email,
            @RequestParam String soDienThoai,
            @RequestParam(required = false) String ngaySinh,
            @RequestParam(required = false) String gioiTinh,
            @RequestParam String matKhau,
            @RequestParam String confirmMatKhau,
            Model model) {
        
        try {
            // Kiểm tra mật khẩu khớp
            if (!matKhau.equals(confirmMatKhau)) {
                model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
                return "register";
            }
            
            // Kiểm tra email đã tồn tại chưa
            if (userService.findByEmail(email).isPresent()) {
                model.addAttribute("error", "Email này đã được sử dụng!");
                return "register";
            }
            
            // Tạo user mới
            User newUser = new User();
            newUser.setTen(ten);
            newUser.setEmail(email);
            newUser.setSoDienThoai(soDienThoai);
            newUser.setNgaySinh(ngaySinh != null && !ngaySinh.isEmpty() ? 
                java.time.LocalDate.parse(ngaySinh) : null);
            newUser.setGioiTinh(gioiTinh != null ? gioiTinh : "Nam");
            newUser.setMatKhau(matKhau); // UserService sẽ encode password
            newUser.setHoatDong(true); // Active
            newUser.setVaiTro(userService.getDefaultRole()); // Set default role
            
            // Lưu user
            User savedUser = userService.save(newUser);
            
            if (savedUser != null) {
                model.addAttribute("success", "Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.");
                return "login";
            } else {
                model.addAttribute("error", "Có lỗi xảy ra khi đăng ký. Vui lòng thử lại!");
                return "register";
            }
            
        } catch (Exception e) {
            log.error("Error during registration", e);
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "register";
        }
    }
    
    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
