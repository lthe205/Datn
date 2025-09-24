package com.example.datn.web;

import com.example.datn.auth.CustomUserDetails;
import com.example.datn.cart.CartService;
import com.example.datn.favorite.FavoriteService;
import com.example.datn.product.SanPham;
import com.example.datn.product.DanhMuc;
import com.example.datn.product.ThuongHieu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;

    @Autowired
    private CartService cartService;

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/")
    public String home(Model model) {
        // Lấy authentication từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Truyền authentication vào model để template có thể sử dụng
        model.addAttribute("currentUser", authentication);
        
        // Thêm thông tin giỏ hàng nếu user đã đăng nhập
        addCartInfoToModel(model, authentication);
        
        // Lấy sản phẩm nổi bật
        List<SanPham> sanPhamNoiBat = homeService.getSanPhamNoiBat();
        model.addAttribute("sanPhamNoiBat", sanPhamNoiBat);

        // Lấy sản phẩm mới nhất (8 sản phẩm đầu tiên)
        Page<SanPham> sanPhamMoiNhat = homeService.getSanPhamMoiNhat(0, 8);
        model.addAttribute("sanPhamMoiNhat", sanPhamMoiNhat.getContent());

        // Lấy sản phẩm bán chạy nhất (8 sản phẩm đầu tiên)
        Page<SanPham> sanPhamBanChayNhat = homeService.getSanPhamBanChayNhat(0, 8);
        model.addAttribute("sanPhamBanChayNhat", sanPhamBanChayNhat.getContent());

        // Lấy sản phẩm khuyến mãi (8 sản phẩm đầu tiên)
        Page<SanPham> sanPhamKhuyenMai = homeService.getSanPhamKhuyenMai(0, 8);
        model.addAttribute("sanPhamKhuyenMai", sanPhamKhuyenMai.getContent());

        // Lấy danh mục cha
        List<DanhMuc> danhMucCha = homeService.getDanhMucCha();
        model.addAttribute("danhMucCha", danhMucCha);

        // Lấy thương hiệu
        List<ThuongHieu> thuongHieu = homeService.getAllThuongHieu();
        model.addAttribute("thuongHieu", thuongHieu);

        // Lấy thống kê
        HomeService.HomeStats stats = homeService.getHomeStats();
        model.addAttribute("stats", stats);

        return "index";
    }

    @GetMapping("/tim-kiem")
    public String timKiem(@RequestParam(value = "q", required = false) String keyword,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "12") int size,
                         Model model) {
        // Truyền authentication vào model
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("currentUser", authentication);
        addCartInfoToModel(model, authentication);
        
        // Thêm dữ liệu cho dropdown navigation
        List<DanhMuc> danhMucCha = homeService.getDanhMucCha();
        model.addAttribute("danhMucCha", danhMucCha);
        
        List<ThuongHieu> thuongHieu = homeService.getAllThuongHieu();
        model.addAttribute("thuongHieu", thuongHieu);
        
        Page<SanPham> ketQuaTimKiem = homeService.timKiemSanPham(keyword, page, size);
        
        model.addAttribute("ketQuaTimKiem", ketQuaTimKiem);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ketQuaTimKiem.getTotalPages());
        
        return "search";
    }

    @GetMapping("/danh-muc/{id}")
    public String danhMuc(@PathVariable Long id,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "12") int size,
                         Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("currentUser", authentication);
        addCartInfoToModel(model, authentication);
        
        // Thêm dữ liệu cho dropdown navigation
        List<DanhMuc> danhMucCha = homeService.getDanhMucCha();
        model.addAttribute("danhMucCha", danhMucCha);
        
        List<ThuongHieu> thuongHieu = homeService.getAllThuongHieu();
        model.addAttribute("thuongHieu", thuongHieu);
        
        Page<SanPham> sanPhamTheoDanhMuc = homeService.locTheoDanhMuc(id, page, size);
        
        model.addAttribute("sanPhamTheoDanhMuc", sanPhamTheoDanhMuc);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sanPhamTheoDanhMuc.getTotalPages());
        model.addAttribute("id", id);
        
        return "category";
    }

    @GetMapping("/thuong-hieu/{id}")
    public String thuongHieu(@PathVariable Long id,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "12") int size,
                            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("currentUser", authentication);
        addCartInfoToModel(model, authentication);
        
        // Thêm dữ liệu cho dropdown navigation
        List<DanhMuc> danhMucCha = homeService.getDanhMucCha();
        model.addAttribute("danhMucCha", danhMucCha);
        
        List<ThuongHieu> thuongHieu = homeService.getAllThuongHieu();
        model.addAttribute("thuongHieu", thuongHieu);
        
        Page<SanPham> sanPhamTheoThuongHieu = homeService.locTheoThuongHieu(id, page, size);
        
        model.addAttribute("sanPhamTheoThuongHieu", sanPhamTheoThuongHieu);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sanPhamTheoThuongHieu.getTotalPages());
        model.addAttribute("id", id);
        
        return "brand";
    }

    @GetMapping("/san-pham/{id}")
    public String chiTietSanPham(@PathVariable Long id, Model model) {
        // Truyền authentication vào model
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("currentUser", authentication);
        addCartInfoToModel(model, authentication);
        
        // Thêm dữ liệu cho dropdown navigation
        List<DanhMuc> danhMucCha = homeService.getDanhMucCha();
        model.addAttribute("danhMucCha", danhMucCha);
        
        List<ThuongHieu> thuongHieu = homeService.getAllThuongHieu();
        model.addAttribute("thuongHieu", thuongHieu);
        
        SanPham sanPham = homeService.getChiTietSanPham(id);
        if (sanPham == null) {
            return "error/404";
        }

        // Tăng lượt xem
        homeService.tangLuotXem(id);

        // Lấy ảnh sản phẩm
        List<com.example.datn.product.AnhSanPham> anhSanPham = homeService.getAnhSanPham(id);
        
        // Lấy sản phẩm liên quan
        List<SanPham> sanPhamLienQuan = homeService.getSanPhamLienQuan(id, sanPham.getDanhMuc().getId(), 4);

        // Lấy biến thể sản phẩm
        List<com.example.datn.product.BienTheSanPham> bienTheSanPham = homeService.getBienTheSanPham(id);
        
        // Lấy danh sách kích cỡ và màu sắc có sẵn
        List<String> availableSizes = homeService.getAvailableSizes(id);
        List<String> availableColors = homeService.getAvailableColors(id);
        
        // Tạo map màu sắc
        Map<String, String> colorMap = homeService.getColorMap();

        // Kiểm tra trạng thái yêu thích
        boolean isFavorite = false;
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            try {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                isFavorite = favoriteService.isFavorite(userDetails.getDomainUser(), id);
            } catch (Exception e) {
                // Nếu có lỗi, giữ isFavorite = false
            }
        }

        model.addAttribute("sanPham", sanPham);
        model.addAttribute("anhSanPham", anhSanPham);
        model.addAttribute("sanPhamLienQuan", sanPhamLienQuan);
        model.addAttribute("bienTheSanPham", bienTheSanPham);
        model.addAttribute("availableSizes", availableSizes);
        model.addAttribute("availableColors", availableColors);
        model.addAttribute("colorMap", colorMap);
        model.addAttribute("isFavorite", isFavorite);

        return "product-detail";
    }

    @GetMapping("/api/san-pham/{id}/anh")
    @ResponseBody
    public List<com.example.datn.product.AnhSanPham> getAnhSanPham(@PathVariable Long id) {
        return homeService.getAnhSanPham(id);
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