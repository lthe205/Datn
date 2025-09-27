package com.example.datn.banner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BannerService {
    
    @Autowired
    private BannerRepository bannerRepository;
    
    private final String uploadDir = "uploads/banners";
    
    // Lấy tất cả banner
    public List<Banner> getAllBanners() {
        return bannerRepository.findAll();
    }
    
    // Lấy banner theo vị trí
    public List<Banner> getBannersByPosition(String viTri) {
        return bannerRepository.findByViTriOrderByThuTuAsc(viTri);
    }
    
    // Lấy banner hoạt động theo vị trí
    public List<Banner> getActiveBannersByPosition(String viTri) {
        return bannerRepository.findByViTriAndHoatDongOrderByThuTuAsc(viTri, true);
    }
    
    // Lấy banner theo ID
    public Optional<Banner> getBannerById(Long id) {
        return bannerRepository.findById(id);
    }
    
    // Tạo banner mới
    public Banner createBanner(Banner banner, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String fileName = saveBannerImage(file);
            banner.setHinhAnh("/uploads/banners/" + fileName);
        }
        
        // Tự động set thứ tự nếu chưa có
        if (banner.getThuTu() == null) {
            long count = bannerRepository.countByViTri(banner.getViTri());
            banner.setThuTu((int) count + 1);
        }
        
        return bannerRepository.save(banner);
    }
    
    // Cập nhật banner
    public Banner updateBanner(Long id, Banner bannerDetails, MultipartFile file) throws IOException {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (optionalBanner.isPresent()) {
            Banner banner = optionalBanner.get();
            
            banner.setTen(bannerDetails.getTen());
            banner.setMoTa(bannerDetails.getMoTa());
            banner.setLink(bannerDetails.getLink());
            banner.setViTri(bannerDetails.getViTri());
            banner.setThuTu(bannerDetails.getThuTu());
            banner.setHoatDong(bannerDetails.getHoatDong());
            
            if (file != null && !file.isEmpty()) {
                String fileName = saveBannerImage(file);
                banner.setHinhAnh("/uploads/banners/" + fileName);
            }
            
            return bannerRepository.save(banner);
        }
        return null;
    }
    
    // Xóa banner
    public boolean deleteBanner(Long id) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (optionalBanner.isPresent()) {
            bannerRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Toggle trạng thái hoạt động
    public Banner toggleBannerStatus(Long id) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (optionalBanner.isPresent()) {
            Banner banner = optionalBanner.get();
            banner.setHoatDong(!banner.getHoatDong());
            return bannerRepository.save(banner);
        }
        return null;
    }
    
    // Tìm kiếm banner
    public List<Banner> searchBanners(String keyword) {
        return bannerRepository.findByTenContainingIgnoreCase(keyword);
    }
    
    // Lưu file ảnh banner
    private String saveBannerImage(MultipartFile file) throws IOException {
        // Tạo thư mục nếu chưa có
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Tạo tên file unique
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Tên file không hợp lệ");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;
        
        // Lưu file
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        
        return fileName;
    }
}
