-- BƯỚC 1: Kiểm tra cấu trúc bảng hiện tại trước
-- Chạy câu lệnh này để xem các cột đã có:
-- SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
-- FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_NAME = 'chi_tiet_don_hang'
-- ORDER BY ORDINAL_POSITION;

-- BƯỚC 2: Thêm các cột còn thiếu (chỉ thêm những cột chưa có)
-- Kiểm tra từng cột trước khi thêm:

-- Thêm cột don_hang_id nếu chưa có
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'chi_tiet_don_hang' AND COLUMN_NAME = 'don_hang_id')
BEGIN
    ALTER TABLE chi_tiet_don_hang ADD don_hang_id BIGINT;
END

-- Thêm cột san_pham_id nếu chưa có
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'chi_tiet_don_hang' AND COLUMN_NAME = 'san_pham_id')
BEGIN
    ALTER TABLE chi_tiet_don_hang ADD san_pham_id BIGINT;
END

-- Thêm cột gia nếu chưa có
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'chi_tiet_don_hang' AND COLUMN_NAME = 'gia')
BEGIN
    ALTER TABLE chi_tiet_don_hang ADD gia DECIMAL(10,2);
END

-- Thêm cột kich_co nếu chưa có
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'chi_tiet_don_hang' AND COLUMN_NAME = 'kich_co')
BEGIN
    ALTER TABLE chi_tiet_don_hang ADD kich_co NVARCHAR(50);
END

-- Thêm cột mau_sac nếu chưa có
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'chi_tiet_don_hang' AND COLUMN_NAME = 'mau_sac')
BEGIN
    ALTER TABLE chi_tiet_don_hang ADD mau_sac NVARCHAR(50);
END

-- BƯỚC 3: Tạo bảng don_hang nếu chưa có
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'don_hang')
BEGIN
    CREATE TABLE don_hang (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nguoi_dung_id BIGINT,
        ngay_dat_hang DATETIME2 DEFAULT GETDATE(),
        tong_tien DECIMAL(10,2),
        trang_thai NVARCHAR(50) DEFAULT 'CHỜ_XÁC_NHẬN',
        dia_chi_giao_hang NVARCHAR(500),
        phuong_thuc_thanh_toan NVARCHAR(100)
    );
END
ELSE
BEGIN
    -- Nếu bảng đã tồn tại, thêm các cột còn thiếu
    -- Thêm cột nguoi_dung_id nếu chưa có
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'don_hang' AND COLUMN_NAME = 'nguoi_dung_id')
    BEGIN
        ALTER TABLE don_hang ADD nguoi_dung_id BIGINT;
    END

    -- Thêm cột ngay_dat_hang nếu chưa có
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'don_hang' AND COLUMN_NAME = 'ngay_dat_hang')
    BEGIN
        ALTER TABLE don_hang ADD ngay_dat_hang DATETIME2 DEFAULT GETDATE();
    END

    -- Thêm cột tong_tien nếu chưa có
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'don_hang' AND COLUMN_NAME = 'tong_tien')
    BEGIN
        ALTER TABLE don_hang ADD tong_tien DECIMAL(10,2);
    END

    -- Thêm cột trang_thai nếu chưa có
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'don_hang' AND COLUMN_NAME = 'trang_thai')
    BEGIN
        ALTER TABLE don_hang ADD trang_thai NVARCHAR(50) DEFAULT 'CHỜ_XÁC_NHẬN';
    END

    -- Thêm cột dia_chi_giao_hang nếu chưa có
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'don_hang' AND COLUMN_NAME = 'dia_chi_giao_hang')
    BEGIN
        ALTER TABLE don_hang ADD dia_chi_giao_hang NVARCHAR(500);
    END

    -- Thêm cột phuong_thuc_thanh_toan nếu chưa có
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'don_hang' AND COLUMN_NAME = 'phuong_thuc_thanh_toan')
    BEGIN
        ALTER TABLE don_hang ADD phuong_thuc_thanh_toan NVARCHAR(100);
    END
END

-- BƯỚC 4: Thêm foreign key constraints nếu chưa có
-- Kiểm tra và thêm foreign key cho don_hang_id
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
               WHERE CONSTRAINT_NAME = 'FK_chi_tiet_don_hang_don_hang')
BEGIN
    ALTER TABLE chi_tiet_don_hang ADD CONSTRAINT FK_chi_tiet_don_hang_don_hang 
    FOREIGN KEY (don_hang_id) REFERENCES don_hang(id);
END

-- Kiểm tra và thêm foreign key cho san_pham_id
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
               WHERE CONSTRAINT_NAME = 'FK_chi_tiet_don_hang_san_pham')
BEGIN
    ALTER TABLE chi_tiet_don_hang ADD CONSTRAINT FK_chi_tiet_don_hang_san_pham 
    FOREIGN KEY (san_pham_id) REFERENCES san_pham(id);
END
