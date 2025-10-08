package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "dia_chi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaChi {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ho_ten_nhan")
    private String hoTenNhan;
    
    @Column(name = "so_dien_thoai")
    private String soDienThoai;
    
    @Column(name = "dia_chi")
    private String diaChi;
    
    @Column(name = "tinh_thanh")
    private String tinhThanh;
    
    @Column(name = "quan_huyen")
    private String quanHuyen;
    
    @Column(name = "mac_dinh", nullable = false)
    private Boolean macDinh = false;
    
    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private User nguoiDung;
}
