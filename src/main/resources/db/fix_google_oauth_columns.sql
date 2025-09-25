-- =============================================
-- Script sửa lỗi: Thêm cột Google OAuth2
-- Chạy từng lệnh một, không chạy cả file
-- =============================================

-- BƯỚC 1: Kiểm tra cấu trúc bảng hiện tại
SELECT '=== CẤU TRÚC BẢNG HIỆN TẠI ===' as ThongBao;
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'nguoi_dung'
ORDER BY ORDINAL_POSITION;

-- BƯỚC 2: Thêm cột google_id (chạy riêng lệnh này)
ALTER TABLE nguoi_dung ADD google_id NVARCHAR(255) NULL;

-- BƯỚC 3: Thêm cột provider (chạy riêng lệnh này)
ALTER TABLE nguoi_dung ADD provider NVARCHAR(50) DEFAULT 'local';

-- BƯỚC 4: Thêm cột avatar_url (chạy riêng lệnh này)
ALTER TABLE nguoi_dung ADD avatar_url NVARCHAR(500) NULL;

-- BƯỚC 5: Kiểm tra lại sau khi thêm (chạy riêng lệnh này)
SELECT '=== CẤU TRÚC BẢNG SAU KHI THÊM ===' as ThongBao;
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'nguoi_dung'
ORDER BY ORDINAL_POSITION;

-- BƯỚC 6: Tạo index (chỉ chạy sau khi cột đã tồn tại)
CREATE UNIQUE INDEX IX_nguoi_dung_google_id ON nguoi_dung(google_id) WHERE google_id IS NOT NULL;

-- BƯỚC 7: Cập nhật dữ liệu (chạy riêng lệnh này)
UPDATE nguoi_dung SET provider = 'local' WHERE provider IS NULL;
