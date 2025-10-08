package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByMaSanPham(String maSanPham);
    
    List<Product> findByHoatDongTrue();
    
    List<Product> findByNoiBatTrue();
    
    Page<Product> findByHoatDongTrue(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.hoatDong = true AND p.soLuongTon > 0")
    List<Product> findAvailableProducts();
    
    @Query("SELECT p FROM Product p WHERE p.hoatDong = true AND " +
           "(p.ten LIKE %:keyword% OR p.moTa LIKE %:keyword%)")
    List<Product> searchProducts(@Param("keyword") String keyword);
    
    @Query("SELECT p FROM Product p WHERE p.hoatDong = true AND p.danhMuc.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);
    
    @Query("SELECT p FROM Product p WHERE p.hoatDong = true AND p.thuongHieu.id = :brandId")
    List<Product> findByBrandId(@Param("brandId") Long brandId);
    
    @Query("SELECT p FROM Product p WHERE p.hoatDong = true ORDER BY p.daBan DESC")
    List<Product> findTopSellingProducts(Pageable pageable);
}
