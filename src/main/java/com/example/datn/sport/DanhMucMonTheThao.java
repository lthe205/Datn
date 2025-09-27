package com.example.datn.sport;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "danh_muc_mon_the_thao")
public class DanhMucMonTheThao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ten", nullable = false, unique = true)
    private String ten;
    
    @Column(name = "mo_ta", length = 500)
    private String moTa;
    
    @Column(name = "hinh_anh", length = 500)
    private String hinhAnh;
    
    @Column(name = "thu_tu")
    private Integer thuTu = 0;
    
    @Column(name = "hoat_dong")
    private Boolean hoatDong = true;
    
    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
    
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
    
    // Constructors
    public DanhMucMonTheThao() {}
    
    public DanhMucMonTheThao(String ten, String moTa, String hinhAnh, Integer thuTu, Boolean hoatDong) {
        this.ten = ten;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
        this.thuTu = thuTu;
        this.hoatDong = hoatDong;
        this.ngayTao = LocalDateTime.now();
        this.ngayCapNhat = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTen() {
        return ten;
    }
    
    public void setTen(String ten) {
        this.ten = ten;
    }
    
    public String getMoTa() {
        return moTa;
    }
    
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    
    public String getHinhAnh() {
        return hinhAnh;
    }
    
    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }
    
    public Integer getThuTu() {
        return thuTu;
    }
    
    public void setThuTu(Integer thuTu) {
        this.thuTu = thuTu;
    }
    
    public Boolean getHoatDong() {
        return hoatDong;
    }
    
    public void setHoatDong(Boolean hoatDong) {
        this.hoatDong = hoatDong;
    }
    
    public LocalDateTime getNgayTao() {
        return ngayTao;
    }
    
    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
    
    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }
    
    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }
    
    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}
