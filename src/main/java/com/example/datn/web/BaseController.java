package com.example.datn.web;

import com.example.datn.auth.AuthHelper;
import com.example.datn.cart.CartService;
import com.example.datn.product.DanhMucRepository;
import com.example.datn.product.ThuongHieuRepository;
import com.example.datn.sport.DanhMucMonTheThaoRepository;
import com.example.datn.user.NguoiDung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.List;

public abstract class BaseController {
    
    @Autowired
    protected DanhMucRepository danhMucRepository;
    
    @Autowired
    protected ThuongHieuRepository thuongHieuRepository;
    
    @Autowired
    protected DanhMucMonTheThaoRepository danhMucMonTheThaoRepository;
    
    @Autowired
    protected CartService cartService;
    
    /**
     * Thêm dữ liệu navigation vào model
     */
    protected void addNavigationDataToModel(Model model) {
        // Lấy danh mục cha cho navigation
        List<com.example.datn.product.DanhMuc> danhMucCha = danhMucRepository.findDanhMucCha();
        model.addAttribute("danhMucCha", danhMucCha);
        
        // Lấy thương hiệu cho navigation
        List<com.example.datn.product.ThuongHieu> thuongHieu = thuongHieuRepository.findAll();
        model.addAttribute("thuongHieu", thuongHieu);
        
        // Lấy danh mục môn thể thao cho navigation
        List<com.example.datn.sport.DanhMucMonTheThao> danhMucMonTheThao = danhMucMonTheThaoRepository.findAllActiveOrderByThuTu();
        model.addAttribute("danhMucMonTheThao", danhMucMonTheThao);
    }
    
    /**
     * Thêm thông tin giỏ hàng vào model
     */
    protected void addCartInfoToModel(Model model, Authentication authentication) {
        NguoiDung nguoiDung = AuthHelper.getCurrentUser(authentication);
        if (nguoiDung != null) {
            try {
                int cartItemCount = cartService.getCartItemCount(nguoiDung);
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
