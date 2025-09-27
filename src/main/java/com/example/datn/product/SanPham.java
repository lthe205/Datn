package com.example.datn.product;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "san_pham")
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ma_san_pham", unique = true, length = 50)
    private String maSanPham;

    @Column(name = "ten")
    private String ten;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "gia", precision = 10, scale = 2)
    private BigDecimal gia;

    @Column(name = "gia_goc", precision = 10, scale = 2)
    private BigDecimal giaGoc;

    @Column(name = "anh_chinh")
    private String anhChinh;

    @Column(name = "so_luong_ton")
    private Integer soLuongTon;

    @Column(name = "chat_lieu")
    private String chatLieu;

    @Column(name = "xuat_xu")
    private String xuatXu;

    @Column(name = "luot_xem")
    private Integer luotXem;

    @Column(name = "da_ban")
    private Integer daBan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_danh_muc")
    private DanhMuc danhMuc;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_thuong_hieu")
    private ThuongHieu thuongHieu;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_mon_the_thao")
    private com.example.datn.sport.DanhMucMonTheThao danhMucMonTheThao;

    @Column(name = "hoat_dong")
    private Boolean hoatDong;

    @Column(name = "noi_bat")
    private Boolean noiBat;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BienTheSanPham> bienTheSanPhams;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AnhSanPham> anhSanPhams;

    public SanPham() {}

    public SanPham(String maSanPham, String ten, String moTa, BigDecimal gia, BigDecimal giaGoc) {
        this.maSanPham = maSanPham;
        this.ten = ten;
        this.moTa = moTa;
        this.gia = gia;
        this.giaGoc = giaGoc;
        this.soLuongTon = 0;
        this.luotXem = 0;
        this.daBan = 0;
        this.hoatDong = true;
        this.noiBat = false;
        this.ngayTao = LocalDateTime.now();
        this.ngayCapNhat = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMaSanPham() { return maSanPham; }
    public void setMaSanPham(String maSanPham) { this.maSanPham = maSanPham; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public BigDecimal getGia() { return gia; }
    public void setGia(BigDecimal gia) { this.gia = gia; }
    public BigDecimal getGiaGoc() { return giaGoc; }
    public void setGiaGoc(BigDecimal giaGoc) { this.giaGoc = giaGoc; }
    public String getAnhChinh() { return anhChinh; }
    public void setAnhChinh(String anhChinh) { this.anhChinh = anhChinh; }
    public Integer getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Integer soLuongTon) { this.soLuongTon = soLuongTon; }
    public String getChatLieu() { return chatLieu; }
    public void setChatLieu(String chatLieu) { this.chatLieu = chatLieu; }
    public String getXuatXu() { return xuatXu; }
    public void setXuatXu(String xuatXu) { this.xuatXu = xuatXu; }
    public Integer getLuotXem() { return luotXem; }
    public void setLuotXem(Integer luotXem) { this.luotXem = luotXem; }
    public Integer getDaBan() { return daBan; }
    public void setDaBan(Integer daBan) { this.daBan = daBan; }
    public DanhMuc getDanhMuc() { return danhMuc; }
    public void setDanhMuc(DanhMuc danhMuc) { this.danhMuc = danhMuc; }
    public ThuongHieu getThuongHieu() { return thuongHieu; }
    public void setThuongHieu(ThuongHieu thuongHieu) { this.thuongHieu = thuongHieu; }
    public com.example.datn.sport.DanhMucMonTheThao getDanhMucMonTheThao() { return danhMucMonTheThao; }
    public void setDanhMucMonTheThao(com.example.datn.sport.DanhMucMonTheThao danhMucMonTheThao) { this.danhMucMonTheThao = danhMucMonTheThao; }
    public Boolean getHoatDong() { return hoatDong; }
    public void setHoatDong(Boolean hoatDong) { this.hoatDong = hoatDong; }
    public Boolean getNoiBat() { return noiBat; }
    public void setNoiBat(Boolean noiBat) { this.noiBat = noiBat; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
    public LocalDateTime getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(LocalDateTime ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
    public List<BienTheSanPham> getBienTheSanPhams() { return bienTheSanPhams; }
    public void setBienTheSanPhams(List<BienTheSanPham> bienTheSanPhams) { this.bienTheSanPhams = bienTheSanPhams; }
    public List<AnhSanPham> getAnhSanPhams() { return anhSanPhams; }
    public void setAnhSanPhams(List<AnhSanPham> anhSanPhams) { this.anhSanPhams = anhSanPhams; }
}
