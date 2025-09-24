package com.example.datn.cart;

import com.example.datn.user.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Long> {
    Optional<GioHang> findByNguoiDung(NguoiDung nguoiDung);
    Optional<GioHang> findByNguoiDungId(Long nguoiDungId);
}
