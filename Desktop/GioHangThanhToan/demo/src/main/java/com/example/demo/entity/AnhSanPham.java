package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "anh_san_pham")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnhSanPham {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "url_anh", nullable = false)
    private String urlAnh;
    
    @Column(name = "thu_tu")
    private Integer thuTu;
    
    @Column(name = "ngay_them", nullable = false)
    private LocalDateTime ngayThem = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham", nullable = false)
    private Product sanPham;
}
