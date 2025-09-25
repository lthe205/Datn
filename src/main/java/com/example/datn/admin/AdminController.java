package com.example.datn.admin;

import com.example.datn.product.*;
import com.example.datn.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;
    private final SanPhamRepository sanPhamRepository;
    private final DanhMucRepository danhMucRepository;
    private final ThuongHieuRepository thuongHieuRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;

    public AdminController(AdminService adminService, NguoiDungRepository nguoiDungRepository, 
                          VaiTroRepository vaiTroRepository, SanPhamRepository sanPhamRepository,
                          DanhMucRepository danhMucRepository, ThuongHieuRepository thuongHieuRepository,
                          BienTheSanPhamRepository bienTheSanPhamRepository) {
        this.adminService = adminService;
        this.nguoiDungRepository = nguoiDungRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.danhMucRepository = danhMucRepository;
        this.thuongHieuRepository = thuongHieuRepository;
        this.bienTheSanPhamRepository = bienTheSanPhamRepository;
    }

    // Check if user is admin
    private boolean isAdmin(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_Admin"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        AdminStats stats = adminService.getAdminStats();
        model.addAttribute("stats", stats);
        model.addAttribute("recentUsers", adminService.getRecentUsers(5));
        model.addAttribute("topProducts", adminService.getTopProducts(5));
        
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model, Authentication auth,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String search) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<NguoiDung> users;
        
        if (search != null && !search.trim().isEmpty()) {
            users = nguoiDungRepository.findByTenContainingOrEmailContaining(search, search, pageable);
        } else {
            users = nguoiDungRepository.findAll(pageable);
        }

        model.addAttribute("users", users);
        model.addAttribute("roles", vaiTroRepository.findAll());
        model.addAttribute("search", search);
        
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttributes) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        try {
            NguoiDung user = nguoiDungRepository.findById(id).orElse(null);
            if (user != null) {
                user.setHoatDong(!user.isHoatDong());
                user.setNgayCapNhat(LocalDateTime.now());
                nguoiDungRepository.save(user);
                
                String status = user.isHoatDong() ? "kích hoạt" : "vô hiệu hóa";
                redirectAttributes.addFlashAttribute("success", "Đã " + status + " tài khoản: " + user.getEmail());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/change-role")
    public String changeUserRole(@PathVariable Long id, @RequestParam Long roleId, 
                                Authentication auth, RedirectAttributes redirectAttributes) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        try {
            NguoiDung user = nguoiDungRepository.findById(id).orElse(null);
            VaiTro role = vaiTroRepository.findById(roleId).orElse(null);
            
            if (user != null && role != null) {
                user.setVaiTro(role);
                user.setNgayCapNhat(LocalDateTime.now());
                nguoiDungRepository.save(user);
                
                redirectAttributes.addFlashAttribute("success", 
                    "Đã thay đổi vai trò của " + user.getEmail() + " thành " + role.getTenVaiTro());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/products")
    public String manageProducts(Model model, Authentication auth,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String search) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<SanPham> products;
        
        if (search != null && !search.trim().isEmpty()) {
            products = sanPhamRepository.findByTenContainingOrMaSanPhamContaining(search, search, pageable);
        } else {
            products = sanPhamRepository.findAll(pageable);
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", danhMucRepository.findAll());
        model.addAttribute("brands", thuongHieuRepository.findAll());
        model.addAttribute("search", search);
        model.addAttribute("newProduct", new SanPham());
        
        return "admin/products";
    }

    @PostMapping("/products/{id}/toggle-status")
    public String toggleProductStatus(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttributes) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        try {
            SanPham product = sanPhamRepository.findById(id).orElse(null);
            if (product != null) {
                product.setHoatDong(!product.getHoatDong());
                product.setNgayCapNhat(LocalDateTime.now());
                sanPhamRepository.save(product);
                
                String status = product.getHoatDong() ? "kích hoạt" : "ẩn";
                redirectAttributes.addFlashAttribute("success", "Đã " + status + " sản phẩm: " + product.getTen());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/toggle-featured")
    public String toggleProductFeatured(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttributes) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        try {
            SanPham product = sanPhamRepository.findById(id).orElse(null);
            if (product != null) {
                product.setNoiBat(!product.getNoiBat());
                product.setNgayCapNhat(LocalDateTime.now());
                sanPhamRepository.save(product);
                
                String status = product.getNoiBat() ? "đánh dấu nổi bật" : "bỏ đánh dấu nổi bật";
                redirectAttributes.addFlashAttribute("success", "Đã " + status + " sản phẩm: " + product.getTen());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // Thêm sản phẩm mới
    @PostMapping("/products")
    public String addProduct(@ModelAttribute SanPham product, 
                            @RequestParam(required = false) Long danhMucId,
                            @RequestParam(required = false) Long thuongHieuId,
                            Authentication auth, RedirectAttributes redirectAttributes) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        try {
            // Set danh mục và thương hiệu
            if (danhMucId != null) {
                DanhMuc danhMuc = danhMucRepository.findById(danhMucId).orElse(null);
                product.setDanhMuc(danhMuc);
            }
            if (thuongHieuId != null) {
                ThuongHieu thuongHieu = thuongHieuRepository.findById(thuongHieuId).orElse(null);
                product.setThuongHieu(thuongHieu);
            }

            // Set các giá trị mặc định
            if (product.getSoLuongTon() == null) product.setSoLuongTon(0);
            if (product.getLuotXem() == null) product.setLuotXem(0);
            if (product.getDaBan() == null) product.setDaBan(0);
            if (product.getHoatDong() == null) product.setHoatDong(true);
            if (product.getNoiBat() == null) product.setNoiBat(false);
            
            product.setNgayTao(LocalDateTime.now());
            product.setNgayCapNhat(LocalDateTime.now());

            sanPhamRepository.save(product);
            redirectAttributes.addFlashAttribute("success", "Đã thêm sản phẩm: " + product.getTen());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // Sửa sản phẩm - GET
    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model, Authentication auth) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        SanPham product = sanPhamRepository.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/admin/products";
        }

        model.addAttribute("product", product);
        model.addAttribute("categories", danhMucRepository.findAll());
        model.addAttribute("brands", thuongHieuRepository.findAll());
        
        return "admin/product-edit";
    }

    // Cập nhật sản phẩm - POST
    @PostMapping("/products/{id}/update")
    public String updateProduct(@PathVariable Long id, 
                               @ModelAttribute SanPham product,
                               @RequestParam(required = false) Long danhMucId,
                               @RequestParam(required = false) Long thuongHieuId,
                               Authentication auth, RedirectAttributes redirectAttributes) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        try {
            SanPham existingProduct = sanPhamRepository.findById(id).orElse(null);
            if (existingProduct == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm");
                return "redirect:/admin/products";
            }

            // Cập nhật thông tin sản phẩm
            existingProduct.setTen(product.getTen());
            existingProduct.setMoTa(product.getMoTa());
            existingProduct.setGia(product.getGia());
            existingProduct.setGiaGoc(product.getGiaGoc());
            existingProduct.setAnhChinh(product.getAnhChinh());
            existingProduct.setSoLuongTon(product.getSoLuongTon());
            existingProduct.setChatLieu(product.getChatLieu());
            existingProduct.setXuatXu(product.getXuatXu());
            existingProduct.setHoatDong(product.getHoatDong());
            existingProduct.setNoiBat(product.getNoiBat());
            existingProduct.setNgayCapNhat(LocalDateTime.now());

            // Set danh mục và thương hiệu
            if (danhMucId != null) {
                DanhMuc danhMuc = danhMucRepository.findById(danhMucId).orElse(null);
                existingProduct.setDanhMuc(danhMuc);
            }
            if (thuongHieuId != null) {
                ThuongHieu thuongHieu = thuongHieuRepository.findById(thuongHieuId).orElse(null);
                existingProduct.setThuongHieu(thuongHieu);
            }

            sanPhamRepository.save(existingProduct);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật sản phẩm: " + existingProduct.getTen());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // Xóa sản phẩm
    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttributes) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        try {
            SanPham product = sanPhamRepository.findById(id).orElse(null);
            if (product != null) {
                String productName = product.getTen();
                sanPhamRepository.delete(product);
                redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm: " + productName);
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/categories")
    public String manageCategories(Model model, Authentication auth) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        model.addAttribute("categories", danhMucRepository.findAll());
        model.addAttribute("newCategory", new DanhMuc());
        
        return "admin/categories";
    }

    @PostMapping("/categories")
    public String addCategory(@ModelAttribute DanhMuc category, Authentication auth, RedirectAttributes redirectAttributes) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        try {
            category.setNgayTao(LocalDateTime.now());
            danhMucRepository.save(category);
            redirectAttributes.addFlashAttribute("success", "Đã thêm danh mục: " + category.getTen());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    @GetMapping("/brands")
    public String manageBrands(Model model, Authentication auth) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        model.addAttribute("brands", thuongHieuRepository.findAll());
        model.addAttribute("newBrand", new ThuongHieu());
        
        return "admin/brands";
    }

    @PostMapping("/brands")
    public String addBrand(@ModelAttribute ThuongHieu brand, Authentication auth, RedirectAttributes redirectAttributes) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        try {
            brand.setNgayTao(LocalDateTime.now());
            thuongHieuRepository.save(brand);
            redirectAttributes.addFlashAttribute("success", "Đã thêm thương hiệu: " + brand.getTen());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/brands";
    }

    // Quản lý biến thể sản phẩm
    @GetMapping("/products/{id}/variants")
    public String manageVariants(@PathVariable Long id, Model model, Authentication auth) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        SanPham product = sanPhamRepository.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/admin/products";
        }

        List<BienTheSanPham> variants = bienTheSanPhamRepository.findBySanPhamIdAndTrangThaiTrue(id);
        
        // Tạo map màu sắc
        Map<String, String> colorMap = new java.util.HashMap<>();
        colorMap.put("Đen", "#000000");
        colorMap.put("Trắng", "#FFFFFF");
        colorMap.put("Xám", "#808080");
        colorMap.put("Xanh", "#0000FF");
        colorMap.put("Đỏ", "#FF0000");
        colorMap.put("Vàng", "#FFFF00");
        colorMap.put("Xanh lá", "#00FF00");
        colorMap.put("Hồng", "#FFC0CB");
        colorMap.put("Tím", "#800080");
        colorMap.put("Cam", "#FFA500");
        colorMap.put("Nâu", "#A52A2A");
        colorMap.put("Be", "#F5F5DC");

        model.addAttribute("product", product);
        model.addAttribute("variants", variants);
        model.addAttribute("colorMap", colorMap);
        
        return "admin/product-variants";
    }

    @PostMapping("/products/{id}/variants/add")
    public String addVariant(@PathVariable Long id,
                            @RequestParam String kichCo,
                            @RequestParam String mauSac,
                            @RequestParam Integer soLuong,
                            @RequestParam BigDecimal giaBan,
                            @RequestParam(required = false) BigDecimal giaKhuyenMai,
                            @RequestParam(defaultValue = "true") boolean trangThai,
                            Authentication auth, RedirectAttributes redirectAttributes) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        try {
            SanPham product = sanPhamRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            BienTheSanPham variant = new BienTheSanPham();
            variant.setSanPham(product);
            variant.setKichCo(kichCo);
            variant.setMauSac(mauSac);
            variant.setSoLuong(soLuong);
            variant.setGiaBan(giaBan);
            variant.setGiaKhuyenMai(giaKhuyenMai);
            variant.setTrangThai(trangThai);
            variant.setNgayTao(LocalDateTime.now());

            bienTheSanPhamRepository.save(variant);
            redirectAttributes.addFlashAttribute("success", "Đã thêm biến thể: " + kichCo + " - " + mauSac);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/products/" + id + "/variants";
    }

    @PostMapping("/products/{productId}/variants/{id}/toggle")
    public String toggleVariantStatus(@PathVariable Long productId, @PathVariable Long id, 
                                    Authentication auth, RedirectAttributes redirectAttributes) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        try {
            BienTheSanPham variant = bienTheSanPhamRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Biến thể không tồn tại"));
            
            variant.setTrangThai(!variant.getTrangThai());
            bienTheSanPhamRepository.save(variant);
            
            String status = variant.getTrangThai() ? "hiển thị" : "ẩn";
            redirectAttributes.addFlashAttribute("success", "Đã " + status + " biến thể: " + variant.getKichCo() + " - " + variant.getMauSac());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/products/" + productId + "/variants";
    }

    @PostMapping("/products/{productId}/variants/{id}/delete")
    public String deleteVariant(@PathVariable Long productId, @PathVariable Long id, 
                               Authentication auth, RedirectAttributes redirectAttributes) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        try {
            BienTheSanPham variant = bienTheSanPhamRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Biến thể không tồn tại"));
            
            String variantInfo = variant.getKichCo() + " - " + variant.getMauSac();
            bienTheSanPhamRepository.delete(variant);
            redirectAttributes.addFlashAttribute("success", "Đã xóa biến thể: " + variantInfo);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/products/" + productId + "/variants";
    }

    // Chart data API endpoints
    @GetMapping("/api/chart/weekly")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getWeeklyChartData(Authentication auth) {
        if (!isAdmin(auth)) {
            return ResponseEntity.status(403).build();
        }

        List<Object[]> weeklyData = adminService.getWeeklyRevenue();
        Map<String, Object> response = new HashMap<>();
        
        List<String> labels = new java.util.ArrayList<>();
        List<BigDecimal> data = new java.util.ArrayList<>();
        
        for (Object[] row : weeklyData) {
            labels.add(row[0].toString());
            data.add((BigDecimal) row[1]);
        }
        
        response.put("labels", labels);
        response.put("data", data);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/chart/monthly")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMonthlyChartData(Authentication auth) {
        if (!isAdmin(auth)) {
            return ResponseEntity.status(403).build();
        }

        List<Object[]> monthlyData = adminService.getMonthlyRevenue();
        Map<String, Object> response = new HashMap<>();
        
        List<String> labels = new java.util.ArrayList<>();
        List<BigDecimal> data = new java.util.ArrayList<>();
        
        String[] monthNames = {"", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                              "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        
        for (Object[] row : monthlyData) {
            int month = (Integer) row[0];
            labels.add(monthNames[month]);
            data.add((BigDecimal) row[1]);
        }
        
        response.put("labels", labels);
        response.put("data", data);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/chart/yearly")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getYearlyChartData(Authentication auth) {
        if (!isAdmin(auth)) {
            return ResponseEntity.status(403).build();
        }

        List<Object[]> yearlyData = adminService.getYearlyRevenue();
        Map<String, Object> response = new HashMap<>();
        
        List<String> labels = new java.util.ArrayList<>();
        List<BigDecimal> data = new java.util.ArrayList<>();
        
        for (Object[] row : yearlyData) {
            labels.add("Năm " + row[0].toString());
            data.add((BigDecimal) row[1]);
        }
        
        response.put("labels", labels);
        response.put("data", data);
        
        return ResponseEntity.ok(response);
    }

    // DTO for statistics
    public static class AdminStats {
        private long totalUsers;
        private long totalProducts;
        private long totalCategories;
        private long totalBrands;
        private long activeUsers;
        private long activeProducts;
        private BigDecimal todayRevenue;
        private BigDecimal monthRevenue;

        // Constructors, getters and setters
        public AdminStats() {}

        public AdminStats(long totalUsers, long totalProducts, long totalCategories, long totalBrands, long activeUsers, long activeProducts) {
            this.totalUsers = totalUsers;
            this.totalProducts = totalProducts;
            this.totalCategories = totalCategories;
            this.totalBrands = totalBrands;
            this.activeUsers = activeUsers;
            this.activeProducts = activeProducts;
        }

        public AdminStats(long totalUsers, long totalProducts, long totalCategories, long totalBrands, long activeUsers, long activeProducts, BigDecimal todayRevenue, BigDecimal monthRevenue) {
            this.totalUsers = totalUsers;
            this.totalProducts = totalProducts;
            this.totalCategories = totalCategories;
            this.totalBrands = totalBrands;
            this.activeUsers = activeUsers;
            this.activeProducts = activeProducts;
            this.todayRevenue = todayRevenue;
            this.monthRevenue = monthRevenue;
        }

        // Getters and setters
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

        public long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }

        public long getTotalCategories() { return totalCategories; }
        public void setTotalCategories(long totalCategories) { this.totalCategories = totalCategories; }

        public long getTotalBrands() { return totalBrands; }
        public void setTotalBrands(long totalBrands) { this.totalBrands = totalBrands; }

        public long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }

        public long getActiveProducts() { return activeProducts; }
        public void setActiveProducts(long activeProducts) { this.activeProducts = activeProducts; }

        public BigDecimal getTodayRevenue() { return todayRevenue; }
        public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }

        public BigDecimal getMonthRevenue() { return monthRevenue; }
        public void setMonthRevenue(BigDecimal monthRevenue) { this.monthRevenue = monthRevenue; }
    }
}
