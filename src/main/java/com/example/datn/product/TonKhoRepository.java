package com.example.datn.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TonKhoRepository extends JpaRepository<TonKho,Long> {
    List<TonKho> findBySanPhamId(Long sanPhamId);
    TonKho findTopBySanPhamIdOrderByNgayCapNhatDesc(Long sanPhamId);
    List<TonKho> findBySanPhamIdOrderByNgayCapNhatDesc(Long sanPhamId);
}
