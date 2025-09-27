package com.example.datn.admin.sport;

import com.example.datn.sport.DanhMucMonTheThao;
import com.example.datn.sport.DanhMucMonTheThaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AdminSportService {
    
    @Autowired
    private DanhMucMonTheThaoRepository danhMucMonTheThaoRepository;
    
    private static final String UPLOAD_DIR = "uploads/sports/";
    
    /**
     * Lấy tất cả danh mục môn thể thao với phân trang
     */
    public Page<DanhMucMonTheThao> getAllSports(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return danhMucMonTheThaoRepository.findAll(pageable);
    }
    
    /**
     * Lấy danh mục môn thể thao theo ID
     */
    public DanhMucMonTheThao getSportById(Long id) {
        return danhMucMonTheThaoRepository.findById(id).orElse(null);
    }
    
    /**
     * Lưu danh mục môn thể thao
     */
    public DanhMucMonTheThao saveSport(DanhMucMonTheThao sport) {
        if (sport.getId() == null) {
            // Tạo mới
            sport.setNgayTao(LocalDateTime.now());
            sport.setNgayCapNhat(LocalDateTime.now());
        } else {
            // Cập nhật - giữ nguyên ngayTao cũ
            DanhMucMonTheThao existingSport = danhMucMonTheThaoRepository.findById(sport.getId()).orElse(null);
            if (existingSport != null) {
                sport.setNgayTao(existingSport.getNgayTao()); // Giữ nguyên ngày tạo
            } else {
                sport.setNgayTao(LocalDateTime.now()); // Fallback nếu không tìm thấy
            }
            sport.setNgayCapNhat(LocalDateTime.now());
        }
        return danhMucMonTheThaoRepository.save(sport);
    }
    
    /**
     * Xóa danh mục môn thể thao
     */
    public void deleteSport(Long id) {
        danhMucMonTheThaoRepository.deleteById(id);
    }
    
    /**
     * Toggle trạng thái hoạt động
     */
    public void toggleSportStatus(Long id) {
        DanhMucMonTheThao sport = getSportById(id);
        if (sport != null) {
            sport.setHoatDong(!sport.getHoatDong());
            sport.setNgayCapNhat(LocalDateTime.now());
            danhMucMonTheThaoRepository.save(sport);
        }
    }
    
    /**
     * Lưu hình ảnh môn thể thao
     */
    public String saveSportImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }
        
        // Tạo thư mục nếu chưa tồn tại
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Tạo tên file unique
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Tên file không hợp lệ");
        }
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // Lưu file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return "/" + UPLOAD_DIR + uniqueFilename;
    }
    
    /**
     * Xóa hình ảnh
     */
    public void deleteSportImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Path filePath = Paths.get(imagePath.substring(1)); // Bỏ dấu / đầu
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log error but don't throw exception
                System.err.println("Error deleting image: " + e.getMessage());
            }
        }
    }
    
    /**
     * Lấy tất cả danh mục môn thể thao (không phân trang)
     */
    public List<DanhMucMonTheThao> getAllSportsList() {
        return danhMucMonTheThaoRepository.findAll();
    }
    
    /**
     * Lấy danh mục môn thể thao đang hoạt động
     */
    public List<DanhMucMonTheThao> getActiveSports() {
        return danhMucMonTheThaoRepository.findByHoatDong(true);
    }
    
    /**
     * Thống kê môn thể thao
     */
    public long getTotalSports() {
        return danhMucMonTheThaoRepository.count();
    }
    
    public long getActiveSportsCount() {
        return danhMucMonTheThaoRepository.findByHoatDong(true).size();
    }
    
    public long getInactiveSports() {
        return danhMucMonTheThaoRepository.findByHoatDong(false).size();
    }
    
    public long getNewSportsThisMonth() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return danhMucMonTheThaoRepository.findAll().stream()
            .filter(sport -> sport.getNgayTao().isAfter(oneMonthAgo))
            .count();
    }
}
