package com.example.datn.cart;

import com.example.datn.auth.AuthHelper;
import com.example.datn.product.DanhMucRepository;
import com.example.datn.product.ThuongHieuRepository;
import com.example.datn.user.NguoiDung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/gio-hang")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    @GetMapping
    public String viewCart(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/dang-nhap";
        }

        model.addAttribute("currentUser", authentication);
        
        // Thêm thông tin user nếu đã đăng nhập
        NguoiDung nguoiDung = AuthHelper.getCurrentUser(authentication);
        if (nguoiDung != null) {
            model.addAttribute("user", nguoiDung);
        }
        
        // Thêm dữ liệu cho dropdown navigation
        model.addAttribute("danhMucCha", danhMucRepository.findDanhMucCha());
        model.addAttribute("thuongHieu", thuongHieuRepository.findAll());
        List<ChiTietGioHang> cartItems = cartService.getCartItems(nguoiDung);
        BigDecimal cartTotal = cartService.getCartTotal(nguoiDung);
        int cartItemCount = cartService.getCartItemCount(nguoiDung);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("cartItemCount", cartItemCount);

        return "cart";
    }

    @PostMapping("/them")
    public String addToCart(@RequestParam Long sanPhamId,
                           @RequestParam(defaultValue = "1") Integer soLuong,
                           @RequestParam(required = false) String kichCo,
                           @RequestParam(required = false) String mauSac,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/dang-nhap";
        }

        try {
            NguoiDung nguoiDung = AuthHelper.getCurrentUser(authentication);
            cartService.addToCart(nguoiDung, sanPhamId, soLuong, kichCo, mauSac);
            redirectAttributes.addFlashAttribute("success", "Đã thêm sản phẩm vào giỏ hàng");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/san-pham/" + sanPhamId;
    }

    @PostMapping("/cap-nhat")
    public String updateCartItem(@RequestParam Long sanPhamId,
                                @RequestParam Integer soLuong,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/dang-nhap";
        }

        try {
            NguoiDung nguoiDung = AuthHelper.getCurrentUser(authentication);
            cartService.updateCartItem(nguoiDung, sanPhamId, soLuong);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật giỏ hàng");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/gio-hang";
    }

    @PostMapping("/xoa")
    public String removeFromCart(@RequestParam Long sanPhamId,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/dang-nhap";
        }

        try {
            NguoiDung nguoiDung = AuthHelper.getCurrentUser(authentication);
            cartService.removeFromCart(nguoiDung, sanPhamId);
            redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm khỏi giỏ hàng");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/gio-hang";
    }

    @PostMapping("/xoa-tat-ca")
    public String clearCart(Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/dang-nhap";
        }

        try {
            NguoiDung nguoiDung = AuthHelper.getCurrentUser(authentication);
            cartService.clearCart(nguoiDung);
            redirectAttributes.addFlashAttribute("success", "Đã xóa tất cả sản phẩm khỏi giỏ hàng");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/gio-hang";
    }

    @PostMapping("/mua-ngay")
    public String buyNow(@RequestParam Long sanPhamId,
                        @RequestParam(defaultValue = "1") Integer soLuong,
                        Authentication authentication,
                        RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/dang-nhap";
        }

        try {
            NguoiDung nguoiDung = AuthHelper.getCurrentUser(authentication);
            cartService.clearCart(nguoiDung); // Xóa giỏ hàng cũ
            cartService.addToCart(nguoiDung, sanPhamId, soLuong);
            redirectAttributes.addFlashAttribute("success", "Đã thêm sản phẩm vào giỏ hàng. Vui lòng thanh toán.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/gio-hang";
    }
}
