package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "danh_muc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DanhMuc {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ten", nullable = false, unique = true)
    private String ten;
    
    @Column(name = "mo_ta")
    private String moTa;
    
    @Column(name = "hinh_anh")
    private String hinhAnh;
    
    @Column(name = "mau_sac")
    private String mauSac;
    
    @Column(name = "thu_tu", nullable = false)
    private Integer thuTu = 0;
    
    @Column(name = "hoat_dong", nullable = false)
    private Boolean hoatDong = true;
    
    @Column(name = "ngay_tao", nullable = false)
    private java.time.LocalDateTime ngayTao = java.time.LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cha")
    @JsonBackReference("danhmuc-parent")
    private DanhMuc danhMucCha;
    
    @OneToMany(mappedBy = "danhMucCha", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("danhmuc-parent")
    private java.util.List<DanhMuc> danhMucCon;
    
    @OneToMany(mappedBy = "danhMuc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("product-danhmuc")
    private java.util.List<Product> sanPhams;
}
