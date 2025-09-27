package com.example.datn.web;

import com.example.datn.banner.Banner;
import com.example.datn.banner.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("webBannerService")
public class BannerService {
    
    @Autowired
    private BannerRepository bannerRepository;
    
    // Lấy banner theo vị trí và hoạt động
    public List<Banner> getActiveBannersByPosition(String viTri) {
        return bannerRepository.findByViTriAndHoatDongOrderByThuTuAsc(viTri, true);
    }
    
    // Lấy tất cả banner hoạt động
    public List<Banner> getAllActiveBanners() {
        return bannerRepository.findByHoatDongOrderByThuTuAsc(true);
    }
    
    // Lấy banner chính (main)
    public List<Banner> getMainBanners() {
        return getActiveBannersByPosition("main");
    }
    
    // Lấy banner header
    public List<Banner> getHeaderBanners() {
        return getActiveBannersByPosition("header");
    }
    
    // Lấy banner sidebar
    public List<Banner> getSidebarBanners() {
        return getActiveBannersByPosition("sidebar");
    }
    
    // Lấy banner footer
    public List<Banner> getFooterBanners() {
        return getActiveBannersByPosition("footer");
    }
}
