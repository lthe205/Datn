package com.example.datn.order;

import com.example.datn.user.NguoiDung;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "don_hang")
public class DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id")
    private NguoiDung nguoiDung;

    @Column(name = "tong_tien", precision = 15, scale = 2)
    private BigDecimal tongTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiDonHang trangThai;

    @Column(name = "dia_chi_giao_hang")
    private String diaChiGiaoHang;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietDonHang> chiTietDonHangs;

    public DonHang() {}

    public DonHang(NguoiDung nguoiDung, BigDecimal tongTien, String diaChiGiaoHang) {
        this.nguoiDung = nguoiDung;
        this.tongTien = tongTien;
        this.diaChiGiaoHang = diaChiGiaoHang;
        this.trangThai = TrangThaiDonHang.CHO_XAC_NHAN;
        this.ngayTao = LocalDateTime.now();
        this.ngayCapNhat = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public NguoiDung getNguoiDung() { return nguoiDung; }
    public void setNguoiDung(NguoiDung nguoiDung) { this.nguoiDung = nguoiDung; }

    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }

    public TrangThaiDonHang getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiDonHang trangThai) { this.trangThai = trangThai; }

    public String getDiaChiGiaoHang() { return diaChiGiaoHang; }
    public void setDiaChiGiaoHang(String diaChiGiaoHang) { this.diaChiGiaoHang = diaChiGiaoHang; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

    public LocalDateTime getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(LocalDateTime ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }

    public List<ChiTietDonHang> getChiTietDonHangs() { return chiTietDonHangs; }
    public void setChiTietDonHangs(List<ChiTietDonHang> chiTietDonHangs) { this.chiTietDonHangs = chiTietDonHangs; }

    public enum TrangThaiDonHang {
        CHO_XAC_NHAN,
        DA_XAC_NHAN,
        DANG_GIAO,
        DA_GIAO,
        DA_HUY
    }
}
