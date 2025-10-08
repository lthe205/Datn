package com.example.demo.controller;

import com.example.demo.entity.DiaChi;
import com.example.demo.entity.User;
import com.example.demo.repository.DiaChiRepository;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Slf4j
public class AddressController {
    
    private final DiaChiRepository diaChiRepository;
    private final UserService userService;
    
    /**
     * Lấy danh sách địa chỉ của người dùng
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserAddresses(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            List<DiaChi> addresses = diaChiRepository.findByUserIdOrderByDefaultAndDate(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("addresses", addresses);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting user addresses", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Lấy địa chỉ mặc định của người dùng
     */
    @GetMapping("/default")
    public ResponseEntity<Map<String, Object>> getDefaultAddress(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            var defaultAddress = diaChiRepository.findDefaultByUserId(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("address", defaultAddress.orElse(null));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting default address", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Thêm địa chỉ mới
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addAddress(
            @RequestParam String hoTenNhan,
            @RequestParam String soDienThoai,
            @RequestParam String diaChi,
            @RequestParam String quanHuyen,
            @RequestParam String tinhThanh,
            @RequestParam(defaultValue = "false") boolean macDinh,
            Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            
            // Nếu đặt làm mặc định, bỏ mặc định của các địa chỉ khác
            if (macDinh) {
                List<DiaChi> existingAddresses = diaChiRepository.findByNguoiDungId(user.getId());
                for (DiaChi existingAddress : existingAddresses) {
                    if (existingAddress.getMacDinh()) {
                        existingAddress.setMacDinh(false);
                        diaChiRepository.save(existingAddress);
                    }
                }
            }
            
            // Tạo địa chỉ mới
            DiaChi newAddress = new DiaChi();
            newAddress.setNguoiDung(user);
            newAddress.setHoTenNhan(hoTenNhan);
            newAddress.setSoDienThoai(soDienThoai);
            newAddress.setDiaChi(diaChi);
            newAddress.setQuanHuyen(quanHuyen);
            newAddress.setTinhThanh(tinhThanh);
            newAddress.setMacDinh(macDinh);
            
            DiaChi savedAddress = diaChiRepository.save(newAddress);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Địa chỉ đã được thêm thành công");
            response.put("address", savedAddress);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error adding address", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Cập nhật địa chỉ
     */
    @PutMapping("/{addressId}")
    public ResponseEntity<Map<String, Object>> updateAddress(
            @PathVariable Long addressId,
            @RequestParam String hoTenNhan,
            @RequestParam String soDienThoai,
            @RequestParam String diaChi,
            @RequestParam String quanHuyen,
            @RequestParam String tinhThanh,
            @RequestParam(defaultValue = "false") boolean macDinh,
            Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            
            DiaChi existingAddress = diaChiRepository.findById(addressId)
                    .orElseThrow(() -> new RuntimeException("Address not found"));
            
            // Kiểm tra quyền sở hữu
            if (!existingAddress.getNguoiDung().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "Không có quyền truy cập"));
            }
            
            // Nếu đặt làm mặc định, bỏ mặc định của các địa chỉ khác
            if (macDinh) {
                List<DiaChi> userAddresses = diaChiRepository.findByNguoiDungId(user.getId());
                for (DiaChi address : userAddresses) {
                    if (address.getMacDinh() && !address.getId().equals(addressId)) {
                        address.setMacDinh(false);
                        diaChiRepository.save(address);
                    }
                }
            }
            
            // Cập nhật thông tin
            existingAddress.setHoTenNhan(hoTenNhan);
            existingAddress.setSoDienThoai(soDienThoai);
            existingAddress.setDiaChi(diaChi);
            existingAddress.setQuanHuyen(quanHuyen);
            existingAddress.setTinhThanh(tinhThanh);
            existingAddress.setMacDinh(macDinh);
            
            DiaChi updatedAddress = diaChiRepository.save(existingAddress);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Địa chỉ đã được cập nhật thành công");
            response.put("address", updatedAddress);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating address", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Xóa địa chỉ
     */
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Map<String, Object>> deleteAddress(
            @PathVariable Long addressId,
            Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            
            DiaChi address = diaChiRepository.findById(addressId)
                    .orElseThrow(() -> new RuntimeException("Address not found"));
            
            // Kiểm tra quyền sở hữu
            if (!address.getNguoiDung().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "Không có quyền truy cập"));
            }
            
            diaChiRepository.delete(address);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Địa chỉ đã được xóa thành công");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting address", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Đặt địa chỉ làm mặc định
     */
    @PostMapping("/{addressId}/set-default")
    public ResponseEntity<Map<String, Object>> setDefaultAddress(
            @PathVariable Long addressId,
            Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            
            DiaChi address = diaChiRepository.findById(addressId)
                    .orElseThrow(() -> new RuntimeException("Address not found"));
            
            // Kiểm tra quyền sở hữu
            if (!address.getNguoiDung().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "Không có quyền truy cập"));
            }
            
            // Bỏ mặc định của các địa chỉ khác
            List<DiaChi> userAddresses = diaChiRepository.findByNguoiDungId(user.getId());
            for (DiaChi userAddress : userAddresses) {
                if (userAddress.getMacDinh() && !userAddress.getId().equals(addressId)) {
                    userAddress.setMacDinh(false);
                    diaChiRepository.save(userAddress);
                }
            }
            
            // Đặt địa chỉ này làm mặc định
            address.setMacDinh(true);
            diaChiRepository.save(address);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Địa chỉ đã được đặt làm mặc định");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error setting default address", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
