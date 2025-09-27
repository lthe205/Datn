package com.example.datn.web;

import com.example.datn.auth.AuthHelper;
import com.example.datn.user.NguoiDung;
import com.example.datn.favorite.FavoriteService;
import com.example.datn.product.SanPham;
import com.example.datn.product.DanhMuc;
import com.example.datn.product.ThuongHieu;
import com.example.datn.sport.DanhMucMonTheThao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController extends BaseController {

    @Autowired
    private HomeService homeService;

    @Autowired
    private FavoriteService favoriteService;
    
    @Autowired
    @Qualifier("webBannerService")
    private BannerService bannerService;
    
    @Autowired
    private com.example.datn.sport.DanhMucMonTheThaoRepository danhMucMonTheThaoRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Lấy authentication từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Truyền authentication vào model để template có thể sử dụng
        model.addAttribute("currentUser", authentication);
        
        // Thêm thông tin user nếu đã đăng nhập
        addUserInfoToModel(model, authentication);
        
        // Thêm thông tin giỏ hàng nếu user đã đăng nhập
        addCartInfoToModel(model, authentication);
        
        
        // Lấy banner chính
        model.addAttribute("mainBanners", bannerService.getMainBanners());
        
        // Lấy banner header
        model.addAttribute("headerBanners", bannerService.getHeaderBanners());
        
        // Lấy banner sidebar
        model.addAttribute("sidebarBanners", bannerService.getSidebarBanners());

        // Lấy danh mục cha cho navigation
        List<DanhMuc> danhMucCha = homeService.getDanhMucCha();
        model.addAttribute("danhMucCha", danhMucCha);
        
        // Lấy thương hiệu cho navigation
        List<ThuongHieu> thuongHieu = homeService.getAllThuongHieu();
        model.addAttribute("thuongHieu", thuongHieu);
        
        // Lấy danh mục môn thể thao cho navigation
        List<DanhMucMonTheThao> danhMucMonTheThao = danhMucMonTheThaoRepository.findAllActiveOrderByThuTu();
        model.addAttribute("danhMucMonTheThao", danhMucMonTheThao);

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
        NguoiDung nguoiDung = AuthHelper.getCurrentUser(authentication);
        if (nguoiDung != null) {
            try {
                isFavorite = favoriteService.isFavorite(nguoiDung, id);
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

    @GetMapping("/lien-he")
    public String lienHe(Model model) {
        // Truyền authentication vào model
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("currentUser", authentication);
        addUserInfoToModel(model, authentication);
        addCartInfoToModel(model, authentication);
        
        // Thêm dữ liệu cho dropdown navigation
        List<DanhMuc> danhMucCha = homeService.getDanhMucCha();
        model.addAttribute("danhMucCha", danhMucCha);
        
        List<ThuongHieu> thuongHieu = homeService.getAllThuongHieu();
        model.addAttribute("thuongHieu", thuongHieu);
        
        return "contact";
    }
    
    @GetMapping("/mon-the-thao/{id}")
    public String monTheThao(@PathVariable Long id,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "12") int size,
                            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("currentUser", authentication);
        addCartInfoToModel(model, authentication);
        
        // Thêm dữ liệu navigation
        List<DanhMuc> danhMucCha = homeService.getDanhMucCha();
        model.addAttribute("danhMucCha", danhMucCha);
        
        List<ThuongHieu> thuongHieu = homeService.getAllThuongHieu();
        model.addAttribute("thuongHieu", thuongHieu);
        
        List<DanhMucMonTheThao> danhMucMonTheThao = danhMucMonTheThaoRepository.findAllActiveOrderByThuTu();
        model.addAttribute("danhMucMonTheThao", danhMucMonTheThao);
        
        // Lấy thông tin môn thể thao
        com.example.datn.sport.DanhMucMonTheThao monTheThao = danhMucMonTheThaoRepository.findById(id).orElse(null);
        if (monTheThao == null) {
            return "error/404";
        }
        
        // Lấy sản phẩm theo môn thể thao (có thể cần tạo method mới trong HomeService)
        // Tạm thời lấy tất cả sản phẩm
        Page<SanPham> sanPhamTheoMonTheThao = homeService.getSanPhamMoiNhat(page, size);
        
        model.addAttribute("monTheThao", monTheThao);
        model.addAttribute("sanPhamTheoMonTheThao", sanPhamTheoMonTheThao);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sanPhamTheoMonTheThao.getTotalPages());
        model.addAttribute("id", id);
        
        return "sport";
    }

    // Helper method để thêm thông tin user vào model
    private void addUserInfoToModel(Model model, Authentication authentication) {
        NguoiDung nguoiDung = AuthHelper.getCurrentUser(authentication);
        if (nguoiDung != null) {
            model.addAttribute("user", nguoiDung);
        }
    }


} 