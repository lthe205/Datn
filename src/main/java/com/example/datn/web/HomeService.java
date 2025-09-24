package com.example.datn.web;

import com.example.datn.product.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class HomeService {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    @Autowired
    private AnhSanPhamRepository anhSanPhamRepository;

    // Lấy danh sách sản phẩm nổi bật
    public List<SanPham> getSanPhamNoiBat() {
        return sanPhamRepository.findNoiBatAndHoatDongTrue();
    }

    // Lấy danh sách sản phẩm mới nhất
    public Page<SanPham> getSanPhamMoiNhat(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sanPhamRepository.findHoatDongTrueOrderByNgayTaoDesc(pageable);
    }

    // Lấy danh sách sản phẩm bán chạy nhất
    public Page<SanPham> getSanPhamBanChayNhat(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sanPhamRepository.findHoatDongTrueOrderByDaBanDesc(pageable);
    }

    // Lấy danh sách sản phẩm khuyến mãi
    public Page<SanPham> getSanPhamKhuyenMai(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sanPhamRepository.findSanPhamKhuyenMai(pageable);
    }

    // Lấy danh sách danh mục cha
    public List<DanhMuc> getDanhMucCha() {
        return danhMucRepository.findDanhMucCha();
    }

    // Lấy danh sách thương hiệu
    public List<ThuongHieu> getAllThuongHieu() {
        return thuongHieuRepository.findAll();
    }

    // Tìm kiếm sản phẩm
    public Page<SanPham> timKiemSanPham(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword == null || keyword.trim().isEmpty()) {
            return sanPhamRepository.findHoatDongTrueOrderByNgayTaoDesc(pageable);
        }
        return sanPhamRepository.findByKeywordAndHoatDongTrue(keyword.trim(), pageable);
    }

    // Lọc sản phẩm theo danh mục
    public Page<SanPham> locTheoDanhMuc(Long danhMucId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sanPhamRepository.findByDanhMucIdAndHoatDongTrue(danhMucId, pageable);
    }

    // Lọc sản phẩm theo thương hiệu
    public Page<SanPham> locTheoThuongHieu(Long thuongHieuId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sanPhamRepository.findByThuongHieuIdAndHoatDongTrue(thuongHieuId, pageable);
    }

    // Lọc sản phẩm theo khoảng giá
    public Page<SanPham> locTheoKhoangGia(BigDecimal giaMin, BigDecimal giaMax, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sanPhamRepository.findByGiaBetweenAndHoatDongTrue(giaMin, giaMax, pageable);
    }

    // Lấy chi tiết sản phẩm
    public SanPham getChiTietSanPham(Long id) {
        return sanPhamRepository.findById(id).orElse(null);
    }

    // Lấy ảnh sản phẩm
    public List<AnhSanPham> getAnhSanPham(Long sanPhamId) {
        return anhSanPhamRepository.findBySanPhamIdOrderByThuTuAsc(sanPhamId);
    }

    // Lấy ảnh chính của sản phẩm
    public AnhSanPham getAnhChinhSanPham(Long sanPhamId) {
        return anhSanPhamRepository.findAnhChinhBySanPhamId(sanPhamId);
    }

    // Tăng lượt xem sản phẩm
    public void tangLuotXem(Long sanPhamId) {
        SanPham sanPham = sanPhamRepository.findById(sanPhamId).orElse(null);
        if (sanPham != null) {
            sanPham.setLuotXem(sanPham.getLuotXem() + 1);
            sanPhamRepository.save(sanPham);
        }
    }

    // Lấy danh sách sản phẩm liên quan (cùng danh mục)
    public List<SanPham> getSanPhamLienQuan(Long sanPhamId, Long danhMucId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return sanPhamRepository.findByDanhMucIdAndHoatDongTrue(danhMucId, pageable)
                .getContent()
                .stream()
                .filter(sp -> !sp.getId().equals(sanPhamId))
                .toList();
    }

    // Lấy thống kê nhanh
    public HomeStats getHomeStats() {
        HomeStats stats = new HomeStats();
        
        // Đếm tổng sản phẩm
        stats.setTongSanPham(sanPhamRepository.count());
        
        // Đếm tổng danh mục
        stats.setTongDanhMuc(danhMucRepository.count());
        
        // Đếm tổng thương hiệu
        stats.setTongThuongHieu(thuongHieuRepository.count());
        
        return stats;
    }

    // Class để chứa thống kê trang chủ
    public static class HomeStats {
        private long tongSanPham;
        private long tongDanhMuc;
        private long tongThuongHieu;

        // Getters and Setters
        public long getTongSanPham() { return tongSanPham; }
        public void setTongSanPham(long tongSanPham) { this.tongSanPham = tongSanPham; }
        public long getTongDanhMuc() { return tongDanhMuc; }
        public void setTongDanhMuc(long tongDanhMuc) { this.tongDanhMuc = tongDanhMuc; }
        public long getTongThuongHieu() { return tongThuongHieu; }
        public void setTongThuongHieu(long tongThuongHieu) { this.tongThuongHieu = tongThuongHieu; }
    }

    // Biến thể sản phẩm
    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    public List<BienTheSanPham> getBienTheSanPham(Long sanPhamId) {
        return bienTheSanPhamRepository.findBySanPhamIdAndTrangThaiTrue(sanPhamId);
    }

    public List<String> getAvailableSizes(Long sanPhamId) {
        return bienTheSanPhamRepository.findDistinctKichCoBySanPhamIdAndTrangThaiTrue(sanPhamId);
    }

    public List<String> getAvailableColors(Long sanPhamId) {
        return bienTheSanPhamRepository.findDistinctMauSacBySanPhamIdAndTrangThaiTrue(sanPhamId);
    }

    public Map<String, String> getColorMap() {
        Map<String, String> colorMap = new HashMap<>();
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
        return colorMap;
    }
}
