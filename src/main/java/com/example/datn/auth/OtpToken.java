package com.example.datn.auth;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_token")
public class OtpToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column(name = "otp_code", nullable = false, length = 10)
	private String code;

	@Column(name = "expiry_time", nullable = false)
	private LocalDateTime expiresAt;

	@Column(nullable = false)
	private boolean used;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getCode() { return code; }
	public void setCode(String code) { this.code = code; }
	public LocalDateTime getExpiresAt() { return expiresAt; }
	public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
	public boolean isUsed() { return used; }
	public void setUsed(boolean used) { this.used = used; }
} 