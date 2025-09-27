package com.example.datn.admin.sport;

import com.example.datn.sport.DanhMucMonTheThao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin/sports")
public class AdminSportController {
    
    @Autowired
    private AdminSportService adminSportService;
    
    /**
     * Trang danh sách môn thể thao
     */
    @GetMapping
    public String sportsList(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "ten") String sortBy,
                           @RequestParam(defaultValue = "asc") String sortDir,
                           @RequestParam(required = false) String search,
                           Model model) {
        
        Page<DanhMucMonTheThao> sportsPage = adminSportService.getAllSports(page, size, sortBy, sortDir);
        
        model.addAttribute("sports", sportsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sportsPage.getTotalPages());
        model.addAttribute("totalElements", sportsPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("search", search);
        
        return "admin/sports";
    }
    
    /**
     * Trang thêm môn thể thao
     */
    @GetMapping("/add")
    public String addSportForm(Model model) {
        model.addAttribute("sport", new DanhMucMonTheThao());
        return "admin/sport-form";
    }
    
    /**
     * Trang sửa môn thể thao
     */
    @GetMapping("/edit/{id}")
    public String editSportForm(@PathVariable Long id, Model model) {
        DanhMucMonTheThao sport = adminSportService.getSportById(id);
        if (sport == null) {
            return "redirect:/admin/sports";
        }
        model.addAttribute("sport", sport);
        return "admin/sport-form";
    }
    
    /**
     * Lưu môn thể thao
     */
    @PostMapping("/save")
    public String saveSport(@ModelAttribute("sport") DanhMucMonTheThao sport,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           RedirectAttributes redirectAttributes) {
        try {
            // Xử lý hình ảnh nếu có
            if (imageFile != null && !imageFile.isEmpty()) {
                // Xóa hình ảnh cũ nếu có
                if (sport.getId() != null) {
                    DanhMucMonTheThao existingSport = adminSportService.getSportById(sport.getId());
                    if (existingSport != null && existingSport.getHinhAnh() != null) {
                        adminSportService.deleteSportImage(existingSport.getHinhAnh());
                    }
                }
                
                // Lưu hình ảnh mới
                String imagePath = adminSportService.saveSportImage(imageFile);
                sport.setHinhAnh(imagePath);
            }
            
            adminSportService.saveSport(sport);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu môn thể thao thành công!");
            
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lưu hình ảnh: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lưu môn thể thao: " + e.getMessage());
        }
        
        return "redirect:/admin/sports";
    }
    
    /**
     * Xóa môn thể thao
     */
    @PostMapping("/delete/{id}")
    public String deleteSport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            DanhMucMonTheThao sport = adminSportService.getSportById(id);
            if (sport != null) {
                // Xóa hình ảnh nếu có
                if (sport.getHinhAnh() != null) {
                    adminSportService.deleteSportImage(sport.getHinhAnh());
                }
                adminSportService.deleteSport(id);
                redirectAttributes.addFlashAttribute("successMessage", "Xóa môn thể thao thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy môn thể thao!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa môn thể thao: " + e.getMessage());
        }
        
        return "redirect:/admin/sports";
    }
    
    /**
     * Toggle trạng thái hoạt động
     */
    @PostMapping("/toggle/{id}")
    public String toggleSportStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminSportService.toggleSportStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        
        return "redirect:/admin/sports";
    }
}
