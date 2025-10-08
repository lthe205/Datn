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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    /**
     * Lấy giỏ hàng của người dùng
     */
    public Optional<Cart> getCartByUser(User user) {
        return cartRepository.findByNguoiDung(user);
    }
    
    /**
     * Tạo giỏ hàng mới cho người dùng
     */
    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setNguoiDung(user);
        return cartRepository.save(cart);
    }
    
    /**
     * Lấy hoặc tạo giỏ hàng cho người dùng
     */
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByNguoiDung(user)
                .orElseGet(() -> createCart(user));
    }
    
    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    public CartItem addToCart(Long userId, Long productId, Integer quantity, String size, String color) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (!product.getHoatDong() || product.getSoLuongTon() < quantity) {
            throw new RuntimeException("Product not available or insufficient stock");
        }
        
        Cart cart = getOrCreateCart(user);
        
        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProductAndVariants(
                cart.getId(), productId, size, color);
        
        if (existingItem.isPresent()) {
            // Tăng số lượng nếu đã có
            CartItem item = existingItem.get();
            item.setSoLuong(item.getSoLuong() + quantity);
            return cartItemRepository.save(item);
        } else {
            // Tạo mới
            CartItem newItem = new CartItem();
            newItem.setGioHang(cart);
            newItem.setSanPham(product);
            newItem.setSoLuong(quantity);
            newItem.setGia(product.getGia());
            newItem.setKichCo(size);
            newItem.setMauSac(color);
            return cartItemRepository.save(newItem);
        }
    }
    
    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     */
    public CartItem updateCartItemQuantity(Long cartItemId, Integer newQuantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (newQuantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }
        
        // Kiểm tra tồn kho
        if (cartItem.getSanPham().getSoLuongTon() < newQuantity) {
            throw new RuntimeException("Insufficient stock");
        }
        
        cartItem.setSoLuong(newQuantity);
        return cartItemRepository.save(cartItem);
    }
    
    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    public void removeFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
    
    /**
     * Xóa toàn bộ giỏ hàng
     */
    public void clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Optional<Cart> cartOpt = cartRepository.findByNguoiDung(user);
        if (cartOpt.isPresent()) {
            cartItemRepository.deleteByGioHang(cartOpt.get());
        }
    }
    
    /**
     * Lấy danh sách sản phẩm trong giỏ hàng
     */
    public List<CartItem> getCartItems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Optional<Cart> cartOpt = cartRepository.findByNguoiDung(user);
        if (cartOpt.isPresent()) {
            return cartItemRepository.findByGioHang(cartOpt.get());
        }
        return List.of();
    }
    
    /**
     * Tính tổng tiền giỏ hàng
     */
    public BigDecimal getCartTotal(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Optional<Cart> cartOpt = cartRepository.findByNguoiDung(user);
        if (cartOpt.isPresent()) {
            Double total = cartItemRepository.getTotalAmountByCartId(cartOpt.get().getId());
            return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Đếm số lượng sản phẩm trong giỏ hàng
     */
    public Integer getCartItemCount(Long userId) {
        List<CartItem> items = getCartItems(userId);
        return items.stream()
                .mapToInt(CartItem::getSoLuong)
                .sum();
    }
}
