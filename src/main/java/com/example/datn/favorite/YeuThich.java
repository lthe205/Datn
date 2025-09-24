package com.example.datn.favorite;

import com.example.datn.product.SanPham;
import com.example.datn.user.NguoiDung;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "yeu_thich", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nguoi_dung_id", "san_pham_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YeuThich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "san_pham_id", nullable = false)
    private SanPham sanPham;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }
}
