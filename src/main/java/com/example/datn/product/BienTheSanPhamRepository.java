package com.example.datn.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BienTheSanPhamRepository extends JpaRepository<BienTheSanPham, Long> {

    // Tìm biến thể theo sản phẩm
    @Query("SELECT b FROM BienTheSanPham b WHERE b.sanPham.id = :sanPhamId AND b.trangThai = true")
    List<BienTheSanPham> findBySanPhamIdAndTrangThaiTrue(@Param("sanPhamId") Long sanPhamId);

    // Tìm biến thể theo sản phẩm và kích cỡ
    @Query("SELECT b FROM BienTheSanPham b WHERE b.sanPham.id = :sanPhamId AND b.kichCo = :kichCo AND b.trangThai = true")
    List<BienTheSanPham> findBySanPhamIdAndKichCoAndTrangThaiTrue(@Param("sanPhamId") Long sanPhamId, 
                                                                  @Param("kichCo") String kichCo);

    // Tìm biến thể theo sản phẩm và màu sắc
    @Query("SELECT b FROM BienTheSanPham b WHERE b.sanPham.id = :sanPhamId AND b.mauSac = :mauSac AND b.trangThai = true")
    List<BienTheSanPham> findBySanPhamIdAndMauSacAndTrangThaiTrue(@Param("sanPhamId") Long sanPhamId, 
                                                                  @Param("mauSac") String mauSac);

    // Tìm tất cả kích cỡ có sẵn của sản phẩm
    @Query("SELECT DISTINCT b.kichCo FROM BienTheSanPham b WHERE b.sanPham.id = :sanPhamId AND b.trangThai = true AND b.kichCo IS NOT NULL")
    List<String> findDistinctKichCoBySanPhamIdAndTrangThaiTrue(@Param("sanPhamId") Long sanPhamId);

    // Tìm tất cả màu sắc có sẵn của sản phẩm
    @Query("SELECT DISTINCT b.mauSac FROM BienTheSanPham b WHERE b.sanPham.id = :sanPhamId AND b.trangThai = true AND b.mauSac IS NOT NULL")
    List<String> findDistinctMauSacBySanPhamIdAndTrangThaiTrue(@Param("sanPhamId") Long sanPhamId);
}
