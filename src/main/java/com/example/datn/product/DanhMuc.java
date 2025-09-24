package com.example.datn.product;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "danh_muc")
public class DanhMuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ten")
    private String ten;

    @Column(name = "mo_ta")
    private String moTa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cha")
    private DanhMuc danhMucCha;

    @OneToMany(mappedBy = "danhMucCha", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DanhMuc> danhMucCon;

    @OneToMany(mappedBy = "danhMuc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SanPham> sanPhams;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    public DanhMuc() {}

    public DanhMuc(String ten, String moTa) {
        this.ten = ten;
        this.moTa = moTa;
        this.ngayTao = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public DanhMuc getDanhMucCha() { return danhMucCha; }
    public void setDanhMucCha(DanhMuc danhMucCha) { this.danhMucCha = danhMucCha; }
    public List<DanhMuc> getDanhMucCon() { return danhMucCon; }
    public void setDanhMucCon(List<DanhMuc> danhMucCon) { this.danhMucCon = danhMucCon; }
    public List<SanPham> getSanPhams() { return sanPhams; }
    public void setSanPhams(List<SanPham> sanPhams) { this.sanPhams = sanPhams; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
}
