package com.example.datn.product;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anh_san_pham")
public class AnhSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham", nullable = false)
    private SanPham sanPham;

    @Column(name = "url_anh")
    private String urlAnh;

    @Column(name = "thu_tu")
    private Integer thuTu;

    @Column(name = "ngay_them")
    private LocalDateTime ngayThem;

    public AnhSanPham() {}

    public AnhSanPham(SanPham sanPham, String urlAnh, Integer thuTu) {
        this.sanPham = sanPham;
        this.urlAnh = urlAnh;
        this.thuTu = thuTu;
        this.ngayThem = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }
    public String getUrlAnh() { return urlAnh; }
    public void setUrlAnh(String urlAnh) { this.urlAnh = urlAnh; }
    public Integer getThuTu() { return thuTu; }
    public void setThuTu(Integer thuTu) { this.thuTu = thuTu; }
    public LocalDateTime getNgayThem() { return ngayThem; }
    public void setNgayThem(LocalDateTime ngayThem) { this.ngayThem = ngayThem; }
}
