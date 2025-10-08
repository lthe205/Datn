package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "thanh_toan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "loai")
    private String loai = "VNPAY";
    
    @Column(name = "ma_giao_dich")
    private String maGiaoDich;
    
    @Column(name = "so_tien", precision = 15, scale = 2)
    private BigDecimal soTien;
    
    @Column(name = "trang_thai")
    private String trangThai = "PENDING";
    
    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();
    
    // Các trường bổ sung có trong database
    @Column(name = "ma_phan_hoi")
    private String maPhanHoi;

    @Column(name = "thong_tin_phan_hoi")
    private String thongTinPhanHoi;

    @Column(name = "ma_giao_dich_vnpay")
    private String maGiaoDichVnpay;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat = LocalDateTime.now();

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "phuong_thuc")
    private String phuongThuc;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_don_hang", nullable = false)
    @JsonIgnore
    private Order donHang;
    
    @PreUpdate
    public void preUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
    
    // Helper methods
    public String getTrangThaiDisplay() {
        switch (trangThai) {
            case "PENDING": return "Chờ thanh toán";
            case "SUCCESS": return "Thành công";
            case "FAILED": return "Thất bại";
            case "CANCELLED": return "Đã hủy";
            default: return trangThai;
        }
    }
    
    public boolean isSuccess() {
        return "SUCCESS".equals(trangThai);
    }
    
    public boolean isPending() {
        return "PENDING".equals(trangThai);
    }
}