package com.example.demo.repository;

import com.example.demo.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VaiTroRepository extends JpaRepository<VaiTro, Long> {
    
    /**
     * Tìm vai trò theo tên vai trò
     */
    Optional<VaiTro> findByTenVaiTro(String tenVaiTro);
    
    /**
     * Kiểm tra vai trò có tồn tại theo tên không
     */
    boolean existsByTenVaiTro(String tenVaiTro);
}
