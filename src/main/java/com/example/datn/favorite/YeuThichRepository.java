package com.example.datn.favorite;

import com.example.datn.product.SanPham;
import com.example.datn.user.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YeuThichRepository extends JpaRepository<YeuThich, Long> {
    List<YeuThich> findByNguoiDung(NguoiDung nguoiDung);
    Optional<YeuThich> findByNguoiDungAndSanPham(NguoiDung nguoiDung, SanPham sanPham);
    boolean existsByNguoiDungAndSanPham(NguoiDung nguoiDung, SanPham sanPham);
    void deleteByNguoiDungAndSanPham(NguoiDung nguoiDung, SanPham sanPham);
    long countByNguoiDung(NguoiDung nguoiDung);
}
