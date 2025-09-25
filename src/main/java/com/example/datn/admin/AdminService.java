package com.example.datn.admin;

import com.example.datn.admin.AdminController.AdminStats;
import com.example.datn.order.DonHangRepository;
import com.example.datn.product.*;
import com.example.datn.user.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    private final NguoiDungRepository nguoiDungRepository;
    private final SanPhamRepository sanPhamRepository;
    private final DanhMucRepository danhMucRepository;
    private final ThuongHieuRepository thuongHieuRepository;
    private final DonHangRepository donHangRepository;

    public AdminService(NguoiDungRepository nguoiDungRepository, SanPhamRepository sanPhamRepository,
                       DanhMucRepository danhMucRepository, ThuongHieuRepository thuongHieuRepository,
                       DonHangRepository donHangRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.danhMucRepository = danhMucRepository;
        this.thuongHieuRepository = thuongHieuRepository;
        this.donHangRepository = donHangRepository;
    }

    public AdminStats getAdminStats() {
        long totalUsers = nguoiDungRepository.count();
        long totalProducts = sanPhamRepository.count();
        long totalCategories = danhMucRepository.count();
        long totalBrands = thuongHieuRepository.count();
        long activeUsers = nguoiDungRepository.countByHoatDongTrue();
        long activeProducts = sanPhamRepository.countByHoatDongTrue();

        // Tính doanh thu hôm nay và tháng này
        BigDecimal todayRevenue = donHangRepository.getRevenueByDate(LocalDate.now());
        LocalDate now = LocalDate.now();
        BigDecimal monthRevenue = donHangRepository.getRevenueByMonth(now.getYear(), now.getMonthValue());

        return new AdminStats(totalUsers, totalProducts, totalCategories, totalBrands, activeUsers, activeProducts, todayRevenue, monthRevenue);
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

    // Chart data methods
    public List<Object[]> getWeeklyRevenue() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusWeeks(1);
        return donHangRepository.getDailyRevenue(startDate, endDate);
    }

    public List<Object[]> getMonthlyRevenue() {
        LocalDate now = LocalDate.now();
        return donHangRepository.getMonthlyRevenue(now.getYear());
    }

    public List<Object[]> getYearlyRevenue() {
        LocalDateTime startDate = LocalDateTime.now().minusYears(5);
        return donHangRepository.getYearlyRevenue(startDate);
    }
}
