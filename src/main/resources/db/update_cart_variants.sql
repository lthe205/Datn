-- Script để cập nhật bảng chi_tiet_gio_hang thêm cột biến thể
-- Chạy script này trước khi chạy ứng dụng

-- Kiểm tra xem cột đã tồn tại chưa
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('dbo.chi_tiet_gio_hang') AND name = 'kich_co')
BEGIN
    -- Thêm cột kich_co
    ALTER TABLE dbo.chi_tiet_gio_hang 
    ADD kich_co NVARCHAR(20) NULL;
    PRINT N'✅ Đã thêm cột kich_co';
END
ELSE
BEGIN
    PRINT N'⚠️ Cột kich_co đã tồn tại';
END

-- Kiểm tra xem cột đã tồn tại chưa
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('dbo.chi_tiet_gio_hang') AND name = 'mau_sac')
BEGIN
    -- Thêm cột mau_sac
    ALTER TABLE dbo.chi_tiet_gio_hang 
    ADD mau_sac NVARCHAR(50) NULL;
    PRINT N'✅ Đã thêm cột mau_sac';
END
ELSE
BEGIN
    PRINT N'⚠️ Cột mau_sac đã tồn tại';
END

-- Xóa constraint cũ nếu tồn tại
IF EXISTS (SELECT * FROM sys.key_constraints WHERE name = 'UQ_chi_tiet_gio_hang_gio_hang_san_pham')
BEGIN
    ALTER TABLE dbo.chi_tiet_gio_hang 
    DROP CONSTRAINT UQ_chi_tiet_gio_hang_gio_hang_san_pham;
    PRINT N'✅ Đã xóa constraint cũ';
END

-- Thêm constraint mới bao gồm biến thể
IF NOT EXISTS (SELECT * FROM sys.key_constraints WHERE name = 'UQ_chi_tiet_gio_hang_gio_hang_san_pham_kich_co_mau_sac')
BEGIN
    ALTER TABLE dbo.chi_tiet_gio_hang 
    ADD CONSTRAINT UQ_chi_tiet_gio_hang_gio_hang_san_pham_kich_co_mau_sac 
    UNIQUE (gio_hang_id, san_pham_id, kich_co, mau_sac);
    PRINT N'✅ Đã thêm constraint mới với biến thể';
END
ELSE
BEGIN
    PRINT N'⚠️ Constraint mới đã tồn tại';
END

PRINT N'🎉 Hoàn thành cập nhật bảng chi_tiet_gio_hang!';
