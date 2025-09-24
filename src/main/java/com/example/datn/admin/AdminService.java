package com.example.datn.admin;

import com.example.datn.admin.AdminController.AdminStats;
import com.example.datn.product.*;
import com.example.datn.user.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final NguoiDungRepository nguoiDungRepository;
    private final SanPhamRepository sanPhamRepository;
    private final DanhMucRepository danhMucRepository;
    private final ThuongHieuRepository thuongHieuRepository;

    public AdminService(NguoiDungRepository nguoiDungRepository, SanPhamRepository sanPhamRepository,
                       DanhMucRepository danhMucRepository, ThuongHieuRepository thuongHieuRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.danhMucRepository = danhMucRepository;
        this.thuongHieuRepository = thuongHieuRepository;
    }

    public AdminStats getAdminStats() {
        long totalUsers = nguoiDungRepository.count();
        long totalProducts = sanPhamRepository.count();
        long totalCategories = danhMucRepository.count();
        long totalBrands = thuongHieuRepository.count();
        long activeUsers = nguoiDungRepository.countByHoatDongTrue();
        long activeProducts = sanPhamRepository.countByHoatDongTrue();

        return new AdminStats(totalUsers, totalProducts, totalCategories, totalBrands, activeUsers, activeProducts);
    }

    public List<NguoiDung> getRecentUsers(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "ngayTao"));
        return nguoiDungRepository.findAll(pageRequest).getContent();
    }

    public List<SanPham> getTopProducts(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "luotXem"));
        return sanPhamRepository.findAll(pageRequest).getContent();
    }

    public List<SanPham> getBestSellingProducts(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "daBan"));
        return sanPhamRepository.findAll(pageRequest).getContent();
    }

    public List<NguoiDung> searchUsers(String keyword) {
        return nguoiDungRepository.findByTenContainingOrEmailContaining(keyword, keyword);
    }

    public List<SanPham> searchProducts(String keyword) {
        return sanPhamRepository.findByTenContainingOrMaSanPhamContaining(keyword, keyword);
    }
}
