package com.example.datn.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
	Optional<NguoiDung> findByEmail(String email);
	boolean existsByEmail(String email);
	
	// Admin management methods
	long countByHoatDongTrue();
	Page<NguoiDung> findByTenContainingOrEmailContaining(String ten, String email, Pageable pageable);
	List<NguoiDung> findByTenContainingOrEmailContaining(String ten, String email);
} 