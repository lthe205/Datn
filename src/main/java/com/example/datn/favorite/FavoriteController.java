package com.example.datn.favorite;

import com.example.datn.auth.AuthHelper;
import com.example.datn.user.NguoiDung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/yeu-thich")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleFavorite(@RequestParam Long sanPhamId,
                                                             Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập để sử dụng chức năng yêu thích");
            return ResponseEntity.ok(response);
        }

        try {
            NguoiDung nguoiDung = AuthHelper.getCurrentUser(authentication);
            boolean isFavorite = favoriteService.toggleFavorite(nguoiDung, sanPhamId);
            
            response.put("success", true);
            response.put("isFavorite", isFavorite);
            response.put("message", isFavorite ? "Đã thêm vào yêu thích" : "Đã bỏ yêu thích");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public String viewFavorites(Authentication authentication, org.springframework.ui.Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/dang-nhap";
        }

        NguoiDung nguoiDung = AuthHelper.getCurrentUser(authentication);
        var favorites = favoriteService.getUserFavorites(nguoiDung);
        long favoriteCount = favoriteService.getFavoriteCount(nguoiDung);

        model.addAttribute("currentUser", authentication);
        model.addAttribute("user", nguoiDung);
        model.addAttribute("favorites", favorites);
        model.addAttribute("favoriteCount", favoriteCount);

        return "favorites";
    }

    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFavorite(@RequestParam Long sanPhamId,
                                                             Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập");
            return ResponseEntity.ok(response);
        }

        try {
            NguoiDung nguoiDung = AuthHelper.getCurrentUser(authentication);
            favoriteService.removeFavorite(nguoiDung, sanPhamId);
            
            response.put("success", true);
            response.put("message", "Đã bỏ yêu thích");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}
