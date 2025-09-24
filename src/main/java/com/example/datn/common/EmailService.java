package com.example.datn.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	private final JavaMailSender mailSender;
	@Value("${app.mail.from:no-reply@activewear.local}")
	private String from;

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendOtpEmail(String to, String code) {
		String subject = "Mã xác thực đăng ký (OTP)";
		String html = "<div style='font-family:Arial,sans-serif'>" +
			"<h2>Activewear</h2>" +
			"<p>Mã OTP của bạn là:</p>" +
			"<div style='font-size:24px;font-weight:700;background:#f1f5f9;border-radius:8px;display:inline-block;padding:8px 16px;letter-spacing:4px'>" + code + "</div>" +
			"<p>Có hiệu lực trong 10 phút. Nếu không phải bạn, hãy bỏ qua email này.</p>" +
			"</div>";
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(html, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("Không gửi được email OTP", e);
		}
	}
} 