package com.example.datn.user;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nguoi_dung")
public class NguoiDung {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "ten", nullable = false)
	private String ten;

	@Column(name = "email", nullable = false, unique = true, length = 100)
	private String email;

	@Column(name = "mat_khau", nullable = false)
	private String matKhau;

	@Column(name = "so_dien_thoai")
	private String soDienThoai;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vai_tro_id")
	private VaiTro vaiTro;

	@Column(name = "dia_chi")
	private String diaChi;

	@Column(name = "gioi_tinh")
	private String gioiTinh;

	@Column(name = "ngay_sinh")
	private LocalDate ngaySinh;

	@Column(name = "thanh_pho")
	private String thanhPho;

	@Column(name = "hoat_dong")
	private boolean hoatDong;

	@Column(name = "bi_khoa")
	private boolean biKhoa;

	@Column(name = "ly_do_khoa")
	private String lyDoKhoa;

	@Column(name = "ngay_tao")
	private LocalDateTime ngayTao;

	@Column(name = "ngay_cap_nhat")
	private LocalDateTime ngayCapNhat;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getTen() { return ten; }
	public void setTen(String ten) { this.ten = ten; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getMatKhau() { return matKhau; }
	public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
	public String getSoDienThoai() { return soDienThoai; }
	public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
	public VaiTro getVaiTro() { return vaiTro; }
	public void setVaiTro(VaiTro vaiTro) { this.vaiTro = vaiTro; }
	public String getDiaChi() { return diaChi; }
	public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
	public String getGioiTinh() { return gioiTinh; }
	public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
	public LocalDate getNgaySinh() { return ngaySinh; }
	public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
	public String getThanhPho() { return thanhPho; }
	public void setThanhPho(String thanhPho) { this.thanhPho = thanhPho; }
	public boolean isHoatDong() { return hoatDong; }
	public void setHoatDong(boolean hoatDong) { this.hoatDong = hoatDong; }
	public boolean isBiKhoa() { return biKhoa; }
	public void setBiKhoa(boolean biKhoa) { this.biKhoa = biKhoa; }
	public String getLyDoKhoa() { return lyDoKhoa; }
	public void setLyDoKhoa(String lyDoKhoa) { this.lyDoKhoa = lyDoKhoa; }
	public LocalDateTime getNgayTao() { return ngayTao; }
	public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
	public LocalDateTime getNgayCapNhat() { return ngayCapNhat; }
	public void setNgayCapNhat(LocalDateTime ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
} 