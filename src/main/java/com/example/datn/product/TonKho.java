package com.example.datn.product;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "ton_kho")
@Data
public class TonKho {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "san_pham_id", nullable = false)
        private SanPham sanPham;

        @Column(name = "so_luong_ton", nullable = false)
        private Integer soLuongTon;

        @Column(name = "so_luong_nhap")
        private Integer soLuongNhap;

        @Column(name = "so_luong_xuat")
        private Integer soLuongXuat;

        @Column(name = "ngay_cap_nhat", nullable = false)
        private LocalDateTime ngayCapNhat;

        @Column(name = "ghi_chu")
        private String ghiChu;

        @PrePersist
        @PreUpdate
        protected void onAudit() {
            if (ngayCapNhat == null) {
                ngayCapNhat = LocalDateTime.now();
            } else {
                ngayCapNhat = LocalDateTime.now();
            }
        }
    }

