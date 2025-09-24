package com.example.datn.favorite;

import com.example.datn.product.SanPham;
import com.example.datn.product.SanPhamRepository;
import com.example.datn.user.NguoiDung;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {

    @Autowired
    private YeuThichRepository yeuThichRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Transactional
    public boolean toggleFavorite(NguoiDung nguoiDung, Long sanPhamId) {
        SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        Optional<YeuThich> existingFavorite = yeuThichRepository.findByNguoiDungAndSanPham(nguoiDung, sanPham);
        
        if (existingFavorite.isPresent()) {
            // Đã yêu thích -> bỏ yêu thích
            yeuThichRepository.delete(existingFavorite.get());
            return false;
        } else {
            // Chưa yêu thích -> thêm yêu thích
            YeuThich yeuThich = new YeuThich();
            yeuThich.setNguoiDung(nguoiDung);
            yeuThich.setSanPham(sanPham);
            yeuThich.setNgayTao(java.time.LocalDateTime.now());
            yeuThichRepository.save(yeuThich);
            return true;
        }
    }

    public boolean isFavorite(NguoiDung nguoiDung, Long sanPhamId) {
        SanPham sanPham = sanPhamRepository.findById(sanPhamId).orElse(null);
        if (sanPham == null) return false;
        
        return yeuThichRepository.existsByNguoiDungAndSanPham(nguoiDung, sanPham);
    }

    public List<YeuThich> getUserFavorites(NguoiDung nguoiDung) {
        return yeuThichRepository.findByNguoiDung(nguoiDung);
    }

    public long getFavoriteCount(NguoiDung nguoiDung) {
        return yeuThichRepository.countByNguoiDung(nguoiDung);
    }

    @Transactional
    public void removeFavorite(NguoiDung nguoiDung, Long sanPhamId) {
        SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
        
        yeuThichRepository.deleteByNguoiDungAndSanPham(nguoiDung, sanPham);
    }
}
