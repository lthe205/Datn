package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bien_the_san_pham")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BienTheSanPham {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "kich_co")
    private String kichCo;
    
    @Column(name = "mau_sac")
    private String mauSac;
    
    @Column(name = "so_luong")
    private Integer soLuong;
    
    @Column(name = "gia_ban", precision = 15, scale = 2)
    private java.math.BigDecimal giaBan;
    
    @Column(name = "gia_khuyen_mai", precision = 15, scale = 2)
    private java.math.BigDecimal giaKhuyenMai;
    
    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = true;
    
    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham", nullable = false)
    private Product sanPham;
}
