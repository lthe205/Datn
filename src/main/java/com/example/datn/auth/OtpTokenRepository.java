package com.example.datn.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
	Optional<OtpToken> findTopByEmailAndUsedFalseOrderByIdDesc(String email);

	@Modifying
	@Transactional
	void deleteByEmailOrExpiresAtBefore(String email, LocalDateTime before);
} 