package com.example.datn.user;

import jakarta.persistence.*;

@Entity
@Table(name = "vai_tro")
public class VaiTro {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "ten_vai_tro")
	private String tenVaiTro;

	@Column(name = "mo_ta")
	private String moTa;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getTenVaiTro() { return tenVaiTro; }
	public void setTenVaiTro(String tenVaiTro) { this.tenVaiTro = tenVaiTro; }
	public String getMoTa() { return moTa; }
	public void setMoTa(String moTa) { this.moTa = moTa; }
} 