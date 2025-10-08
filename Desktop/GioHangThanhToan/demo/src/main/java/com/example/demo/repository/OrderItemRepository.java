package com.example.demo.repository;

import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByDonHang(Order order);
    
    List<OrderItem> findByDonHangId(Long orderId);
    
    @Query("SELECT SUM(oi.soLuong * oi.gia) FROM OrderItem oi WHERE oi.donHang.id = :orderId")
    Double getTotalAmountByOrderId(@Param("orderId") Long orderId);
    
    @Query("SELECT oi.sanPham.id, SUM(oi.soLuong) FROM OrderItem oi " +
           "WHERE oi.donHang.trangThai = 'DA_GIAO' " +
           "GROUP BY oi.sanPham.id ORDER BY SUM(oi.soLuong) DESC")
    List<Object[]> getTopSellingProducts();
}
