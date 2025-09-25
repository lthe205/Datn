package com.example.datn.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {

    @Query("SELECT COALESCE(SUM(d.tongTien), 0) FROM DonHang d WHERE CAST(d.ngayTao AS DATE) = :date AND d.trangThai IN ('DA_XAC_NHAN', 'DANG_GIAO', 'DA_GIAO')")
    BigDecimal getRevenueByDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(d.tongTien), 0) FROM DonHang d WHERE YEAR(d.ngayTao) = :year AND MONTH(d.ngayTao) = :month AND d.trangThai IN ('DA_XAC_NHAN', 'DANG_GIAO', 'DA_GIAO')")
    BigDecimal getRevenueByMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COALESCE(SUM(d.tongTien), 0) FROM DonHang d WHERE YEAR(d.ngayTao) = :year AND d.trangThai IN ('DA_XAC_NHAN', 'DANG_GIAO', 'DA_GIAO')")
    BigDecimal getRevenueByYear(@Param("year") int year);

    @Query("SELECT d FROM DonHang d WHERE d.ngayTao >= :startDate AND d.ngayTao <= :endDate AND d.trangThai IN ('DA_XAC_NHAN', 'DANG_GIAO', 'DA_GIAO') ORDER BY d.ngayTao DESC")
    List<DonHang> getOrdersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT CAST(d.ngayTao AS DATE) as orderDate, COALESCE(SUM(d.tongTien), 0) as revenue " +
           "FROM DonHang d " +
           "WHERE d.ngayTao >= :startDate AND d.ngayTao <= :endDate " +
           "AND d.trangThai IN ('DA_XAC_NHAN', 'DANG_GIAO', 'DA_GIAO') " +
           "GROUP BY CAST(d.ngayTao AS DATE) " +
           "ORDER BY orderDate")
    List<Object[]> getDailyRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT MONTH(d.ngayTao) as month, COALESCE(SUM(d.tongTien), 0) as revenue " +
           "FROM DonHang d " +
           "WHERE YEAR(d.ngayTao) = :year " +
           "AND d.trangThai IN ('DA_XAC_NHAN', 'DANG_GIAO', 'DA_GIAO') " +
           "GROUP BY MONTH(d.ngayTao) " +
           "ORDER BY month")
    List<Object[]> getMonthlyRevenue(@Param("year") int year);

    @Query("SELECT YEAR(d.ngayTao) as year, COALESCE(SUM(d.tongTien), 0) as revenue " +
           "FROM DonHang d " +
           "WHERE d.ngayTao >= :startDate " +
           "AND d.trangThai IN ('DA_XAC_NHAN', 'DANG_GIAO', 'DA_GIAO') " +
           "GROUP BY YEAR(d.ngayTao) " +
           "ORDER BY year")
    List<Object[]> getYearlyRevenue(@Param("startDate") LocalDateTime startDate);
}