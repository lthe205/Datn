package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.service.EmailService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    private final EmailService emailService;
    private final UserService userService;
    
    /**
     * Tạo đơn hàng từ giỏ hàng
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestParam(required = false) Long addressId,
            @RequestParam(defaultValue = "COD") String paymentMethod,
            @RequestParam(required = false) String notes,
            Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            Order order = orderService.createOrderFromCart(user.getId(), addressId, paymentMethod, notes);
            
            // Chỉ gửi email xác nhận cho thanh toán COD (tiền mặt)
            // Với VNPay, email sẽ được gửi sau khi thanh toán thành công
            if ("COD".equals(paymentMethod)) {
                emailService.sendOrderConfirmationEmail(order);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đơn hàng đã được tạo thành công");
            response.put("order", order);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating order", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách đơn hàng của người dùng
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserOrders(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            List<Order> orders = orderService.getUserOrders(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orders);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting user orders", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Lấy chi tiết đơn hàng
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderDetails(
            @PathVariable Long orderId,
            Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            Optional<Order> orderOpt = orderService.getOrderById(orderId);
            
            if (orderOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Order order = orderOpt.get();
            // Kiểm tra quyền truy cập
            if (!order.getNguoiDung().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "Không có quyền truy cập"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("order", order);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting order details", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Lấy đơn hàng theo mã đơn hàng
     */
    @GetMapping("/code/{orderCode}")
    public ResponseEntity<Map<String, Object>> getOrderByCode(
            @PathVariable String orderCode,
            Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            Optional<Order> orderOpt = orderService.getOrderByCode(orderCode);
            
            if (orderOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Order order = orderOpt.get();
            // Kiểm tra quyền truy cập
            if (!order.getNguoiDung().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "Không có quyền truy cập"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("order", order);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting order by code", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Hủy đơn hàng
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason,
            Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            Optional<Order> orderOpt = orderService.getOrderById(orderId);
            
            if (orderOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Order order = orderOpt.get();
            // Kiểm tra quyền truy cập
            if (!order.getNguoiDung().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "Không có quyền truy cập"));
            }
            
            Order cancelledOrder = orderService.cancelOrder(orderId, reason);
            
            // Gửi email hủy đơn hàng
            emailService.sendOrderCancellationEmail(cancelledOrder, reason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đơn hàng đã được hủy thành công");
            response.put("order", cancelledOrder);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error cancelling order", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Lấy thống kê đơn hàng của người dùng
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getOrderStats(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            OrderService.OrderStats stats = orderService.getOrderStats(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting order stats", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Lấy đơn hàng theo trạng thái
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getOrdersByStatus(
            @PathVariable String status,
            Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            List<Order> orders = orderService.getUserOrders(user.getId())
                    .stream()
                    .filter(order -> status.equals(order.getTrangThai()))
                    .toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orders);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting orders by status", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
