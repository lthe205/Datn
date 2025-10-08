package com.example.demo.repository;

import com.example.demo.entity.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, Long> {
    
    /**
     * Tìm thanh toán theo mã giao dịch
     */
    Optional<ThanhToan> findByMaGiaoDich(String maGiaoDich);
    
    // Method findByMaGiaoDichVnpay đã được xóa vì trường maGiaoDichVnpay là @Transient
    
    /**
     * Tìm tất cả thanh toán của một đơn hàng
     */
    List<ThanhToan> findByDonHangIdOrderByNgayTaoDesc(Long donHangId);
    
    /**
     * Tìm thanh toán thành công của một đơn hàng
     */
    @Query("SELECT t FROM ThanhToan t WHERE t.donHang.id = :donHangId AND t.trangThai = 'SUCCESS' ORDER BY t.ngayTao DESC")
    List<ThanhToan> findSuccessfulPaymentsByOrderId(@Param("donHangId") Long donHangId);
    
    /**
     * Kiểm tra xem đơn hàng đã được thanh toán thành công chưa
     */
    @Query("SELECT COUNT(t) > 0 FROM ThanhToan t WHERE t.donHang.id = :donHangId AND t.trangThai = 'SUCCESS'")
    boolean existsSuccessfulPaymentByOrderId(@Param("donHangId") Long donHangId);
}
