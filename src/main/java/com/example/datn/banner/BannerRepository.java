package com.example.datn.banner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    
    // Tìm banner theo vị trí và hoạt động
    List<Banner> findByViTriAndHoatDongOrderByThuTuAsc(String viTri, Boolean hoatDong);
    
    // Tìm tất cả banner hoạt động, sắp xếp theo thứ tự
    List<Banner> findByHoatDongOrderByThuTuAsc(Boolean hoatDong);
    
    // Tìm banner theo vị trí, sắp xếp theo thứ tự
    List<Banner> findByViTriOrderByThuTuAsc(String viTri);
    
    // Đếm số banner theo vị trí
    long countByViTri(String viTri);
    
    // Tìm banner theo tên (tìm kiếm)
    List<Banner> findByTenContainingIgnoreCase(String ten);
    
    // Tìm banner theo ID và hoạt động
    Optional<Banner> findByIdAndHoatDong(Long id, Boolean hoatDong);
}
