package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "san_pham")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ma_san_pham", nullable = false, unique = true)
    private String maSanPham;
    
    @Column(name = "ten", nullable = false)
    private String ten;
    
    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;
    
    @Column(name = "gia", precision = 15, scale = 2)
    private BigDecimal gia;
    
    @Column(name = "gia_goc", precision = 15, scale = 2)
    private BigDecimal giaGoc;
    
    @Column(name = "anh_chinh")
    private String anhChinh;
    
    @Column(name = "so_luong_ton", nullable = false)
    private Integer soLuongTon = 0;
    
    @Column(name = "chat_lieu")
    private String chatLieu;
    
    @Column(name = "xuat_xu")
    private String xuatXu;
    
    @Column(name = "luot_xem", nullable = false)
    private Integer luotXem = 0;
    
    @Column(name = "da_ban", nullable = false)
    private Integer daBan = 0;
    
    @Column(name = "hoat_dong", nullable = false)
    private Boolean hoatDong = true;
    
    @Column(name = "noi_bat", nullable = false)
    private Boolean noiBat = false;
    
    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();
    
    @Column(name = "ngay_cap_nhat", nullable = false)
    private LocalDateTime ngayCapNhat = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_muc")
    @JsonBackReference("product-danhmuc")
    private DanhMuc danhMuc;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thuong_hieu")
    @JsonBackReference("product-thuonghieu")
    private ThuongHieu thuongHieu;
    
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BienTheSanPham> bienTheSanPhams;
    
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AnhSanPham> anhSanPhams;
    
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CartItem> cartItems;
    
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderItem> orderItems;
    
    @PreUpdate
    public void preUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}
