package com.example.datn.order;

import com.example.datn.product.SanPham;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_don_hang")
public class ChiTietDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_hang_id")
    private DonHang donHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "san_pham_id")
    private SanPham sanPham;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "gia", precision = 10, scale = 2)
    private BigDecimal gia;

    @Column(name = "thanh_tien", precision = 10, scale = 2)
    private BigDecimal thanhTien;

    @Column(name = "kich_co")
    private String kichCo;

    @Column(name = "mau_sac")
    private String mauSac;

    public ChiTietDonHang() {}

    public ChiTietDonHang(DonHang donHang, SanPham sanPham, Integer soLuong, BigDecimal gia) {
        this.donHang = donHang;
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.gia = gia;
        this.thanhTien = gia.multiply(BigDecimal.valueOf(soLuong));
    }

    public ChiTietDonHang(DonHang donHang, SanPham sanPham, Integer soLuong, BigDecimal gia, String kichCo, String mauSac) {
        this.donHang = donHang;
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.gia = gia;
        this.kichCo = kichCo;
        this.mauSac = mauSac;
        this.thanhTien = gia.multiply(BigDecimal.valueOf(soLuong));
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DonHang getDonHang() { return donHang; }
    public void setDonHang(DonHang donHang) { this.donHang = donHang; }

    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { 
        this.soLuong = soLuong;
        if (this.gia != null) {
            this.thanhTien = this.gia.multiply(BigDecimal.valueOf(soLuong));
        }
    }

    public BigDecimal getGia() { return gia; }
    public void setGia(BigDecimal gia) { 
        this.gia = gia;
        if (this.soLuong != null) {
            this.thanhTien = gia.multiply(BigDecimal.valueOf(soLuong));
        }
    }

    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }

    public String getKichCo() { return kichCo; }
    public void setKichCo(String kichCo) { this.kichCo = kichCo; }

    public String getMauSac() { return mauSac; }
    public void setMauSac(String mauSac) { this.mauSac = mauSac; }
}
