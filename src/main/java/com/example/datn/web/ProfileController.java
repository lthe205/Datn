package com.example.datn.web;

import com.example.datn.auth.CustomUserDetails;
import com.example.datn.cart.CartService;
import com.example.datn.product.DanhMucRepository;
import com.example.datn.product.ThuongHieuRepository;
import com.example.datn.user.NguoiDung;
import com.example.datn.user.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/thong-tin-ca-nhan")
public class ProfileController {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    @Autowired
    private CartService cartService;

    @GetMapping
    public String profile(Model model) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("currentUser", authentication);
        
        // Thêm thông tin giỏ hàng nếu user đã đăng nhập
        addCartInfoToModel(model, authentication);
        
        // Thêm dữ liệu cho dropdown navigation
        List<com.example.datn.product.DanhMuc> danhMucCha = danhMucRepository.findDanhMucCha();
        model.addAttribute("danhMucCha", danhMucCha);
        
        List<com.example.datn.product.ThuongHieu> thuongHieu = thuongHieuRepository.findAll();
        model.addAttribute("thuongHieu", thuongHieu);
        
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            
            NguoiDung nguoiDung = nguoiDungRepository.findByEmail(authentication.getName())
                .orElse(null);
            
            if (nguoiDung != null) {
                model.addAttribute("nguoiDung", nguoiDung);
                model.addAttribute("profileForm", new ProfileUpdateForm(nguoiDung));
                return "profile";
            }
        }
        
        return "redirect:/dang-nhap";
    }

    @PostMapping("/cap-nhat")
    public String updateProfile(@ModelAttribute("profileForm") ProfileUpdateForm form, 
                              RedirectAttributes redirectAttributes) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            
            NguoiDung nguoiDung = nguoiDungRepository.findByEmail(authentication.getName())
                .orElse(null);
            
            if (nguoiDung != null) {
                // Cập nhật thông tin
                nguoiDung.setTen(form.getTen());
                nguoiDung.setSoDienThoai(form.getSoDienThoai());
                nguoiDung.setDiaChi(form.getDiaChi());
                nguoiDung.setThanhPho(form.getThanhPho());
                nguoiDung.setGioiTinh(form.getGioiTinh());
                nguoiDung.setNgayCapNhat(LocalDateTime.now());
                
                nguoiDungRepository.save(nguoiDung);
                
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
                return "redirect:/thong-tin-ca-nhan";
            }
        }
        
        return "redirect:/dang-nhap";
    }

    @PostMapping("/doi-mat-khau")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                RedirectAttributes redirectAttributes) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            
            NguoiDung nguoiDung = nguoiDungRepository.findByEmail(authentication.getName())
                .orElse(null);
            
            if (nguoiDung != null) {
                // Kiểm tra mật khẩu hiện tại
                if (!passwordEncoder.matches(currentPassword, nguoiDung.getMatKhau())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu hiện tại không đúng!");
                    return "redirect:/thong-tin-ca-nhan";
                }
                
                // Kiểm tra mật khẩu mới
                if (newPassword.length() < 6) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới phải có ít nhất 6 ký tự!");
                    return "redirect:/thong-tin-ca-nhan";
                }
                
                // Kiểm tra xác nhận mật khẩu
                if (!newPassword.equals(confirmPassword)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Xác nhận mật khẩu không khớp!");
                    return "redirect:/thong-tin-ca-nhan";
                }
                
                // Cập nhật mật khẩu
                nguoiDung.setMatKhau(passwordEncoder.encode(newPassword));
                nguoiDung.setNgayCapNhat(LocalDateTime.now());
                
                nguoiDungRepository.save(nguoiDung);
                
                redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
                return "redirect:/thong-tin-ca-nhan";
            }
        }
        
        return "redirect:/dang-nhap";
    }

    // Form class cho cập nhật thông tin
    public static class ProfileUpdateForm {
        private String ten;
        private String soDienThoai;
        private String diaChi;
        private String thanhPho;
        private String gioiTinh;

        public ProfileUpdateForm() {}

        public ProfileUpdateForm(NguoiDung nguoiDung) {
            this.ten = nguoiDung.getTen();
            this.soDienThoai = nguoiDung.getSoDienThoai();
            this.diaChi = nguoiDung.getDiaChi();
            this.thanhPho = nguoiDung.getThanhPho();
            this.gioiTinh = nguoiDung.getGioiTinh();
        }

        // Getters and Setters
        public String getTen() { return ten; }
        public void setTen(String ten) { this.ten = ten; }

        public String getSoDienThoai() { return soDienThoai; }
        public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

        public String getDiaChi() { return diaChi; }
        public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

        public String getThanhPho() { return thanhPho; }
        public void setThanhPho(String thanhPho) { this.thanhPho = thanhPho; }

        public String getGioiTinh() { return gioiTinh; }
        public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    }

    // Helper method để thêm thông tin giỏ hàng vào model
    private void addCartInfoToModel(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() &&
            !authentication.getName().equals("anonymousUser")) {
            try {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                int cartItemCount = cartService.getCartItemCount(userDetails.getDomainUser());
                model.addAttribute("cartItemCount", cartItemCount);
            } catch (Exception e) {
                // Nếu có lỗi, set cartItemCount = 0
                model.addAttribute("cartItemCount", 0);
            }
        } else {
            model.addAttribute("cartItemCount", 0);
        }
    }
}
