package com.example.demo.repository;

import com.example.demo.entity.DiaChi;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, Long> {
    
    List<DiaChi> findByNguoiDung(User user);
    
    List<DiaChi> findByNguoiDungId(Long userId);
    
    Optional<DiaChi> findByNguoiDungAndMacDinhTrue(User user);
    
    @Query("SELECT d FROM DiaChi d WHERE d.nguoiDung.id = :userId AND d.macDinh = true")
    Optional<DiaChi> findDefaultByUserId(@Param("userId") Long userId);
    
    @Query("SELECT d FROM DiaChi d WHERE d.nguoiDung.id = :userId ORDER BY d.macDinh DESC, d.ngayTao DESC")
    List<DiaChi> findByUserIdOrderByDefaultAndDate(@Param("userId") Long userId);
}
