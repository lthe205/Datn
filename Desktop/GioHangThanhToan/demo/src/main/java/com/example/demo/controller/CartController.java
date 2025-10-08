package com.example.demo.controller;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.User;
import com.example.demo.service.CartService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    
    private final CartService cartService;
    private final UserService userService;
    
    /**
     * Lấy giỏ hàng của người dùng
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            List<CartItem> cartItems = cartService.getCartItems(user.getId());
            BigDecimal total = cartService.getCartTotal(user.getId());
            Integer itemCount = cartService.getCartItemCount(user.getId());

            // Build lightweight DTOs to avoid serializing JPA proxies
            List<Map<String, Object>> items = cartItems.stream().map(ci -> {
                Map<String, Object> product = new HashMap<>();
                product.put("id", ci.getSanPham().getId());
                product.put("ten", ci.getSanPham().getTen());
                product.put("anhChinh", ci.getSanPham().getAnhChinh());
                product.put("gia", ci.getSanPham().getGia());

                Map<String, Object> item = new HashMap<>();
                item.put("id", ci.getId());
                item.put("soLuong", ci.getSoLuong());
                item.put("gia", ci.getGia());
                item.put("kichCo", ci.getKichCo());
                item.put("mauSac", ci.getMauSac());
                item.put("sanPham", product);
                return item;
            }).toList();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("cartItems", items);
            response.put("total", total);
            response.put("itemCount", itemCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting cart", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String color,
            Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            CartItem ci = cartService.addToCart(user.getId(), productId, quantity, size, color);

            Map<String, Object> product = new HashMap<>();
            product.put("id", ci.getSanPham().getId());
            product.put("ten", ci.getSanPham().getTen());
            product.put("anhChinh", ci.getSanPham().getAnhChinh());
            product.put("gia", ci.getSanPham().getGia());

            Map<String, Object> item = new HashMap<>();
            item.put("id", ci.getId());
            item.put("soLuong", ci.getSoLuong());
            item.put("gia", ci.getGia());
            item.put("kichCo", ci.getKichCo());
            item.put("mauSac", ci.getMauSac());
            item.put("sanPham", product);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã thêm sản phẩm vào giỏ hàng");
            response.put("cartItem", item);
            response.put("total", cartService.getCartTotal(user.getId()));
            response.put("itemCount", cartService.getCartItemCount(user.getId()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error adding to cart", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     */
    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<Map<String, Object>> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity,
            Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            CartItem ci = cartService.updateCartItemQuantity(cartItemId, quantity);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã cập nhật số lượng sản phẩm");
            if (ci != null) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", ci.getSanPham().getId());
                product.put("ten", ci.getSanPham().getTen());
                product.put("anhChinh", ci.getSanPham().getAnhChinh());
                product.put("gia", ci.getSanPham().getGia());

                Map<String, Object> item = new HashMap<>();
                item.put("id", ci.getId());
                item.put("soLuong", ci.getSoLuong());
                item.put("gia", ci.getGia());
                item.put("kichCo", ci.getKichCo());
                item.put("mauSac", ci.getMauSac());
                item.put("sanPham", product);
                response.put("cartItem", item);
            }
            response.put("total", cartService.getCartTotal(user.getId()));
            response.put("itemCount", cartService.getCartItemCount(user.getId()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating cart item", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<Map<String, Object>> removeFromCart(
            @PathVariable Long cartItemId,
            Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            cartService.removeFromCart(cartItemId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã xóa sản phẩm khỏi giỏ hàng");
            response.put("total", cartService.getCartTotal(user.getId()));
            response.put("itemCount", cartService.getCartItemCount(user.getId()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error removing from cart", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Xóa toàn bộ giỏ hàng
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearCart(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            cartService.clearCart(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã xóa toàn bộ giỏ hàng");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error clearing cart", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Lấy số lượng sản phẩm trong giỏ hàng
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCartItemCount(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            Integer itemCount = cartService.getCartItemCount(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("itemCount", itemCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting cart count", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Lấy tổng tiền giỏ hàng
     */
    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> getCartTotal(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            BigDecimal total = cartService.getCartTotal(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", total);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting cart total", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
