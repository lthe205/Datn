package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final DiaChiRepository diaChiRepository;
    
    /**
     * Tạo đơn hàng từ giỏ hàng
     */
    public Order createOrderFromCart(Long userId, Long addressId, String paymentMethod, String notes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Lấy địa chỉ giao hàng (ưu tiên addressId, fallback địa chỉ mặc định của user)
        Optional<DiaChi> addressOpt = Optional.empty();
        if (addressId != null) {
            addressOpt = diaChiRepository.findById(addressId);
        }
        if (addressOpt.isEmpty()) {
            addressOpt = diaChiRepository.findDefaultByUserId(userId);
        }
        DiaChi address = addressOpt.orElseThrow(() -> new RuntimeException("Address not found"));
        
        // Lấy giỏ hàng
        List<CartItem> cartItems = cartService.getCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        // Tạo đơn hàng
        Order order = new Order();
        order.setMaDonHang(generateOrderCode());
        order.setNguoiDung(user);
        order.setTenNguoiNhan(address.getHoTenNhan());
        order.setSoDienThoaiNhan(address.getSoDienThoai());
        order.setDiaChiGiaoHang(buildFullAddress(address));
        order.setPhuongThucThanhToan(paymentMethod);
        order.setGhiChu(notes);
        order.setTrangThai("CHO_XAC_NHAN");
        
        // Tính tổng tiền
        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        order.setTongTien(totalAmount);
        
        // Tính phí vận chuyển
        BigDecimal shippingFee = calculateShippingFee(totalAmount, address.getTinhThanh());
        order.setPhiVanChuyen(shippingFee);
        order.setTongThanhToan(totalAmount.add(shippingFee));
        
        order = orderRepository.save(order);
        
        // Tạo chi tiết đơn hàng và cập nhật tồn kho
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setDonHang(order);
            orderItem.setSanPham(cartItem.getSanPham());
            orderItem.setSoLuong(cartItem.getSoLuong());
            orderItem.setGia(cartItem.getGia());
            orderItem.setThanhTien(cartItem.getThanhTien());
            orderItem.setKichCo(cartItem.getKichCo());
            orderItem.setMauSac(cartItem.getMauSac());
            orderItemRepository.save(orderItem);
            
            // Cập nhật tồn kho
            Product product = cartItem.getSanPham();
            product.setSoLuongTon(product.getSoLuongTon() - cartItem.getSoLuong());
            product.setDaBan(product.getDaBan() + cartItem.getSoLuong());
            productRepository.save(product);
        }
        
        // Xóa giỏ hàng
        cartService.clearCart(userId);
        
        log.info("Created order {} for user {}", order.getMaDonHang(), user.getEmail());
        return order;
    }
    
    /**
     * Lấy danh sách đơn hàng của người dùng
     */
    public List<Order> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByNguoiDungId(userId);
        // Force load orderItems to avoid lazy loading issues
        for (Order order : orders) {
            if (order.getOrderItems() != null) {
                order.getOrderItems().size(); // Force load
            }
        }
        return orders;
    }
    
    /**
     * Lấy chi tiết đơn hàng
     */
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }
    
    /**
     * Lấy đơn hàng theo mã đơn hàng
     */
    public Optional<Order> getOrderByMaDonHang(String maDonHang) {
        return orderRepository.findByMaDonHang(maDonHang);
    }
    
    /**
     * Lưu đơn hàng
     */
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
    
    /**
     * Lấy đơn hàng theo mã đơn hàng
     */
    public Optional<Order> getOrderByCode(String orderCode) {
        return orderRepository.findByMaDonHang(orderCode);
    }
    
    /**
     * Cập nhật trạng thái đơn hàng
     */
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setTrangThai(newStatus);
        return orderRepository.save(order);
    }
    
    /**
     * Hủy đơn hàng
     */
    public Order cancelOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!"CHO_XAC_NHAN".equals(order.getTrangThai())) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getTrangThai());
        }
        
        // Hoàn trả tồn kho
        List<OrderItem> orderItems = orderItemRepository.findByDonHang(order);
        for (OrderItem item : orderItems) {
            Product product = item.getSanPham();
            product.setSoLuongTon(product.getSoLuongTon() + item.getSoLuong());
            product.setDaBan(product.getDaBan() - item.getSoLuong());
            productRepository.save(product);
        }
        
        order.setTrangThai("DA_HUY");
        order.setGhiChu(reason);
        
        log.info("Cancelled order {} for reason: {}", order.getMaDonHang(), reason);
        return orderRepository.save(order);
    }
    
    /**
     * Tính phí vận chuyển
     */
    private BigDecimal calculateShippingFee(BigDecimal orderTotal, String province) {
        // Miễn phí vận chuyển cho đơn hàng trên 500,000 VNĐ
        if (orderTotal.compareTo(BigDecimal.valueOf(500000)) >= 0) {
            return BigDecimal.ZERO;
        }
        
        // Phí vận chuyển theo tỉnh thành
        switch (province) {
            case "TP.HCM":
            case "Hà Nội":
                return BigDecimal.valueOf(30000);
            case "Đà Nẵng":
            case "Cần Thơ":
                return BigDecimal.valueOf(40000);
            default:
                return BigDecimal.valueOf(50000);
        }
    }
    
    /**
     * Tạo mã đơn hàng
     */
    private String generateOrderCode() {
        return "DH" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
    
    /**
     * Xây dựng địa chỉ đầy đủ
     */
    private String buildFullAddress(DiaChi address) {
        StringBuilder fullAddress = new StringBuilder();
        if (address.getDiaChi() != null) {
            fullAddress.append(address.getDiaChi());
        }
        if (address.getQuanHuyen() != null) {
            fullAddress.append(", ").append(address.getQuanHuyen());
        }
        if (address.getTinhThanh() != null) {
            fullAddress.append(", ").append(address.getTinhThanh());
        }
        return fullAddress.toString();
    }
    
    /**
     * Lấy thống kê đơn hàng
     */
    public OrderStats getOrderStats(Long userId) {
        List<Order> orders = getUserOrders(userId);
        
        long totalOrders = orders.size();
        long pendingOrders = orders.stream().filter(o -> "CHO_XAC_NHAN".equals(o.getTrangThai())).count();
        long completedOrders = orders.stream().filter(o -> "DA_GIAO".equals(o.getTrangThai())).count();
        long cancelledOrders = orders.stream().filter(o -> "DA_HUY".equals(o.getTrangThai())).count();
        
        BigDecimal totalSpent = orders.stream()
                .filter(o -> "DA_GIAO".equals(o.getTrangThai()))
                .map(Order::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new OrderStats(totalOrders, pendingOrders, completedOrders, cancelledOrders, totalSpent);
    }
    
    /**
     * Inner class cho thống kê đơn hàng
     */
    public static class OrderStats {
        public final long totalOrders;
        public final long pendingOrders;
        public final long completedOrders;
        public final long cancelledOrders;
        public final BigDecimal totalSpent;
        
        public OrderStats(long totalOrders, long pendingOrders, long completedOrders, 
                         long cancelledOrders, BigDecimal totalSpent) {
            this.totalOrders = totalOrders;
            this.pendingOrders = pendingOrders;
            this.completedOrders = completedOrders;
            this.cancelledOrders = cancelledOrders;
            this.totalSpent = totalSpent;
        }
    }
}
