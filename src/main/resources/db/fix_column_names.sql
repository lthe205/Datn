/* =========================================================
 Fix Column Names Migration - Đồng bộ tên cột với Entity
 Target: SQL Server 2019+
 Purpose: Sửa tên cột để khớp với JPA entity mapping
========================================================= */

USE DATN;
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET NOCOUNT ON;
GO

PRINT N'🔧 Bắt đầu sửa tên cột để khớp với Entity...';

-- 1. Sửa bảng dia_chi: id_nguoi_dung -> nguoi_dung_id
IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N'dbo.dia_chi') AND name = N'id_nguoi_dung')
BEGIN
    -- Drop foreign key constraint trước
    IF EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_dia_chi_nguoi_dung')
        ALTER TABLE dbo.dia_chi DROP CONSTRAINT FK_dia_chi_nguoi_dung;
    
    -- Rename column
    EXEC sp_rename 'dbo.dia_chi.id_nguoi_dung', 'nguoi_dung_id', 'COLUMN';
    
    -- Recreate foreign key constraint
    ALTER TABLE dbo.dia_chi 
        ADD CONSTRAINT FK_dia_chi_nguoi_dung 
        FOREIGN KEY (nguoi_dung_id) REFERENCES dbo.nguoi_dung(id) ON DELETE CASCADE;
    
    PRINT N'✅ Đã sửa dia_chi.id_nguoi_dung -> nguoi_dung_id';
END

-- 2. Sửa bảng don_hang: id_nguoi_dung -> nguoi_dung_id
IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N'dbo.don_hang') AND name = N'id_nguoi_dung')
BEGIN
    -- Drop foreign key constraint trước
    IF EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_don_hang_nguoi_dung')
        ALTER TABLE dbo.don_hang DROP CONSTRAINT FK_don_hang_nguoi_dung;
    
    -- Rename column
    EXEC sp_rename 'dbo.don_hang.id_nguoi_dung', 'nguoi_dung_id', 'COLUMN';
    
    -- Recreate foreign key constraint
    ALTER TABLE dbo.don_hang 
        ADD CONSTRAINT FK_don_hang_nguoi_dung 
        FOREIGN KEY (nguoi_dung_id) REFERENCES dbo.nguoi_dung(id);
    
    PRINT N'✅ Đã sửa don_hang.id_nguoi_dung -> nguoi_dung_id';
END

-- 3. Sửa bảng danh_gia: id_nguoi_dung -> nguoi_dung_id
IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N'dbo.danh_gia') AND name = N'id_nguoi_dung')
BEGIN
    -- Drop foreign key constraint trước
    IF EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_danh_gia_nguoi_dung')
        ALTER TABLE dbo.danh_gia DROP CONSTRAINT FK_danh_gia_nguoi_dung;
    
    -- Rename column
    EXEC sp_rename 'dbo.danh_gia.id_nguoi_dung', 'nguoi_dung_id', 'COLUMN';
    
    -- Recreate foreign key constraint
    ALTER TABLE dbo.danh_gia 
        ADD CONSTRAINT FK_danh_gia_nguoi_dung 
        FOREIGN KEY (nguoi_dung_id) REFERENCES dbo.nguoi_dung(id) ON DELETE CASCADE;
    
    PRINT N'✅ Đã sửa danh_gia.id_nguoi_dung -> nguoi_dung_id';
END

-- 4. Sửa bảng thong_bao: id_nguoi_dung -> nguoi_dung_id
IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N'dbo.thong_bao') AND name = N'id_nguoi_dung')
BEGIN
    -- Rename column (không có foreign key constraint)
    EXEC sp_rename 'dbo.thong_bao.id_nguoi_dung', 'nguoi_dung_id', 'COLUMN';
    
    PRINT N'✅ Đã sửa thong_bao.id_nguoi_dung -> nguoi_dung_id';
END

PRINT N'🎉 Hoàn tất sửa tên cột!';
PRINT N'✅ Tất cả cột đã được đổi tên để khớp với Entity mapping.';
