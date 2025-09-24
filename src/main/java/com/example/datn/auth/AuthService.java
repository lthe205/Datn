package com.example.datn.auth;

import com.example.datn.common.EmailService;
import com.example.datn.user.NguoiDung;
import com.example.datn.user.NguoiDungRepository;
import com.example.datn.user.VaiTro;
import com.example.datn.user.VaiTroRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class AuthService {

	private final NguoiDungRepository nguoiDungRepository;
	private final VaiTroRepository vaiTroRepository;
	private final PasswordEncoder passwordEncoder;
	private final OtpTokenRepository otpTokenRepository;
	private final EmailService emailService;
	private final SecureRandom random = new SecureRandom();

	public AuthService(NguoiDungRepository nguoiDungRepository, VaiTroRepository vaiTroRepository, PasswordEncoder passwordEncoder, OtpTokenRepository otpTokenRepository, EmailService emailService) {
		this.nguoiDungRepository = nguoiDungRepository;
		this.vaiTroRepository = vaiTroRepository;
		this.passwordEncoder = passwordEncoder;
		this.otpTokenRepository = otpTokenRepository;
		this.emailService = emailService;
	}

	@Transactional
	public void startRegister(String ten, String email, String rawPassword) {
		if (nguoiDungRepository.existsByEmail(email)) {
			throw new IllegalArgumentException("Email đã tồn tại");
		}
		// create inactive user
		VaiTro roleUser = vaiTroRepository.findByTenVaiTro("USER")
			.orElseGet(() -> {
				VaiTro r = new VaiTro();
				r.setTenVaiTro("USER");
				return vaiTroRepository.save(r);
			});
		NguoiDung user = new NguoiDung();
		user.setTen(ten);
		user.setEmail(email);
		user.setMatKhau(rawPassword);
		user.setVaiTro(roleUser);
		user.setHoatDong(false); // inactive until verify
		user.setBiKhoa(false);
		user.setNgayTao(LocalDateTime.now());
		user.setNgayCapNhat(LocalDateTime.now());
		nguoiDungRepository.save(user);

		// purge old tokens and create new
		otpTokenRepository.deleteByEmailOrExpiresAtBefore(email, LocalDateTime.now().minusDays(1));
		String code = String.format("%06d", random.nextInt(1_000_000));
		OtpToken token = new OtpToken();
		token.setEmail(email);
		token.setCode(code);
		token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
		token.setUsed(false);
		otpTokenRepository.save(token);

		emailService.sendOtpEmail(email, code);
	}

	@Transactional
	public boolean verifyOtp(String email, String code) {
		OtpToken latest = otpTokenRepository.findTopByEmailAndUsedFalseOrderByIdDesc(email)
			.orElse(null);
		if (latest == null) return false;
		if (latest.isUsed()) return false;
		if (latest.getExpiresAt().isBefore(LocalDateTime.now())) return false;
		if (!latest.getCode().equals(code)) return false;

		latest.setUsed(true);
		otpTokenRepository.save(latest);

		NguoiDung user = nguoiDungRepository.findByEmail(email).orElse(null);
		if (user == null) return false;
		user.setHoatDong(true);
		user.setNgayCapNhat(LocalDateTime.now());
		nguoiDungRepository.save(user);
		return true;
	}
} 