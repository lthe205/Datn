package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_gio_hang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "so_luong", nullable = false)
    private Integer soLuong = 1;
    
    @Column(name = "gia", nullable = false, precision = 15, scale = 2)
    private BigDecimal gia;
    
    @Column(name = "kich_co")
    private String kichCo;
    
    @Column(name = "mau_sac")
    private String mauSac;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gio_hang_id", nullable = false)
    @JsonIgnore
    private Cart gioHang;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "san_pham_id", nullable = false)
    private Product sanPham;
    
    // Helper methods
    public BigDecimal getThanhTien() {
        return gia.multiply(BigDecimal.valueOf(soLuong));
    }
}
