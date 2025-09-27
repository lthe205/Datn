package com.example.datn.admin;

import com.example.datn.banner.Banner;
import com.example.datn.banner.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/banners")
public class BannerController {
    
    @Autowired
    private BannerService bannerService;
    
    // Danh sách banner
    @GetMapping
    public String listBanners(Model model, @RequestParam(required = false) String search) {
        List<Banner> banners;
        if (search != null && !search.trim().isEmpty()) {
            banners = bannerService.searchBanners(search);
            model.addAttribute("search", search);
        } else {
            banners = bannerService.getAllBanners();
        }
        
        model.addAttribute("banners", banners);
        return "admin/banners";
    }
    
    // Form thêm banner
    @GetMapping("/add")
    public String addBannerForm(Model model) {
        model.addAttribute("banner", new Banner());
        return "admin/banner-form";
    }
    
    // Form sửa banner
    @GetMapping("/edit/{id}")
    public String editBannerForm(@PathVariable Long id, Model model) {
        Banner banner = bannerService.getBannerById(id).orElse(null);
        if (banner == null) {
            return "redirect:/admin/banners";
        }
        model.addAttribute("banner", banner);
        return "admin/banner-form";
    }
    
    // Lưu banner
    @PostMapping("/save")
    public String saveBanner(@ModelAttribute Banner banner,
                           @RequestParam(required = false) MultipartFile file,
                           RedirectAttributes redirectAttributes) {
        try {
            if (banner.getId() == null) {
                // Tạo mới
                bannerService.createBanner(banner, file);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm banner thành công!");
            } else {
                // Cập nhật
                bannerService.updateBanner(banner.getId(), banner, file);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật banner thành công!");
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi upload ảnh: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/banners";
    }
    
    // Xóa banner
    @PostMapping("/delete/{id}")
    public String deleteBanner(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = bannerService.deleteBanner(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Xóa banner thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy banner!");
        }
        return "redirect:/admin/banners";
    }
    
    // Toggle trạng thái
    @PostMapping("/toggle/{id}")
    public String toggleBannerStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Banner banner = bannerService.toggleBannerStatus(id);
        if (banner != null) {
            String status = banner.getHoatDong() ? "kích hoạt" : "vô hiệu hóa";
            redirectAttributes.addFlashAttribute("successMessage", "Đã " + status + " banner!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy banner!");
        }
        return "redirect:/admin/banners";
    }
}
