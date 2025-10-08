package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByMaDonHang(String maDonHang);
    
    List<Order> findByNguoiDung(User user);
    
    List<Order> findByNguoiDungId(Long userId);
    
    Page<Order> findByNguoiDungIdOrderByNgayTaoDesc(Long userId, Pageable pageable);
    
    List<Order> findByTrangThai(String trangThai);
    
    @Query("SELECT o FROM Order o WHERE o.nguoiDung.id = :userId AND o.trangThai = :status")
    List<Order> findByUserAndStatus(@Param("userId") Long userId, @Param("status") String status);
    
    @Query("SELECT o FROM Order o WHERE o.ngayTao BETWEEN :startDate AND :endDate")
    List<Order> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.trangThai = :status")
    Long countByStatus(@Param("status") String status);
    
    @Query("SELECT SUM(o.tongTien) FROM Order o WHERE o.trangThai = 'DA_GIAO'")
    Double getTotalRevenue();
    
    @Query("SELECT SUM(o.tongTien) FROM Order o WHERE o.trangThai = 'DA_GIAO' AND o.ngayTao >= :startDate")
    Double getRevenueFromDate(@Param("startDate") LocalDateTime startDate);
}
