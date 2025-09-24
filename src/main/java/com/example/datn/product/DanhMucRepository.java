package com.example.datn.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Long> {

    // Tìm danh mục cha (không có danh mục cha)
    @Query("SELECT d FROM DanhMuc d WHERE d.danhMucCha IS NULL")
    List<DanhMuc> findDanhMucCha();

    // Tìm danh mục con theo danh mục cha
    @Query("SELECT d FROM DanhMuc d WHERE d.danhMucCha.id = :idCha")
    List<DanhMuc> findDanhMucCon(Long idCha);

    // Tìm danh mục theo tên
    List<DanhMuc> findByTenContainingIgnoreCase(String ten);
}
