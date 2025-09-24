package com.example.datn.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnhSanPhamRepository extends JpaRepository<AnhSanPham, Long> {

    // Tìm ảnh theo sản phẩm, sắp xếp theo thứ tự
    @Query("SELECT a FROM AnhSanPham a WHERE a.sanPham.id = :sanPhamId ORDER BY a.thuTu ASC")
    List<AnhSanPham> findBySanPhamIdOrderByThuTuAsc(@Param("sanPhamId") Long sanPhamId);

    // Tìm ảnh chính của sản phẩm (thứ tự = 1)
    @Query("SELECT a FROM AnhSanPham a WHERE a.sanPham.id = :sanPhamId AND a.thuTu = 1")
    AnhSanPham findAnhChinhBySanPhamId(@Param("sanPhamId") Long sanPhamId);

    // Đếm số ảnh của sản phẩm
    @Query("SELECT COUNT(a) FROM AnhSanPham a WHERE a.sanPham.id = :sanPhamId")
    Long countBySanPhamId(@Param("sanPhamId") Long sanPhamId);
}
