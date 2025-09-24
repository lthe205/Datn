package com.example.datn.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long> {

    // Tìm sản phẩm theo danh mục
    @Query("SELECT s FROM SanPham s WHERE s.danhMuc.id = :danhMucId AND s.hoatDong = true")
    Page<SanPham> findByDanhMucIdAndHoatDongTrue(@Param("danhMucId") Long danhMucId, Pageable pageable);

    // Tìm sản phẩm theo thương hiệu
    @Query("SELECT s FROM SanPham s WHERE s.thuongHieu.id = :thuongHieuId AND s.hoatDong = true")
    Page<SanPham> findByThuongHieuIdAndHoatDongTrue(@Param("thuongHieuId") Long thuongHieuId, Pageable pageable);

    // Tìm sản phẩm nổi bật
    @Query("SELECT s FROM SanPham s WHERE s.noiBat = true AND s.hoatDong = true ORDER BY s.ngayTao DESC")
    List<SanPham> findNoiBatAndHoatDongTrue();

    // Tìm sản phẩm mới nhất
    @Query("SELECT s FROM SanPham s WHERE s.hoatDong = true ORDER BY s.ngayTao DESC")
    Page<SanPham> findHoatDongTrueOrderByNgayTaoDesc(Pageable pageable);

    // Tìm sản phẩm bán chạy nhất
    @Query("SELECT s FROM SanPham s WHERE s.hoatDong = true ORDER BY s.daBan DESC")
    Page<SanPham> findHoatDongTrueOrderByDaBanDesc(Pageable pageable);

    // Tìm sản phẩm theo tên
    @Query("SELECT s FROM SanPham s WHERE s.ten LIKE %:ten% AND s.hoatDong = true")
    Page<SanPham> findByTenContainingAndHoatDongTrue(@Param("ten") String ten, Pageable pageable);

    // Tìm sản phẩm theo khoảng giá
    @Query("SELECT s FROM SanPham s WHERE s.gia BETWEEN :giaMin AND :giaMax AND s.hoatDong = true")
    Page<SanPham> findByGiaBetweenAndHoatDongTrue(@Param("giaMin") BigDecimal giaMin, 
                                                  @Param("giaMax") BigDecimal giaMax, 
                                                  Pageable pageable);

    // Tìm sản phẩm theo từ khóa tìm kiếm
    @Query("SELECT s FROM SanPham s WHERE " +
           "(s.ten LIKE %:keyword% OR s.moTa LIKE %:keyword% OR s.thuongHieu.ten LIKE %:keyword%) " +
           "AND s.hoatDong = true")
    Page<SanPham> findByKeywordAndHoatDongTrue(@Param("keyword") String keyword, Pageable pageable);

    // Tìm sản phẩm có khuyến mãi
    @Query("SELECT s FROM SanPham s WHERE s.gia < s.giaGoc AND s.hoatDong = true ORDER BY (s.giaGoc - s.gia) DESC")
    Page<SanPham> findSanPhamKhuyenMai(Pageable pageable);

    // Đếm sản phẩm theo danh mục
    @Query("SELECT COUNT(s) FROM SanPham s WHERE s.danhMuc.id = :danhMucId AND s.hoatDong = true")
    Long countByDanhMucIdAndHoatDongTrue(@Param("danhMucId") Long danhMucId);

    // Đếm sản phẩm theo thương hiệu
    @Query("SELECT COUNT(s) FROM SanPham s WHERE s.thuongHieu.id = :thuongHieuId AND s.hoatDong = true")
    Long countByThuongHieuIdAndHoatDongTrue(@Param("thuongHieuId") Long thuongHieuId);
    
    // Admin management methods
    long countByHoatDongTrue();
    Page<SanPham> findByTenContainingOrMaSanPhamContaining(String ten, String maSanPham, Pageable pageable);
    List<SanPham> findByTenContainingOrMaSanPhamContaining(String ten, String maSanPham);
}
