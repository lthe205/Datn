package com.example.datn.admin;

import com.example.datn.product.*;
import com.example.datn.user.*;
import com.example.datn.sport.DanhMucMonTheThaoRepository;
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
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private final DanhMucMonTheThaoRepository danhMucMonTheThaoRepository;
    private final AnhSanPhamRepository anhSanPhamRepository;

    public AdminController(AdminService adminService, NguoiDungRepository nguoiDungRepository, 
                          VaiTroRepository vaiTroRepository, SanPhamRepository sanPhamRepository,
                          DanhMucRepository danhMucRepository, ThuongHieuRepository thuongHieuRepository,
                          BienTheSanPhamRepository bienTheSanPhamRepository, DanhMucMonTheThaoRepository danhMucMonTheThaoRepository,
                          AnhSanPhamRepository anhSanPhamRepository) {
        this.adminService = adminService;
        this.nguoiDungRepository = nguoiDungRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.danhMucRepository = danhMucRepository;
        this.thuongHieuRepository = thuongHieuRepository;
        this.bienTheSanPhamRepository = bienTheSanPhamRepository;
        this.danhMucMonTheThaoRepository = danhMucMonTheThaoRepository;
        this.anhSanPhamRepository = anhSanPhamRepository;
    }

    // Check if user is admin
    private boolean isAdmin(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_Admin"));
    }

    // Tạo mã sản phẩm tự động
    private String generateNextProductCode() {
        // Tìm mã sản phẩm lớn nhất có định dạng SPxxx
        String maxCode = sanPhamRepository.findMaxProductCode();
        
        if (maxCode == null || maxCode.isEmpty()) {
            return "SP001";
        }
        
        // Trích xuất số từ mã hiện tại (ví dụ: SP012 -> 12)
        String numberPart = maxCode.substring(2); // Bỏ "SP"
        try {
            int nextNumber = Integer.parseInt(numberPart) + 1;
            return String.format("SP%03d", nextNumber); // SP001, SP002, SP003...
        } catch (NumberFormatException e) {
            // Nếu không parse được, tạo mã mới từ số lượng sản phẩm hiện tại
            long productCount = sanPhamRepository.count();
            return String.format("SP%03d", productCount + 1);
        }
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
        
        // Thêm thống kê môn thể thao
        long totalSports = danhMucMonTheThaoRepository.count();
        long activeSports = danhMucMonTheThaoRepository.findByHoatDong(true).size();
        model.addAttribute("totalSports", totalSports);
        model.addAttribute("activeSports", activeSports);
        
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model, Authentication auth,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String search,
                             @RequestParam(required = false) Long role,
                             @RequestParam(required = false) String status) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<NguoiDung> users;
        
        // Apply filters using the new query method
        users = nguoiDungRepository.findWithFilters(
            search != null && !search.trim().isEmpty() ? search : null,
            role,
            status,
            pageable
        );

        // Calculate statistics
        long activeUsers = nguoiDungRepository.countByHoatDong(true);
        long lockedUsers = nguoiDungRepository.countByBiKhoa(true);
        long newUsers = nguoiDungRepository.countByNgayTaoAfter(LocalDateTime.now().minusDays(1));

        model.addAttribute("users", users);
        model.addAttribute("roles", vaiTroRepository.findAll());
        model.addAttribute("search", search);
        model.addAttribute("selectedRole", role);
        model.addAttribute("selectedStatus", status);
        
        // Statistics
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("lockedUsers", lockedUsers);
        model.addAttribute("newUsers", newUsers);
        
        return "admin/users";
    }

    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Authentication auth, Model model) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        NguoiDung user = nguoiDungRepository.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/admin/users";
        }

        model.addAttribute("user", user);
        return "admin/user-detail";
    }

    @PostMapping("/users/{id}/toggle")
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
                redirectAttributes.addFlashAttribute("successMessage", "Đã " + status + " tài khoản: " + user.getEmail());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/users/export")
    public void exportUsers(@RequestParam(required = false) String search,
                           @RequestParam(required = false) Long role,
                           @RequestParam(required = false) String status,
                           HttpServletResponse response) throws IOException {
        // This is a placeholder for Excel export functionality
        // You would implement actual Excel export here
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=users.xlsx");
        
        // For now, just return a simple text response
        response.getWriter().write("Excel export functionality would be implemented here");
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
                                @RequestParam(required = false) String search,
                                @RequestParam(required = false) Long category,
                                @RequestParam(required = false) Long brand) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<SanPham> products;
        
        // Apply filters
        if (search != null && !search.trim().isEmpty()) {
            products = sanPhamRepository.findByTenContainingOrMaSanPhamContaining(search, search, pageable);
        } else if (category != null) {
            products = sanPhamRepository.findByDanhMucId(category, pageable);
        } else if (brand != null) {
            products = sanPhamRepository.findByThuongHieuId(brand, pageable);
        } else {
            products = sanPhamRepository.findAll(pageable);
        }

        // Calculate statistics
        long activeProducts = sanPhamRepository.countByHoatDong(true);
        long featuredProducts = sanPhamRepository.countByNoiBat(true);
        long lowStockProducts = sanPhamRepository.countBySoLuongTonLessThanEqual(10);

        model.addAttribute("products", products);
        model.addAttribute("categories", danhMucRepository.findAll());
        model.addAttribute("brands", thuongHieuRepository.findAll());
        model.addAttribute("sports", danhMucMonTheThaoRepository.findAll());
        model.addAttribute("search", search);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("newProduct", new SanPham());
        
        // Statistics
        model.addAttribute("activeProducts", activeProducts);
        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("lowStockProducts", lowStockProducts);
        
        return "admin/products";
    }

    @PostMapping("/products/{id}/toggle")
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
                redirectAttributes.addFlashAttribute("successMessage", "Đã " + status + " sản phẩm: " + product.getTen());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/products/export")
    public void exportProducts(@RequestParam(required = false) String search,
                              @RequestParam(required = false) Long category,
                              @RequestParam(required = false) Long brand,
                              HttpServletResponse response) throws IOException {
        // This is a placeholder for Excel export functionality
        // You would implement actual Excel export here
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=products.xlsx");
        
        // For now, just return a simple text response
        response.getWriter().write("Excel export functionality would be implemented here");
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
                            @RequestParam(required = false) Long monTheThaoId,
                            @RequestParam(required = false) MultipartFile[] productImages,
                            Authentication auth, RedirectAttributes redirectAttributes) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        try {
            // Tạo mã sản phẩm tự động nếu chưa có hoặc trùng
            if (product.getMaSanPham() == null || product.getMaSanPham().trim().isEmpty()) {
                String nextCode = generateNextProductCode();
                product.setMaSanPham(nextCode);
            } else {
                // Kiểm tra mã đã tồn tại chưa
                if (sanPhamRepository.existsByMaSanPham(product.getMaSanPham())) {
                    String nextCode = generateNextProductCode();
                    product.setMaSanPham(nextCode);
                }
            }

            // Set danh mục, thương hiệu và môn thể thao
            if (danhMucId != null) {
                DanhMuc danhMuc = danhMucRepository.findById(danhMucId).orElse(null);
                product.setDanhMuc(danhMuc);
            }
            if (thuongHieuId != null) {
                ThuongHieu thuongHieu = thuongHieuRepository.findById(thuongHieuId).orElse(null);
                product.setThuongHieu(thuongHieu);
            }
            if (monTheThaoId != null) {
                com.example.datn.sport.DanhMucMonTheThao danhMucMonTheThao = danhMucMonTheThaoRepository.findById(monTheThaoId).orElse(null);
                product.setDanhMucMonTheThao(danhMucMonTheThao);
            }

            // Set các giá trị mặc định
            if (product.getSoLuongTon() == null) product.setSoLuongTon(0);
            if (product.getLuotXem() == null) product.setLuotXem(0);
            if (product.getDaBan() == null) product.setDaBan(0);
            if (product.getHoatDong() == null) product.setHoatDong(true);
            if (product.getNoiBat() == null) product.setNoiBat(false);
            
            product.setNgayTao(LocalDateTime.now());
            product.setNgayCapNhat(LocalDateTime.now());

            SanPham savedProduct = sanPhamRepository.save(product);
            
            // Xử lý upload ảnh sản phẩm
            if (productImages != null && productImages.length > 0) {
                for (int i = 0; i < productImages.length; i++) {
                    MultipartFile file = productImages[i];
                    if (!file.isEmpty()) {
                        try {
                            // Tạo tên file unique
                            String originalFilename = file.getOriginalFilename();
                            if (originalFilename != null) {
                                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                                String fileName = "product_" + savedProduct.getId() + "_" + (i + 1) + fileExtension;
                                
                                // Lưu file (tạm thời lưu vào thư mục uploads)
                                String uploadDir = "uploads/products/";
                                java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir).toAbsolutePath().normalize();
                                if (!java.nio.file.Files.exists(uploadPath)) {
                                    java.nio.file.Files.createDirectories(uploadPath);
                                }
                                
                                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                                file.transferTo(filePath.toFile());
                                
                                // Lưu thông tin ảnh vào database
                                AnhSanPham anhSanPham = new AnhSanPham();
                                anhSanPham.setSanPham(savedProduct);
                                anhSanPham.setUrlAnh("/uploads/products/" + fileName);
                                anhSanPham.setThuTu(i + 1); // Ảnh đầu tiên là ảnh chính (thứ tự = 1)
                                anhSanPham.setNgayThem(LocalDateTime.now());
                                
                                anhSanPhamRepository.save(anhSanPham);
                            }
                        } catch (Exception e) {
                            // Log lỗi nhưng không dừng quá trình
                            System.err.println("Lỗi upload ảnh " + (i + 1) + ": " + e.getMessage());
                        }
                    }
                }
            }
            
            redirectAttributes.addFlashAttribute("success", "Đã thêm sản phẩm: " + product.getTen());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // Xóa ảnh sản phẩm
    @PostMapping("/products/images/{imageId}/delete")
    @ResponseBody
    public ResponseEntity<String> deleteProductImage(@PathVariable Long imageId, Authentication auth) {
        if (!isAdmin(auth)) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        try {
            AnhSanPham image = anhSanPhamRepository.findById(imageId).orElse(null);
            if (image == null) {
                return ResponseEntity.status(404).body("Image not found");
            }

            // Xóa file từ filesystem
            try {
                String imagePath = image.getUrlAnh().substring(1); // Bỏ dấu / đầu
                java.nio.file.Path filePath = java.nio.file.Paths.get(imagePath).toAbsolutePath().normalize();
                if (java.nio.file.Files.exists(filePath)) {
                    java.nio.file.Files.delete(filePath);
                }
            } catch (Exception e) {
                System.err.println("Lỗi xóa file: " + e.getMessage());
            }

            // Xóa record từ database
            anhSanPhamRepository.delete(image);
            
            return ResponseEntity.ok("Image deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting image: " + e.getMessage());
        }
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
        model.addAttribute("sports", danhMucMonTheThaoRepository.findAll());
        model.addAttribute("productImages", anhSanPhamRepository.findBySanPhamIdOrderByThuTuAsc(id));
        
        return "admin/product-edit";
    }

    // Cập nhật sản phẩm - POST
    @PostMapping("/products/{id}/update")
    public String updateProduct(@PathVariable Long id, 
                               @ModelAttribute SanPham product,
                               @RequestParam(required = false) Long danhMucId,
                               @RequestParam(required = false) Long thuongHieuId,
                               @RequestParam(required = false) Long monTheThaoId,
                               @RequestParam(required = false) MultipartFile[] additionalImages,
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

            // Set danh mục, thương hiệu và môn thể thao
            if (danhMucId != null) {
                DanhMuc danhMuc = danhMucRepository.findById(danhMucId).orElse(null);
                existingProduct.setDanhMuc(danhMuc);
            }
            if (thuongHieuId != null) {
                ThuongHieu thuongHieu = thuongHieuRepository.findById(thuongHieuId).orElse(null);
                existingProduct.setThuongHieu(thuongHieu);
            }
            if (monTheThaoId != null) {
                com.example.datn.sport.DanhMucMonTheThao danhMucMonTheThao = danhMucMonTheThaoRepository.findById(monTheThaoId).orElse(null);
                existingProduct.setDanhMucMonTheThao(danhMucMonTheThao);
            } else {
                existingProduct.setDanhMucMonTheThao(null);
            }

            sanPhamRepository.save(existingProduct);
            
            // Xử lý thêm ảnh mới
            if (additionalImages != null && additionalImages.length > 0) {
                // Lấy số lượng ảnh hiện tại để tính thứ tự tiếp theo
                Long currentImageCount = anhSanPhamRepository.countBySanPhamId(id);
                
                for (int i = 0; i < additionalImages.length; i++) {
                    MultipartFile file = additionalImages[i];
                    if (!file.isEmpty()) {
                        try {
                            // Tạo tên file unique
                            String originalFilename = file.getOriginalFilename();
                            if (originalFilename != null) {
                                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                                String fileName = "product_" + id + "_" + (currentImageCount + i + 1) + fileExtension;
                                
                                // Lưu file
                                String uploadDir = "uploads/products/";
                                java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir).toAbsolutePath().normalize();
                                if (!java.nio.file.Files.exists(uploadPath)) {
                                    java.nio.file.Files.createDirectories(uploadPath);
                                }
                                
                                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                                file.transferTo(filePath.toFile());
                                
                                // Lưu thông tin ảnh vào database
                                AnhSanPham anhSanPham = new AnhSanPham();
                                anhSanPham.setSanPham(existingProduct);
                                anhSanPham.setUrlAnh("/uploads/products/" + fileName);
                                anhSanPham.setThuTu((int)(currentImageCount + i + 1));
                                anhSanPham.setNgayThem(LocalDateTime.now());
                                
                                anhSanPhamRepository.save(anhSanPham);
                            }
                        } catch (Exception e) {
                            // Log lỗi nhưng không dừng quá trình
                            System.err.println("Lỗi upload ảnh " + (i + 1) + ": " + e.getMessage());
                        }
                    }
                }
            }
            
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

        List<DanhMuc> categories = danhMucRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("newCategory", new DanhMuc());
        
        // Thống kê danh mục
        long totalCategories = danhMucRepository.count();
        long activeCategories = totalCategories; // Tạm thời coi tất cả là active
        long parentCategories = danhMucRepository.findByDanhMucChaIsNull().size();
        long childCategories = totalCategories - parentCategories;
        
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("activeCategories", activeCategories);
        model.addAttribute("parentCategories", parentCategories);
        model.addAttribute("childCategories", childCategories);
        model.addAttribute("parentCategoriesList", danhMucRepository.findByDanhMucChaIsNull());
        
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
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm danh mục: " + category.getTen());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }


    @GetMapping("/brands")
    public String manageBrands(Model model, Authentication auth) {
        if (!isAdmin(auth)) {
            return "redirect:/dang-nhap";
        }

        List<ThuongHieu> brands = thuongHieuRepository.findAll();
        model.addAttribute("brands", brands);
        model.addAttribute("newBrand", new ThuongHieu());
        model.addAttribute("sanPhamRepository", sanPhamRepository);
        
        // Thống kê thương hiệu
        long totalBrands = thuongHieuRepository.count();
        long activeBrands = totalBrands; // Tạm thời coi tất cả là active
        long brandsWithProducts = brands.stream()
            .mapToLong(brand -> sanPhamRepository.countByThuongHieuId(brand.getId()))
            .filter(count -> count > 0)
            .count();
        long newBrandsThisMonth = brands.stream()
            .filter(brand -> brand.getNgayTao().isAfter(LocalDateTime.now().minusMonths(1)))
            .count();
        
        model.addAttribute("totalBrands", totalBrands);
        model.addAttribute("activeBrands", activeBrands);
        model.addAttribute("brandsWithProducts", brandsWithProducts);
        model.addAttribute("newBrandsThisMonth", newBrandsThisMonth);
        
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
