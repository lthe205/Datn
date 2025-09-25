package com.example.datn.cart;

import com.example.datn.product.SanPham;
import com.example.datn.product.SanPhamRepository;
import com.example.datn.product.TonKhoService;
import com.example.datn.user.NguoiDung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private ChiTietGioHangRepository chiTietGioHangRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private TonKhoService tonKhoService;

    public GioHang getOrCreateCart(NguoiDung nguoiDung) {
        Optional<GioHang> existingCart = gioHangRepository.findByNguoiDung(nguoiDung);
        if (existingCart.isPresent()) {
            return existingCart.get();
        } else {
            GioHang newCart = new GioHang(nguoiDung);
            return gioHangRepository.save(newCart);
        }
    }

    public void addToCart(NguoiDung nguoiDung, Long sanPhamId, Integer soLuong) {
        addToCart(nguoiDung, sanPhamId, soLuong, null, null);
    }

    public void addToCart(NguoiDung nguoiDung, Long sanPhamId, Integer soLuong, String kichCo, String mauSac) {
        SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (!sanPham.getHoatDong()) {
            throw new RuntimeException("Sản phẩm không còn hoạt động");
        }

        // Kiểm tra tồn kho
        int available = tonKhoService.getCurrentStock(sanPhamId);
        if (soLuong == null || soLuong <= 0) soLuong = 1;

        GioHang gioHang = getOrCreateCart(nguoiDung);
        // Tính số lượng hiện tại trong giỏ cho cùng biến thể
        int existingQty = chiTietGioHangRepository.findByGioHangAndSanPhamAndKichCoAndMauSac(
                gioHang, sanPham, kichCo, mauSac
        ).map(ChiTietGioHang::getSoLuong).orElse(0);

        if (existingQty + soLuong > available) {
            throw new RuntimeException("Số lượng tồn không đủ. Hiện còn: " + available);
        }


        // Tìm item hiện tại với cùng sản phẩm, kích cỡ và màu sắc
        Optional<ChiTietGioHang> existingItem = chiTietGioHangRepository.findByGioHangAndSanPhamAndKichCoAndMauSac(
                gioHang, sanPham, kichCo, mauSac);

        if (existingItem.isPresent()) {
            ChiTietGioHang chiTiet = existingItem.get();
            chiTiet.setSoLuong(chiTiet.getSoLuong() + soLuong);
            chiTietGioHangRepository.save(chiTiet);
        } else {
            ChiTietGioHang chiTiet = new ChiTietGioHang(gioHang, sanPham, soLuong, sanPham.getGia(), kichCo, mauSac);
            chiTietGioHangRepository.save(chiTiet);
        }
    }

    public void updateCartItem(NguoiDung nguoiDung, Long sanPhamId, Integer soLuong) {
        GioHang gioHang = getOrCreateCart(nguoiDung);
        SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        Optional<ChiTietGioHang> existingItem = chiTietGioHangRepository.findByGioHangAndSanPham(gioHang, sanPham);

        if (existingItem.isPresent()) {
            if (soLuong <= 0) {
                chiTietGioHangRepository.delete(existingItem.get());
            } else {
                int available = tonKhoService.getCurrentStock(sanPhamId);
                if (soLuong > available) {
                    throw new RuntimeException("Số lượng tồn không đủ. Hiện còn: " + available);
                }
                ChiTietGioHang chiTiet = existingItem.get();
                chiTiet.setSoLuong(soLuong);
                chiTietGioHangRepository.save(chiTiet);
            }
        }
    }

    public void removeFromCart(NguoiDung nguoiDung, Long sanPhamId) {
        GioHang gioHang = getOrCreateCart(nguoiDung);
        SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        chiTietGioHangRepository.deleteByGioHangAndSanPham(gioHang, sanPham);
    }

    public List<ChiTietGioHang> getCartItems(NguoiDung nguoiDung) {
        GioHang gioHang = getOrCreateCart(nguoiDung);
        return chiTietGioHangRepository.findByGioHangId(gioHang.getId());
    }

    public BigDecimal getCartTotal(NguoiDung nguoiDung) {
        List<ChiTietGioHang> cartItems = getCartItems(nguoiDung);
        return cartItems.stream()
                .map(ChiTietGioHang::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getCartItemCount(NguoiDung nguoiDung) {
        List<ChiTietGioHang> cartItems = getCartItems(nguoiDung);
        return cartItems.stream()
                .mapToInt(ChiTietGioHang::getSoLuong)
                .sum();
    }

    public void clearCart(NguoiDung nguoiDung) {
        GioHang gioHang = getOrCreateCart(nguoiDung);
        chiTietGioHangRepository.deleteByGioHangId(gioHang.getId());
    }
}
