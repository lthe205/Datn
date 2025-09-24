package com.example.datn.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Long> {

    // Tìm thương hiệu theo tên
    List<ThuongHieu> findByTenContainingIgnoreCase(String ten);

    // Tìm thương hiệu theo tên chính xác
    ThuongHieu findByTen(String ten);
}
