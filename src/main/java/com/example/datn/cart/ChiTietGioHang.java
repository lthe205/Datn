package com.example.datn.cart;

import com.example.datn.product.SanPham;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_gio_hang")
public class ChiTietGioHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gio_hang_id")
    private GioHang gioHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "san_pham_id")
    private SanPham sanPham;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "gia", precision = 10, scale = 2)
    private BigDecimal gia;

    @Column(name = "kich_co")
    private String kichCo;

    @Column(name = "mau_sac")
    private String mauSac;

    public ChiTietGioHang() {}

    public ChiTietGioHang(GioHang gioHang, SanPham sanPham, Integer soLuong, BigDecimal gia) {
        this.gioHang = gioHang;
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.gia = gia;
    }

    public ChiTietGioHang(GioHang gioHang, SanPham sanPham, Integer soLuong, BigDecimal gia, String kichCo, String mauSac) {
        this.gioHang = gioHang;
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.gia = gia;
        this.kichCo = kichCo;
        this.mauSac = mauSac;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GioHang getGioHang() { return gioHang; }
    public void setGioHang(GioHang gioHang) { this.gioHang = gioHang; }
    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    public BigDecimal getGia() { return gia; }
    public void setGia(BigDecimal gia) { this.gia = gia; }
    public String getKichCo() { return kichCo; }
    public void setKichCo(String kichCo) { this.kichCo = kichCo; }
    public String getMauSac() { return mauSac; }
    public void setMauSac(String mauSac) { this.mauSac = mauSac; }

    // Helper methods
    public BigDecimal getThanhTien() {
        if (gia != null && soLuong != null) {
            return gia.multiply(BigDecimal.valueOf(soLuong));
        }
        return BigDecimal.ZERO;
    }
}
