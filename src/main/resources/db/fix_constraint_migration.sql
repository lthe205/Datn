/* =========================================================
 Fix Constraint Migration Issues for Hibernate Schema Update
 Target: SQL Server 2019+
 Purpose: Drop constraints before column alterations, then recreate them
========================================================= */

USE DATN;
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET NOCOUNT ON;
GO

PRINT N'🔧 Bắt đầu sửa lỗi constraint migration...';

-- 1. Drop constraints that are causing issues
-- Drop unique constraints first
IF EXISTS (SELECT * FROM sys.key_constraints WHERE name = 'UQ_chi_tiet_gio_hang_gio_hang_san_pham_kich_co_mau_sac')
BEGIN
    ALTER TABLE dbo.chi_tiet_gio_hang DROP CONSTRAINT UQ_chi_tiet_gio_hang_gio_hang_san_pham_kich_co_mau_sac;
    PRINT N'✅ Dropped UQ_chi_tiet_gio_hang_gio_hang_san_pham_kich_co_mau_sac';
END

IF EXISTS (SELECT * FROM sys.key_constraints WHERE name = 'UQ_danh_muc_ten')
BEGIN
    ALTER TABLE dbo.danh_muc DROP CONSTRAINT UQ_danh_muc_ten;
    PRINT N'✅ Dropped UQ_danh_muc_ten';
END

IF EXISTS (SELECT * FROM sys.key_constraints WHERE name = 'UQ_thuong_hieu_ten')
BEGIN
    ALTER TABLE dbo.thuong_hieu DROP CONSTRAINT UQ_thuong_hieu_ten;
    PRINT N'✅ Dropped UQ_thuong_hieu_ten';
END

IF EXISTS (SELECT * FROM sys.key_constraints WHERE name = 'UQ_vai_tro_ten')
BEGIN
    ALTER TABLE dbo.vai_tro DROP CONSTRAINT UQ_vai_tro_ten;
    PRINT N'✅ Dropped UQ_vai_tro_ten';
END

-- Drop default constraints
IF EXISTS (SELECT * FROM sys.default_constraints WHERE name = 'DF_don_hang_trang_thai')
BEGIN
    ALTER TABLE dbo.don_hang DROP CONSTRAINT DF_don_hang_trang_thai;
    PRINT N'✅ Dropped DF_don_hang_trang_thai';
END

IF EXISTS (SELECT * FROM sys.default_constraints WHERE name = 'DF_nguoi_dung_provider')
BEGIN
    ALTER TABLE dbo.nguoi_dung DROP CONSTRAINT DF_nguoi_dung_provider;
    PRINT N'✅ Dropped DF_nguoi_dung_provider';
END

-- 2. Alter columns to match Hibernate expectations
-- Alter chi_tiet_gio_hang columns
ALTER TABLE dbo.chi_tiet_gio_hang 
    ALTER COLUMN kich_co NVARCHAR(255) NULL;
ALTER TABLE dbo.chi_tiet_gio_hang 
    ALTER COLUMN mau_sac NVARCHAR(255) NULL;
PRINT N'✅ Altered chi_tiet_gio_hang columns';

-- Alter danh_muc column
ALTER TABLE dbo.danh_muc 
    ALTER COLUMN ten NVARCHAR(255) NOT NULL;
PRINT N'✅ Altered danh_muc.ten column';

-- Alter don_hang columns
ALTER TABLE dbo.don_hang 
    ALTER COLUMN dia_chi_giao_hang NVARCHAR(255) NULL;
ALTER TABLE dbo.don_hang 
    ALTER COLUMN ghi_chu NVARCHAR(255) NULL;
ALTER TABLE dbo.don_hang 
    ALTER COLUMN tong_tien DECIMAL(15,2) NULL;
ALTER TABLE dbo.don_hang 
    ALTER COLUMN trang_thai NVARCHAR(255) NOT NULL;
PRINT N'✅ Altered don_hang columns';

-- Alter nguoi_dung columns
ALTER TABLE dbo.nguoi_dung 
    ALTER COLUMN avatar_url NVARCHAR(255) NULL;
ALTER TABLE dbo.nguoi_dung 
    ALTER COLUMN dia_chi NVARCHAR(255) NULL;
ALTER TABLE dbo.nguoi_dung 
    ALTER COLUMN gioi_tinh NVARCHAR(255) NULL;
ALTER TABLE dbo.nguoi_dung 
    ALTER COLUMN provider NVARCHAR(255) NULL;
ALTER TABLE dbo.nguoi_dung 
    ALTER COLUMN so_dien_thoai NVARCHAR(255) NULL;
ALTER TABLE dbo.nguoi_dung 
    ALTER COLUMN ten NVARCHAR(255) NOT NULL;
ALTER TABLE dbo.nguoi_dung 
    ALTER COLUMN thanh_pho NVARCHAR(255) NULL;
PRINT N'✅ Altered nguoi_dung columns';

-- Alter other columns
ALTER TABLE dbo.otp_token 
    ALTER COLUMN email NVARCHAR(255) NOT NULL;
ALTER TABLE dbo.san_pham 
    ALTER COLUMN chat_lieu NVARCHAR(255) NULL;
ALTER TABLE dbo.san_pham 
    ALTER COLUMN ten NVARCHAR(255) NOT NULL;
ALTER TABLE dbo.san_pham 
    ALTER COLUMN xuat_xu NVARCHAR(255) NULL;
ALTER TABLE dbo.thuong_hieu 
    ALTER COLUMN ten NVARCHAR(255) NOT NULL;
ALTER TABLE dbo.vai_tro 
    ALTER COLUMN ten_vai_tro NVARCHAR(255) NOT NULL;
PRINT N'✅ Altered other table columns';

-- 3. Recreate constraints
-- Recreate unique constraints
ALTER TABLE dbo.chi_tiet_gio_hang 
    ADD CONSTRAINT UQ_chi_tiet_gio_hang_gio_hang_san_pham_kich_co_mau_sac
    UNIQUE (gio_hang_id, san_pham_id, kich_co, mau_sac);
PRINT N'✅ Recreated UQ_chi_tiet_gio_hang_gio_hang_san_pham_kich_co_mau_sac';

ALTER TABLE dbo.danh_muc 
    ADD CONSTRAINT UQ_danh_muc_ten UNIQUE (ten);
PRINT N'✅ Recreated UQ_danh_muc_ten';

ALTER TABLE dbo.thuong_hieu 
    ADD CONSTRAINT UQ_thuong_hieu_ten UNIQUE (ten);
PRINT N'✅ Recreated UQ_thuong_hieu_ten';

ALTER TABLE dbo.vai_tro 
    ADD CONSTRAINT UQ_vai_tro_ten UNIQUE (ten_vai_tro);
PRINT N'✅ Recreated UQ_vai_tro_ten';

-- Recreate default constraints
ALTER TABLE dbo.don_hang 
    ADD CONSTRAINT DF_don_hang_trang_thai DEFAULT (N'CHO_XAC_NHAN') FOR trang_thai;
PRINT N'✅ Recreated DF_don_hang_trang_thai';

ALTER TABLE dbo.nguoi_dung 
    ADD CONSTRAINT DF_nguoi_dung_provider DEFAULT (N'local') FOR provider;
PRINT N'✅ Recreated DF_nguoi_dung_provider';

PRINT N'🎉 Hoàn tất sửa lỗi constraint migration!';
PRINT N'✅ Tất cả constraints đã được tạo lại thành công.';
