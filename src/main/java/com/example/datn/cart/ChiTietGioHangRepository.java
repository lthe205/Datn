package com.example.datn.cart;

import com.example.datn.product.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, Long> {
    List<ChiTietGioHang> findByGioHangId(Long gioHangId);
    Optional<ChiTietGioHang> findByGioHangAndSanPham(GioHang gioHang, SanPham sanPham);
    Optional<ChiTietGioHang> findByGioHangAndSanPhamAndKichCoAndMauSac(GioHang gioHang, SanPham sanPham, String kichCo, String mauSac);
    void deleteByGioHangId(Long gioHangId);
    void deleteByGioHangAndSanPham(GioHang gioHang, SanPham sanPham);
}
