package com.example.datn.product;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bien_the_san_pham")
public class BienTheSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham", nullable = false)
    private SanPham sanPham;

    @Column(name = "kich_co")
    private String kichCo;

    @Column(name = "mau_sac")
    private String mauSac;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "gia_ban", precision = 10, scale = 2)
    private BigDecimal giaBan;

    @Column(name = "gia_khuyen_mai", precision = 10, scale = 2)
    private BigDecimal giaKhuyenMai;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    public BienTheSanPham() {}

    public BienTheSanPham(SanPham sanPham, String kichCo, String mauSac, Integer soLuong, BigDecimal giaBan) {
        this.sanPham = sanPham;
        this.kichCo = kichCo;
        this.mauSac = mauSac;
        this.soLuong = soLuong;
        this.giaBan = giaBan;
        this.trangThai = true;
        this.ngayTao = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }
    public String getKichCo() { return kichCo; }
    public void setKichCo(String kichCo) { this.kichCo = kichCo; }
    public String getMauSac() { return mauSac; }
    public void setMauSac(String mauSac) { this.mauSac = mauSac; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    public BigDecimal getGiaBan() { return giaBan; }
    public void setGiaBan(BigDecimal giaBan) { this.giaBan = giaBan; }
    public BigDecimal getGiaKhuyenMai() { return giaKhuyenMai; }
    public void setGiaKhuyenMai(BigDecimal giaKhuyenMai) { this.giaKhuyenMai = giaKhuyenMai; }
    public Boolean getTrangThai() { return trangThai; }
    public void setTrangThai(Boolean trangThai) { this.trangThai = trangThai; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
}
