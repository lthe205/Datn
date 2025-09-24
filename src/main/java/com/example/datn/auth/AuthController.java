package com.example.datn.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Controller
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}

	@GetMapping("/dang-nhap")
	public String dangNhap() {
		return "auth/login";
	}

	@GetMapping("/register")
	public String registerForm(Model model) {
		model.addAttribute("form", new RegisterForm());
		return "auth/register";
	}

	@GetMapping("/dang-ky")
	public String dangKy(Model model) {
		model.addAttribute("form", new RegisterForm());
		return "auth/register";
	}

	@PostMapping("/register")
	public String doRegister(@ModelAttribute("form") RegisterForm form, BindingResult result, Model model) {
		if (form.ten == null || form.ten.isBlank()) {
			result.rejectValue("ten", "ten.blank", "Vui lòng nhập họ tên");
		}
		if (form.email == null || form.email.isBlank()) {
			result.rejectValue("email", "email.blank", "Vui lòng nhập email");
		}
		if (form.password == null || form.password.length() < 6) {
			result.rejectValue("password", "password.short", "Mật khẩu tối thiểu 6 ký tự");
		}
		if (result.hasErrors()) {
			return "auth/register";
		}
		try {
			authService.startRegister(form.ten, form.email, form.password);
		} catch (IllegalArgumentException ex) {
			model.addAttribute("error", ex.getMessage());
			return "auth/register";
		}
		model.addAttribute("email", form.email);
		return "auth/verify";
	}

	@PostMapping("/dang-ky")
	public String doDangKy(@ModelAttribute("form") RegisterForm form, BindingResult result, Model model) {
		return doRegister(form, result, model);
	}

	@GetMapping("/dang-xuat")
	public String dangXuat() {
		return "redirect:/logout";
	}

	@GetMapping("/verify")
	public String verifyPage(@RequestParam(value = "email", required = false) String email, Model model) {
		model.addAttribute("email", email);
		return "auth/verify";
	}

	@PostMapping("/verify")
	public String doVerify(@RequestParam("email") String email, @RequestParam("code") String code, Model model) {
		boolean ok = authService.verifyOtp(email, code);
		if (!ok) {
			model.addAttribute("email", email);
			model.addAttribute("error", "Mã OTP không hợp lệ hoặc đã hết hạn");
			return "auth/verify";
		}
		return "redirect:/login?verified";
	}

	public static class RegisterForm {
		@NotBlank public String ten;
		@NotBlank @Email public String email;
		@NotBlank public String password;
		public String getTen() { return ten; }
		public void setTen(String ten) { this.ten = ten; }
		public String getEmail() { return email; }
		public void setEmail(String email) { this.email = email; }
		public String getPassword() { return password; }
		public void setPassword(String password) { this.password = password; }
	}
} 