package com.example.datn.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TonKhoService {

    private final TonKhoRepository tonKhoRepository;
    private final SanPhamRepository sanPhamRepository;

    @Transactional(readOnly = true)
    public TonKho getLatestRecordBySanPhamId(Long sanPhamId) {
        return tonKhoRepository.findTopBySanPhamIdOrderByNgayCapNhatDesc(sanPhamId);
    }

    @Transactional(readOnly = true)
    public int getCurrentStock(Long sanPhamId) {
        TonKho latest = getLatestRecordBySanPhamId(sanPhamId);
        if (latest != null && latest.getSoLuongTon() != null) {
            return latest.getSoLuongTon();
        }
        return sanPhamRepository.findById(sanPhamId)
                .map(sp -> sp.getSoLuongTon() == null ? 0 : sp.getSoLuongTon())
                .orElse(0);
    }

    @Transactional
    public TonKho setStock(Long sanPhamId, int newQuantity, String note) {
        if (newQuantity < 0) newQuantity = 0;
        SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
        int current = getCurrentStock(sanPhamId);
        int delta = newQuantity - current;
        TonKho record = new TonKho();
        record.setSanPham(sanPham);
        record.setSoLuongTon(newQuantity);
        record.setSoLuongNhap(delta > 0 ? delta : null);
        record.setSoLuongXuat(delta < 0 ? Math.abs(delta) : null);
        record.setGhiChu(note);
        TonKho saved = tonKhoRepository.save(record);
        syncSanPhamStockField(sanPham, newQuantity);
        return saved;
    }

    @Transactional
    public TonKho increaseStock(Long sanPhamId, int amount, String note) {
        if (amount <= 0) throw new IllegalArgumentException("Số lượng nhập phải > 0");
        int current = getCurrentStock(sanPhamId);
        return setStock(sanPhamId, current + amount, note);
    }

    @Transactional
    public TonKho decreaseStock(Long sanPhamId, int amount, String note) {
        if (amount <= 0) throw new IllegalArgumentException("Số lượng xuất phải > 0");
        int current = getCurrentStock(sanPhamId);
        if (amount > current) throw new IllegalArgumentException("Số lượng tồn không đủ");
        return setStock(sanPhamId, current - amount, note);
    }

    @Transactional
    public void syncSanPhamStockField(SanPham sanPham, int soLuongTon) {
        sanPham.setSoLuongTon(soLuongTon);
        sanPham.setNgayCapNhat(java.time.LocalDateTime.now());
        sanPhamRepository.save(sanPham);
    }

    @Transactional(readOnly = true)
    public java.util.List<TonKho> getHistory(Long sanPhamId) {
        return tonKhoRepository.findBySanPhamIdOrderByNgayCapNhatDesc(sanPhamId);
    }
}
