package com.example.datn.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
	Optional<NguoiDung> findByEmail(String email);
	boolean existsByEmail(String email);
	
	// Admin management methods
	long countByHoatDongTrue();
	Page<NguoiDung> findByTenContainingOrEmailContaining(String ten, String email, Pageable pageable);
	List<NguoiDung> findByTenContainingOrEmailContaining(String ten, String email);
	
	// Statistics methods
	long countByHoatDong(boolean hoatDong);
	long countByBiKhoa(boolean biKhoa);
	long countByNgayTaoAfter(LocalDateTime ngayTao);
	
	// Filter methods
	Page<NguoiDung> findByHoatDong(boolean hoatDong, Pageable pageable);
	Page<NguoiDung> findByBiKhoa(boolean biKhoa, Pageable pageable);
	Page<NguoiDung> findByVaiTroId(Long vaiTroId, Pageable pageable);
	
	// Combined filters
	@Query("SELECT u FROM NguoiDung u WHERE " +
	       "(:search IS NULL OR u.ten LIKE %:search% OR u.email LIKE %:search%) AND " +
	       "(:role IS NULL OR u.vaiTro.id = :role) AND " +
	       "((:status = 'active' AND u.hoatDong = true) OR " +
	       "(:status = 'locked' AND u.biKhoa = true) OR " +
	       "(:status = 'inactive' AND u.hoatDong = false) OR " +
	       "(:status IS NULL))")
	Page<NguoiDung> findWithFilters(String search, Long role, String status, Pageable pageable);
} 