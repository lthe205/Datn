package com.example.datn.sport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DanhMucMonTheThaoRepository extends JpaRepository<DanhMucMonTheThao, Long> {
    
    /**
     * Tìm tất cả danh mục môn thể thao đang hoạt động, sắp xếp theo thứ tự
     */
    @Query("SELECT d FROM DanhMucMonTheThao d WHERE d.hoatDong = true ORDER BY d.thuTu ASC, d.ten ASC")
    List<DanhMucMonTheThao> findAllActiveOrderByThuTu();
    
    /**
     * Tìm danh mục môn thể thao theo tên
     */
    DanhMucMonTheThao findByTen(String ten);
    
    /**
     * Kiểm tra tồn tại theo tên
     */
    boolean existsByTen(String ten);
    
    /**
     * Tìm danh mục môn thể thao theo trạng thái hoạt động
     */
    List<DanhMucMonTheThao> findByHoatDong(Boolean hoatDong);
}
