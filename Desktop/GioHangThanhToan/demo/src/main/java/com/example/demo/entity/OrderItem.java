package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_don_hang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "so_luong")
    private Integer soLuong;
    
    @Column(name = "gia", precision = 15, scale = 2)
    private BigDecimal gia;
    
    @Column(name = "thanh_tien", precision = 15, scale = 2)
    private BigDecimal thanhTien;
    
    @Column(name = "kich_co")
    private String kichCo;
    
    @Column(name = "mau_sac")
    private String mauSac;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_hang_id", nullable = false)
    @JsonIgnore
    private Order donHang;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "san_pham_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product sanPham;
    
    // Helper method
    public BigDecimal getThanhTien() {
        if (thanhTien != null) {
            return thanhTien;
        }
        return gia.multiply(BigDecimal.valueOf(soLuong));
    }
}
