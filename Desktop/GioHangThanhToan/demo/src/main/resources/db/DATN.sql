/* =========================================================
 Activewear Store - SQL Server DDL & Seed (DATN) - ONE FILE
 Target: SQL Server 2019+
 Tính chất: Idempotent (chạy nhiều lần không lỗi, không trùng)
========================================================= */

----------------------------------------------------------
-- 0) CREATE DATABASE (nếu chưa có) + (khuyến nghị) Collation
----------------------------------------------------------
IF DB_ID(N'DATN') IS NULL
BEGIN
    CREATE DATABASE DATN;
    PRINT N'✅ Database DATN đã được tạo.';
END
ELSE
BEGIN
    PRINT N'ℹ️ Database DATN đã tồn tại — sẽ sử dụng DB hiện có.';
END
GO

-- (Tùy chọn) Collation tiếng Việt hiện đại. Chạy trước khi tạo bảng.
-- ALTER DATABASE DATN COLLATE Vietnamese_100_CI_AS_SC;
-- GO

USE DATN;
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET NOCOUNT ON;
GO

----------------------------------------------------------
-- 1) TABLES (chỉ tạo nếu chưa tồn tại)
----------------------------------------------------------

IF OBJECT_ID(N'dbo.vai_tro',N'U') IS NULL
BEGIN
CREATE TABLE dbo.vai_tro (
    id           BIGINT IDENTITY(1,1) NOT NULL,
    ten_vai_tro  NVARCHAR(255)  NOT NULL,
    mo_ta        NVARCHAR(255) NULL,
    CONSTRAINT PK_vai_tro PRIMARY KEY CLUSTERED (id),
    CONSTRAINT UQ_vai_tro_ten UNIQUE (ten_vai_tro)
);
PRINT N'✅ Tạo dbo.vai_tro';
END

IF OBJECT_ID(N'dbo.danh_muc',N'U') IS NULL
BEGIN
CREATE TABLE dbo.danh_muc (
    id         BIGINT IDENTITY(1,1) NOT NULL,
    ten        NVARCHAR(255) NOT NULL,
    mo_ta      NVARCHAR(255) NULL,
    hinh_anh   NVARCHAR(500) NULL,
    mau_sac    NVARCHAR(50)  NULL,
    thu_tu     INT           NOT NULL DEFAULT 0,
    hoat_dong  BIT           NOT NULL DEFAULT 1,
    id_cha     BIGINT        NULL,
    ngay_tao   DATETIME2(0)  NOT NULL CONSTRAINT DF_danh_muc_ngay_tao DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_danh_muc PRIMARY KEY CLUSTERED (id),
    CONSTRAINT UQ_danh_muc_ten UNIQUE (ten)
);
PRINT N'✅ Tạo dbo.danh_muc';
END

IF OBJECT_ID(N'dbo.thuong_hieu',N'U') IS NULL
BEGIN
CREATE TABLE dbo.thuong_hieu (
    id        BIGINT IDENTITY(1,1) NOT NULL,
    ten       NVARCHAR(255) NOT NULL,
    ngay_tao  DATETIME2(0)  NOT NULL CONSTRAINT DF_thuong_hieu_ngay_tao DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_thuong_hieu PRIMARY KEY CLUSTERED (id),
    CONSTRAINT UQ_thuong_hieu_ten UNIQUE (ten)
);
PRINT N'✅ Tạo dbo.thuong_hieu';
END

IF OBJECT_ID(N'dbo.nguoi_dung',N'U') IS NULL
BEGIN
CREATE TABLE dbo.nguoi_dung (
    id             BIGINT IDENTITY(1,1) NOT NULL,
    ten            NVARCHAR(255)        NOT NULL,
    email          NVARCHAR(100)        NOT NULL,
    mat_khau       NVARCHAR(255)        NOT NULL,
    so_dien_thoai  NVARCHAR(255)        NULL,
    vai_tro_id     BIGINT               NULL,
    dia_chi        NVARCHAR(255)        NULL,
    gioi_tinh      NVARCHAR(255)        NULL,
    ngay_sinh      DATE                 NULL,
    thanh_pho      NVARCHAR(255)        NULL,
    hoat_dong      BIT                  NOT NULL CONSTRAINT DF_nguoi_dung_hoat_dong DEFAULT (1),
    bi_khoa        BIT                  NOT NULL CONSTRAINT DF_nguoi_dung_bi_khoa DEFAULT (0),
    ly_do_khoa     NVARCHAR(255)        NULL,
    ngay_tao       DATETIME2(0)         NOT NULL CONSTRAINT DF_nguoi_dung_ngay_tao DEFAULT (SYSUTCDATETIME()),
    ngay_cap_nhat  DATETIME2(0)         NOT NULL CONSTRAINT DF_nguoi_dung_ngay_cap_nhat DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_nguoi_dung PRIMARY KEY CLUSTERED (id),
    CONSTRAINT UQ_nguoi_dung_email UNIQUE (email)
);
PRINT N'✅ Tạo dbo.nguoi_dung';
END

-- Thêm cột OAuth (idempotent) - Sửa để tương thích với Hibernate
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N'dbo.nguoi_dung') AND name = N'google_id')
    ALTER TABLE dbo.nguoi_dung ADD google_id NVARCHAR(255) NULL;
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N'dbo.nguoi_dung') AND name = N'provider')
    ALTER TABLE dbo.nguoi_dung ADD provider NVARCHAR(255) NULL;
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N'dbo.nguoi_dung') AND name = N'avatar_url')
    ALTER TABLE dbo.nguoi_dung ADD avatar_url NVARCHAR(255) NULL;
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_nguoi_dung_google_id' AND object_id = OBJECT_ID(N'dbo.nguoi_dung'))
    CREATE UNIQUE INDEX IX_nguoi_dung_google_id ON dbo.nguoi_dung(google_id) WHERE google_id IS NOT NULL;

-- Thêm default constraint cho provider sau khi tạo cột
IF NOT EXISTS (SELECT 1 FROM sys.default_constraints WHERE name = N'DF_nguoi_dung_provider')
    ALTER TABLE dbo.nguoi_dung ADD CONSTRAINT DF_nguoi_dung_provider DEFAULT N'local' FOR provider;

-- Chuẩn hoá dữ liệu provider
UPDATE dbo.nguoi_dung SET provider = ISNULL(provider, N'local') WHERE provider IS NULL;

IF OBJECT_ID(N'dbo.dia_chi',N'U') IS NULL
BEGIN
CREATE TABLE dbo.dia_chi (
    id             BIGINT IDENTITY(1,1) NOT NULL,
    nguoi_dung_id  BIGINT               NOT NULL,
    ho_ten_nhan    NVARCHAR(100)        NULL,
    so_dien_thoai  NVARCHAR(20)         NULL,
    dia_chi        NVARCHAR(255)        NULL,
    tinh_thanh     NVARCHAR(100)        NULL,
    quan_huyen     NVARCHAR(100)        NULL,
    mac_dinh       BIT                  NOT NULL CONSTRAINT DF_dia_chi_mac_dinh DEFAULT (0),
    ngay_tao       DATETIME2(0)         NOT NULL CONSTRAINT DF_dia_chi_ngay_tao DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_dia_chi PRIMARY KEY CLUSTERED (id)
);
PRINT N'✅ Tạo dbo.dia_chi';
END

IF OBJECT_ID(N'dbo.san_pham',N'U') IS NULL
BEGIN
CREATE TABLE dbo.san_pham (
    id              BIGINT IDENTITY(1,1) NOT NULL,
    ma_san_pham     NVARCHAR(50)         NOT NULL,
    ten             NVARCHAR(255)        NOT NULL,
    mo_ta           NVARCHAR(MAX)        NULL,
    gia             DECIMAL(15,2)        NULL,
    gia_goc         DECIMAL(15,2)        NULL,
    anh_chinh       NVARCHAR(255)        NULL,
    so_luong_ton    INT                  NOT NULL CONSTRAINT DF_san_pham_so_luong_ton DEFAULT (0),
    chat_lieu       NVARCHAR(255)        NULL,
    xuat_xu         NVARCHAR(255)        NULL,
    luot_xem        INT                  NOT NULL CONSTRAINT DF_san_pham_luot_xem DEFAULT (0),
    da_ban          INT                  NOT NULL CONSTRAINT DF_san_pham_da_ban DEFAULT (0),
    id_danh_muc     BIGINT               NULL,
    id_thuong_hieu  BIGINT               NULL,
    id_mon_the_thao BIGINT               NULL,
    hoat_dong       BIT                  NOT NULL CONSTRAINT DF_san_pham_hoat_dong DEFAULT (1),
    noi_bat         BIT                  NOT NULL CONSTRAINT DF_san_pham_noi_bat DEFAULT (0),
    ngay_tao        DATETIME2(0)         NOT NULL CONSTRAINT DF_san_pham_ngay_tao DEFAULT (SYSUTCDATETIME()),
    ngay_cap_nhat   DATETIME2(0)         NOT NULL CONSTRAINT DF_san_pham_ngay_cap_nhat DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_san_pham PRIMARY KEY CLUSTERED (id),
    CONSTRAINT UQ_san_pham_ma UNIQUE (ma_san_pham)
);
PRINT N'✅ Tạo dbo.san_pham';
END

IF OBJECT_ID(N'dbo.bien_the_san_pham',N'U') IS NULL
BEGIN
CREATE TABLE dbo.bien_the_san_pham (
    id              BIGINT IDENTITY(1,1) NOT NULL,
    id_san_pham     BIGINT               NOT NULL,
    kich_co         NVARCHAR(255)        NULL,
    mau_sac         NVARCHAR(255)        NULL,
    so_luong        INT                  NULL,
    gia_ban         DECIMAL(15,2)        NULL,
    gia_khuyen_mai  DECIMAL(15,2)        NULL,
    trang_thai      BIT                  NOT NULL CONSTRAINT DF_bien_the_trang_thai DEFAULT (1),
    ngay_tao        DATETIME2(0)         NOT NULL CONSTRAINT DF_bien_the_ngay_tao DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_bien_the_san_pham PRIMARY KEY CLUSTERED (id)
);
PRINT N'✅ Tạo dbo.bien_the_san_pham';
END

IF OBJECT_ID(N'dbo.anh_san_pham',N'U') IS NULL
BEGIN
CREATE TABLE dbo.anh_san_pham (
    id           BIGINT IDENTITY(1,1) NOT NULL,
    id_san_pham  BIGINT               NOT NULL,
    url_anh      NVARCHAR(255)        NOT NULL,
    thu_tu       INT                  NULL,
    ngay_them    DATETIME2(0)         NOT NULL CONSTRAINT DF_anh_san_pham_ngay_them DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_anh_san_pham PRIMARY KEY CLUSTERED (id)
);
PRINT N'✅ Tạo dbo.anh_san_pham';
END

IF OBJECT_ID(N'dbo.gio_hang',N'U') IS NULL
BEGIN
CREATE TABLE dbo.gio_hang (
    id            BIGINT IDENTITY(1,1) NOT NULL,
    nguoi_dung_id BIGINT               NOT NULL,
    ngay_tao      DATETIME2(7)         NOT NULL DEFAULT GETDATE(),
    ngay_cap_nhat DATETIME2(7)         NOT NULL DEFAULT GETDATE(),
    CONSTRAINT PK_gio_hang PRIMARY KEY CLUSTERED (id),
    CONSTRAINT UQ_gio_hang_nguoi_dung UNIQUE (nguoi_dung_id)
);
PRINT N'✅ Tạo dbo.gio_hang';
END

IF OBJECT_ID(N'dbo.chi_tiet_gio_hang',N'U') IS NULL
BEGIN
CREATE TABLE dbo.chi_tiet_gio_hang (
    id           BIGINT IDENTITY(1,1) NOT NULL,
    gio_hang_id  BIGINT               NOT NULL,
    san_pham_id  BIGINT               NOT NULL,
    so_luong     INT                  NOT NULL DEFAULT 1,
    gia          DECIMAL(15,2)        NOT NULL,
    kich_co      NVARCHAR(255)        NULL,
    mau_sac      NVARCHAR(255)        NULL,
    CONSTRAINT PK_chi_tiet_gio_hang PRIMARY KEY CLUSTERED (id),
    CONSTRAINT CK_chi_tiet_gio_hang_so_luong CHECK (so_luong > 0),
    CONSTRAINT CK_chi_tiet_gio_hang_gia CHECK (gia >= 0),
    CONSTRAINT UQ_chi_tiet_gio_hang_gio_hang_san_pham_kich_co_mau_sac UNIQUE (gio_hang_id, san_pham_id, kich_co, mau_sac)
);
PRINT N'✅ Tạo dbo.chi_tiet_gio_hang';
END
ELSE
BEGIN
    -- Đảm bảo có cột biến thể, idempotent
    IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.chi_tiet_gio_hang') AND name = 'kich_co')
        ALTER TABLE dbo.chi_tiet_gio_hang ADD kich_co NVARCHAR(20) NULL;
    IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.chi_tiet_gio_hang') AND name = 'mau_sac')
        ALTER TABLE dbo.chi_tiet_gio_hang ADD mau_sac NVARCHAR(50) NULL;

    -- Xoá unique cũ (nếu có) và tạo unique mới theo biến thể
    IF EXISTS (SELECT * FROM sys.key_constraints WHERE name = 'UQ_chi_tiet_gio_hang_gio_hang_san_pham')
    BEGIN
        ALTER TABLE dbo.chi_tiet_gio_hang DROP CONSTRAINT UQ_chi_tiet_gio_hang_gio_hang_san_pham;
    END
    IF NOT EXISTS (SELECT * FROM sys.key_constraints WHERE name = 'UQ_chi_tiet_gio_hang_gio_hang_san_pham_kich_co_mau_sac')
    BEGIN
        ALTER TABLE dbo.chi_tiet_gio_hang ADD CONSTRAINT UQ_chi_tiet_gio_hang_gio_hang_san_pham_kich_co_mau_sac
        UNIQUE (gio_hang_id, san_pham_id, kich_co, mau_sac);
    END
END

IF OBJECT_ID(N'dbo.yeu_thich',N'U') IS NULL
BEGIN
CREATE TABLE dbo.yeu_thich (
    id            BIGINT IDENTITY(1,1) NOT NULL,
    nguoi_dung_id BIGINT               NOT NULL,
    san_pham_id   BIGINT               NOT NULL,
    ngay_tao      DATETIME2(7)         NOT NULL DEFAULT GETDATE(),
    CONSTRAINT PK_yeu_thich PRIMARY KEY CLUSTERED (id),
    CONSTRAINT UQ_yeu_thich_nguoi_dung_san_pham UNIQUE (nguoi_dung_id, san_pham_id)
);
PRINT N'✅ Tạo dbo.yeu_thich';
END

IF OBJECT_ID(N'dbo.don_hang',N'U') IS NULL
BEGIN
CREATE TABLE dbo.don_hang (
    id                       BIGINT IDENTITY(1,1) NOT NULL,
    ma_don_hang              NVARCHAR(50)         NOT NULL,
    nguoi_dung_id            BIGINT               NOT NULL,
    tong_tien                DECIMAL(15,2)        NULL,
    phi_van_chuyen           DECIMAL(10,2)        NULL,
    tong_thanh_toan          DECIMAL(10,2)        NULL,
    ten_nguoi_nhan           NVARCHAR(100)        NULL,
    so_dien_thoai_nhan       NVARCHAR(20)         NULL,
    dia_chi_giao_hang        NVARCHAR(255)        NULL,
    trang_thai               NVARCHAR(255)        NOT NULL CONSTRAINT DF_don_hang_trang_thai DEFAULT (N'CHO_XAC_NHAN'),
    phuong_thuc_thanh_toan   NVARCHAR(50)         NOT NULL CONSTRAINT DF_don_hang_pttt DEFAULT (N'COD'),
    da_thanh_toan            BIT                  NOT NULL CONSTRAINT DF_don_hang_da_thanh_toan DEFAULT (0),
    ghi_chu                  NVARCHAR(255)        NULL,
    ngay_tao                 DATETIME2(0)         NOT NULL CONSTRAINT DF_don_hang_ngay_tao DEFAULT (SYSUTCDATETIME()),
    ngay_cap_nhat            DATETIME2(0)         NOT NULL CONSTRAINT DF_don_hang_ngay_cap_nhat DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_don_hang PRIMARY KEY CLUSTERED (id),
    CONSTRAINT UQ_don_hang_ma UNIQUE (ma_don_hang)
);
PRINT N'✅ Tạo dbo.don_hang';
END

IF OBJECT_ID(N'dbo.chi_tiet_don_hang',N'U') IS NULL
BEGIN
CREATE TABLE dbo.chi_tiet_don_hang (
    id             BIGINT IDENTITY(1,1) NOT NULL,
    don_hang_id    BIGINT               NOT NULL,
    san_pham_id    BIGINT               NOT NULL,
    so_luong       INT                  NULL,
    gia            DECIMAL(15,2)        NULL,
    thanh_tien     DECIMAL(15,2)        NULL,
    kich_co        NVARCHAR(255)        NULL,
    mau_sac        NVARCHAR(255)        NULL,
    CONSTRAINT PK_chi_tiet_don_hang PRIMARY KEY CLUSTERED (id)
);
PRINT N'✅ Tạo dbo.chi_tiet_don_hang';
END
ELSE
BEGIN
    -- Đảm bảo các cột tuỳ chọn tồn tại (idempotent)
    IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.chi_tiet_don_hang') AND name = 'don_hang_id')
        ALTER TABLE dbo.chi_tiet_don_hang ADD don_hang_id BIGINT NULL;
    IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.chi_tiet_don_hang') AND name = 'san_pham_id')
        ALTER TABLE dbo.chi_tiet_don_hang ADD san_pham_id BIGINT NULL;
    IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.chi_tiet_don_hang') AND name = 'gia')
        ALTER TABLE dbo.chi_tiet_don_hang ADD gia DECIMAL(15,2) NULL;
    IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.chi_tiet_don_hang') AND name = 'kich_co')
        ALTER TABLE dbo.chi_tiet_don_hang ADD kich_co NVARCHAR(50) NULL;
    IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.chi_tiet_don_hang') AND name = 'mau_sac')
        ALTER TABLE dbo.chi_tiet_don_hang ADD mau_sac NVARCHAR(50) NULL;
    IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.chi_tiet_don_hang') AND name = 'thanh_tien')
        ALTER TABLE dbo.chi_tiet_don_hang ADD thanh_tien DECIMAL(15,2) NULL;
END

IF OBJECT_ID(N'dbo.thanh_toan',N'U') IS NULL
BEGIN
CREATE TABLE dbo.thanh_toan (
    id            BIGINT IDENTITY(1,1) NOT NULL,
    id_don_hang   BIGINT               NOT NULL,
    loai          NVARCHAR(50)         NULL, -- VNPAY, MOMO, PAYPAL...
    ma_giao_dich  NVARCHAR(100)        NULL,
    so_tien       DECIMAL(15,2)        NULL,
    trang_thai    NVARCHAR(30)         NULL,
    ngay_tao      DATETIME2(0)         NOT NULL CONSTRAINT DF_thanh_toan_ngay_tao DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_thanh_toan PRIMARY KEY CLUSTERED (id)
);
PRINT N'✅ Tạo dbo.thanh_toan';
END

IF OBJECT_ID(N'dbo.danh_gia',N'U') IS NULL
BEGIN
CREATE TABLE dbo.danh_gia (
    id            BIGINT IDENTITY(1,1) NOT NULL,
    nguoi_dung_id BIGINT               NOT NULL,
    id_san_pham   BIGINT               NOT NULL,
    so_sao        INT                  NULL,
    noi_dung      NVARCHAR(500)        NULL,
    ngay_danh_gia DATETIME2(0)         NOT NULL CONSTRAINT DF_danh_gia_ngay DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_danh_gia PRIMARY KEY CLUSTERED (id)
);
PRINT N'✅ Tạo dbo.danh_gia';
END

IF OBJECT_ID(N'dbo.khuyen_mai',N'U') IS NULL
BEGIN
CREATE TABLE dbo.khuyen_mai (
    id             BIGINT IDENTITY(1,1) NOT NULL,
    ma_khuyen_mai  NVARCHAR(50)         NULL,
    ten            NVARCHAR(100)        NULL,
    mo_ta          NVARCHAR(255)        NULL,
    gia_tri        DECIMAL(15,2)        NULL,
    loai           NVARCHAR(20)         NULL, -- 'PERCENT' hoặc 'VND'
    ngay_bat_dau   DATETIME2(0)         NULL,
    ngay_ket_thuc  DATETIME2(0)         NULL,
    dieu_kien      NVARCHAR(255)        NULL,
    hoat_dong      BIT                  NOT NULL CONSTRAINT DF_khuyen_mai_hoat_dong DEFAULT (1),
    CONSTRAINT PK_khuyen_mai PRIMARY KEY CLUSTERED (id)
);
PRINT N'✅ Tạo dbo.khuyen_mai';
END

IF OBJECT_ID(N'dbo.ma_giam_gia',N'U') IS NULL
BEGIN
CREATE TABLE dbo.ma_giam_gia (
    id               BIGINT IDENTITY(1,1) NOT NULL,
    id_khuyen_mai    BIGINT               NOT NULL,
    ma_code          NVARCHAR(50)         NULL,
    so_luong         INT                  NULL,
    so_luong_da_dung INT                  NULL,
    ngay_tao         DATETIME2(0)         NOT NULL CONSTRAINT DF_ma_giam_gia_ngay_tao DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_ma_giam_gia PRIMARY KEY CLUSTERED (id)
);
PRINT N'✅ Tạo dbo.ma_giam_gia';
END

IF OBJECT_ID(N'dbo.thong_bao',N'U') IS NULL
BEGIN
CREATE TABLE dbo.thong_bao (
    id            BIGINT IDENTITY(1,1) NOT NULL,
    tieu_de       NVARCHAR(200)        NULL,
    noi_dung      NVARCHAR(MAX)        NULL,
    loai          NVARCHAR(50)         NULL,
    da_doc        BIT                  NOT NULL CONSTRAINT DF_thong_bao_da_doc DEFAULT (0),
    nguoi_dung_id BIGINT               NULL,
    ngay_tao      DATETIME2(0)         NOT NULL CONSTRAINT DF_thong_bao_ngay_tao DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_thong_bao PRIMARY KEY CLUSTERED (id)
);
PRINT N'✅ Tạo dbo.thong_bao';
END

IF OBJECT_ID(N'dbo.nhat_ky_admin',N'U') IS NULL
BEGIN
CREATE TABLE dbo.nhat_ky_admin (
    id        BIGINT IDENTITY(1,1) NOT NULL,
    id_admin  BIGINT               NOT NULL,
    hanh_dong NVARCHAR(100)        NULL,
    noi_dung  NVARCHAR(MAX)        NULL,
    ngay_tao  DATETIME2(0)         NOT NULL CONSTRAINT DF_nhat_ky_admin_ngay_tao DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_nhat_ky_admin PRIMARY KEY CLUSTERED (id)
);
PRINT N'✅ Tạo dbo.nhat_ky_admin';
END

IF OBJECT_ID(N'dbo.otp_token',N'U') IS NULL
BEGIN
CREATE TABLE dbo.otp_token (
    id          BIGINT IDENTITY(1,1) NOT NULL,
    email       NVARCHAR(255)        NOT NULL,
    otp_code    NVARCHAR(10)         NOT NULL,
    expiry_time DATETIME2(0)         NOT NULL,
    used        BIT                  NOT NULL CONSTRAINT DF_otp_token_used DEFAULT (0),
    created_at  DATETIME2(0)         NOT NULL CONSTRAINT DF_otp_token_created_at DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_otp_token PRIMARY KEY CLUSTERED (id)
);
PRINT N'✅ Tạo dbo.otp_token';
END
GO

----------------------------------------------------------
-- 2) FOREIGN KEYS (chỉ tạo nếu chưa có)
----------------------------------------------------------
-- nguoi_dung -> vai_tro
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_nguoi_dung_vai_tro')
    ALTER TABLE dbo.nguoi_dung
    ADD CONSTRAINT FK_nguoi_dung_vai_tro FOREIGN KEY (vai_tro_id) REFERENCES dbo.vai_tro(id);

-- danh_muc cha
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_danh_muc_cha')
    ALTER TABLE dbo.danh_muc
    ADD CONSTRAINT FK_danh_muc_cha FOREIGN KEY (id_cha) REFERENCES dbo.danh_muc(id);

-- dia_chi -> nguoi_dung
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_dia_chi_nguoi_dung')
    ALTER TABLE dbo.dia_chi
    ADD CONSTRAINT FK_dia_chi_nguoi_dung FOREIGN KEY (nguoi_dung_id) REFERENCES dbo.nguoi_dung(id) ON DELETE CASCADE;

-- san_pham -> danh_muc, thuong_hieu
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_san_pham_danh_muc')
    ALTER TABLE dbo.san_pham
    ADD CONSTRAINT FK_san_pham_danh_muc FOREIGN KEY (id_danh_muc) REFERENCES dbo.danh_muc(id);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_san_pham_thuong_hieu')
    ALTER TABLE dbo.san_pham
    ADD CONSTRAINT FK_san_pham_thuong_hieu FOREIGN KEY (id_thuong_hieu) REFERENCES dbo.thuong_hieu(id);

-- bien_the_san_pham -> san_pham
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_bien_the_san_pham_san_pham')
    ALTER TABLE dbo.bien_the_san_pham
    ADD CONSTRAINT FK_bien_the_san_pham_san_pham FOREIGN KEY (id_san_pham) REFERENCES dbo.san_pham(id) ON DELETE CASCADE;

-- anh_san_pham -> san_pham
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_anh_san_pham_san_pham')
    ALTER TABLE dbo.anh_san_pham
    ADD CONSTRAINT FK_anh_san_pham_san_pham FOREIGN KEY (id_san_pham) REFERENCES dbo.san_pham(id) ON DELETE CASCADE;

-- gio_hang -> nguoi_dung
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_gio_hang_nguoi_dung')
    ALTER TABLE dbo.gio_hang
    ADD CONSTRAINT FK_gio_hang_nguoi_dung FOREIGN KEY (nguoi_dung_id) REFERENCES dbo.nguoi_dung(id) ON DELETE CASCADE;

-- chi_tiet_gio_hang -> gio_hang, san_pham
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_chi_tiet_gio_hang_gio_hang')
    ALTER TABLE dbo.chi_tiet_gio_hang
    ADD CONSTRAINT FK_chi_tiet_gio_hang_gio_hang FOREIGN KEY (gio_hang_id) REFERENCES dbo.gio_hang(id) ON DELETE CASCADE;
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_chi_tiet_gio_hang_san_pham')
    ALTER TABLE dbo.chi_tiet_gio_hang
    ADD CONSTRAINT FK_chi_tiet_gio_hang_san_pham FOREIGN KEY (san_pham_id) REFERENCES dbo.san_pham(id) ON DELETE CASCADE;

-- don_hang -> nguoi_dung
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_don_hang_nguoi_dung')
    ALTER TABLE dbo.don_hang
    ADD CONSTRAINT FK_don_hang_nguoi_dung FOREIGN KEY (nguoi_dung_id) REFERENCES dbo.nguoi_dung(id);

-- chi_tiet_don_hang -> don_hang, san_pham
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_ctdh_don_hang')
    ALTER TABLE dbo.chi_tiet_don_hang
    ADD CONSTRAINT FK_ctdh_don_hang FOREIGN KEY (don_hang_id) REFERENCES dbo.don_hang(id);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_ctdh_san_pham')
    ALTER TABLE dbo.chi_tiet_don_hang
    ADD CONSTRAINT FK_ctdh_san_pham FOREIGN KEY (san_pham_id) REFERENCES dbo.san_pham(id);

-- thanh_toan -> don_hang
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_thanh_toan_don_hang')
    ALTER TABLE dbo.thanh_toan
    ADD CONSTRAINT FK_thanh_toan_don_hang FOREIGN KEY (id_don_hang) REFERENCES dbo.don_hang(id);

-- danh_gia -> nguoi_dung, san_pham
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_danh_gia_nguoi_dung')
    ALTER TABLE dbo.danh_gia
    ADD CONSTRAINT FK_danh_gia_nguoi_dung FOREIGN KEY (nguoi_dung_id) REFERENCES dbo.nguoi_dung(id) ON DELETE CASCADE;
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_danh_gia_san_pham')
    ALTER TABLE dbo.danh_gia
    ADD CONSTRAINT FK_danh_gia_san_pham FOREIGN KEY (id_san_pham) REFERENCES dbo.san_pham(id) ON DELETE CASCADE;

-- ma_giam_gia -> khuyen_mai
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_ma_giam_gia_khuyen_mai')
    ALTER TABLE dbo.ma_giam_gia
    ADD CONSTRAINT FK_ma_giam_gia_khuyen_mai FOREIGN KEY (id_khuyen_mai) REFERENCES dbo.khuyen_mai(id);

PRINT N'🔗 Hoàn tất tạo/cập nhật các khóa ngoại.';
GO

----------------------------------------------------------
-- 3) SEED DATA (idempotent, không trùng khóa)
----------------------------------------------------------
SET XACT_ABORT ON;
BEGIN TRY
BEGIN TRAN;

-- 3.1 Vai trò
MERGE dbo.vai_tro AS T
USING (VALUES
    (N'Admin',       N'Quản trị viên hệ thống'),
    (N'Khách hàng',  N'Khách hàng mua hàng'),
    (N'Nhân viên',   N'Nhân viên bán hàng')
) AS S(ten_vai_tro, mo_ta)
ON T.ten_vai_tro = S.ten_vai_tro
WHEN MATCHED THEN UPDATE SET mo_ta = S.mo_ta
WHEN NOT MATCHED THEN INSERT (ten_vai_tro, mo_ta) VALUES (S.ten_vai_tro, S.mo_ta);

-- 3.2 Danh mục cha
;WITH parents AS (
    SELECT * FROM (VALUES
        (N'Áo thun', N'Áo thun thể thao nam nữ'),
        (N'Quần short', N'Quần short thể thao'),
        (N'Giày thể thao', N'Giày chạy bộ, tập gym'),
        (N'Phụ kiện', N'Phụ kiện thể thao'),
        (N'Đồ tập nữ', N'Trang phục tập luyện nữ'),
        (N'Đồ tập nam', N'Trang phục tập luyện nam')
    ) v(ten, mo_ta)
)
MERGE dbo.danh_muc AS T
USING parents AS S
ON T.ten = S.ten
WHEN MATCHED THEN UPDATE SET mo_ta = S.mo_ta
WHEN NOT MATCHED THEN INSERT (ten, mo_ta) VALUES (S.ten, S.mo_ta);

-- 3.3 Danh mục con
;WITH child AS (
    SELECT * FROM (VALUES
        (N'Áo thun nam',  N'Áo thun thể thao nam', N'Áo thun'),
        (N'Áo thun nữ',   N'Áo thun thể thao nữ', N'Áo thun'),
        (N'Áo tank top',  N'Áo ba lỗ tập gym',    N'Áo thun'),
        (N'Quần short nam', N'Quần short thể thao nam', N'Quần short'),
        (N'Quần short nữ',  N'Quần short thể thao nữ',  N'Quần short'),
        (N'Quần legging',   N'Quần legging tập yoga',   N'Quần short'),
        (N'Giày chạy bộ',   N'Giày chạy bộ nam nữ',     N'Giày thể thao'),
        (N'Giày tập gym',   N'Giày tập gym, cử tạ',     N'Giày thể thao'),
        (N'Giày bóng đá',   N'Giày bóng đá chuyên nghiệp', N'Giày thể thao')
    ) v(ten, mo_ta, ten_cha)
)
MERGE dbo.danh_muc AS T
USING (
    SELECT c.ten, c.mo_ta, p.id AS id_cha
    FROM child c
    JOIN dbo.danh_muc p ON p.ten = c.ten_cha
) AS S
ON T.ten = S.ten
WHEN MATCHED THEN UPDATE SET mo_ta = S.mo_ta, id_cha = S.id_cha
WHEN NOT MATCHED THEN INSERT (ten, mo_ta, id_cha) VALUES (S.ten, S.mo_ta, S.id_cha);

-- 3.4 Thương hiệu
MERGE dbo.thuong_hieu AS T
USING (VALUES
    (N'Nike'),(N'Adidas'),(N'Puma'),(N'Under Armour'),(N'Reebok'),
    (N'New Balance'),(N'Converse'),(N'Vans'),(N'Champion'),(N'Fila')
) AS S(ten)
ON T.ten = S.ten
WHEN NOT MATCHED THEN INSERT (ten) VALUES (S.ten);

-- 3.5 Người dùng (key = email)
MERGE dbo.nguoi_dung AS T
USING (VALUES
    (N'Admin User', N'admin@activewear.com', N'admin123', N'0123456789', N'Admin',       N'123 Đường ABC, Quận 1, TP.HCM', N'Nam', N'1990-01-01', N'TP.HCM'),
    (N'Admin LV', N'thelvph50187@gmail.com', N'admin123', N'0123456789', N'Admin', N'456 Đường Admin, Quận 1, TP.HCM', N'Nam', N'1990-01-01', N'TP.HCM'),
    (N'Nguyễn Văn A', N'nguyenvana@gmail.com', N'123456', N'0987654321', N'Khách hàng', N'456 Đường XYZ, Quận 2, TP.HCM', N'Nam', N'1995-05-15', N'TP.HCM'),
    (N'Trần Thị B',   N'tranthib@gmail.com',  N'123456', N'0912345678', N'Khách hàng', N'789 Đường DEF, Quận 3, TP.HCM', N'Nữ',  N'1998-08-20', N'TP.HCM')
) AS S(ten, email, mat_khau, sdt, ten_vai_tro, dia_chi, gioi_tinh, ngay_sinh, thanh_pho)
ON T.email = S.email
WHEN MATCHED THEN UPDATE SET
    ten = S.ten,
    so_dien_thoai = S.sdt,
    vai_tro_id = (SELECT id FROM dbo.vai_tro WHERE ten_vai_tro = S.ten_vai_tro),
    dia_chi = S.dia_chi,
    gioi_tinh = S.gioi_tinh,
    ngay_sinh = TRY_CONVERT(date, S.ngay_sinh),
    thanh_pho = S.thanh_pho,
    ngay_cap_nhat = SYSUTCDATETIME()
WHEN NOT MATCHED THEN INSERT
    (ten, email, mat_khau, so_dien_thoai, vai_tro_id, dia_chi, gioi_tinh, ngay_sinh, thanh_pho, hoat_dong, bi_khoa, ngay_tao, ngay_cap_nhat, provider)
VALUES
    (S.ten, S.email, S.mat_khau, S.sdt,
     (SELECT id FROM dbo.vai_tro WHERE ten_vai_tro = S.ten_vai_tro),
     S.dia_chi, S.gioi_tinh, TRY_CONVERT(date, S.ngay_sinh), S.thanh_pho, 1, 0, SYSUTCDATETIME(), SYSUTCDATETIME(), N'local');

-- 3.6 Sản phẩm
IF OBJECT_ID(N'tempdb..#SP') IS NOT NULL DROP TABLE #SP;
CREATE TABLE #SP(
    ma NVARCHAR(50), ten NVARCHAR(150), mo_ta NVARCHAR(MAX),
    gia DECIMAL(15,2), gia_goc DECIMAL(15,2), anh NVARCHAR(255),
    so_luong INT, chat_lieu NVARCHAR(100), xuat_xu NVARCHAR(50),
    luot_xem INT, da_ban INT, ten_danh_muc NVARCHAR(100), ten_thuong_hieu NVARCHAR(100),
    hoat_dong BIT, noi_bat BIT
);

INSERT INTO #SP VALUES
(N'SP001', N'Áo thun Nike Dri-FIT', N'Áo thun thể thao Nike Dri-FIT với công nghệ thấm hút mồ hôi vượt trội, phù hợp cho tập luyện và hoạt động thể thao hàng ngày.', 450000, 500000, N'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', 50, N'Polyester', N'Việt Nam', 120, 25, N'Áo thun nam', N'Nike', 1, 1),
(N'SP002', N'Quần short Adidas 3-Stripes', N'Quần short thể thao Adidas với thiết kế 3 sọc đặc trưng, chất liệu thoáng mát, co giãn tốt.', 380000, 420000, N'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', 30, N'Polyester + Spandex', N'Việt Nam', 95, 18, N'Quần short nam', N'Adidas', 1, 1),
(N'SP003', N'Giày Nike Air Max 270', N'Giày thể thao Nike Air Max 270 với đế Air Max lớn, mang lại cảm giác êm ái và hỗ trợ tối đa cho bàn chân.', 2500000, 2800000, N'https://images.unsplash.com/photo-1542291026-7eec264c27ff?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', 15, N'Mesh + Synthetic', N'Việt Nam', 200, 12, N'Giày chạy bộ', N'Nike', 1, 1),
(N'SP004', N'Áo tank top Puma Essential', N'Áo ba lỗ Puma Essential với thiết kế đơn giản, chất liệu mềm mại, thoáng mát.', 320000, 350000, N'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', 40, N'Cotton + Polyester', N'Việt Nam', 45, 8, N'Áo tank top', N'Puma', 1, 0),
(N'SP005', N'Quần legging nữ Under Armour', N'Quần legging nữ Under Armour với công nghệ HeatGear, thấm hút mồ hôi và khô nhanh.', 650000, 700000, N'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', 25, N'Polyester + Elastane', N'Việt Nam', 30, 5, N'Quần legging', N'Under Armour', 1, 0),
(N'SP006', N'Giày Adidas Ultraboost 22', N'Giày chạy bộ Adidas Ultraboost 22 với công nghệ Boost, mang lại năng lượng trả về tối đa.', 3200000, 3500000, N'https://images.unsplash.com/photo-1549298916-b41d501d3772?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', 8, N'Primeknit + Boost', N'Việt Nam', 80, 3, N'Giày chạy bộ', N'Adidas', 1, 0),
(N'SP007', N'Áo thun nữ Reebok Classic', N'Áo thun nữ Reebok Classic với thiết kế cổ điển, chất liệu cotton mềm mại.', 280000, 300000, N'https://images.unsplash.com/photo-1571945153237-09f5e4a2c5c3?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', 60, N'100% Cotton', N'Việt Nam', 150, 35, N'Áo thun nữ', N'Reebok', 1, 0),
(N'SP008', N'Quần short nam New Balance', N'Quần short nam New Balance với thiết kế năng động, chất liệu thoáng mát.', 350000, 380000, N'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', 45, N'Polyester + Cotton', N'Việt Nam', 110, 28, N'Quần short nam', N'New Balance', 1, 0),
(N'SP009', N'Giày Converse Chuck Taylor', N'Giày Converse Chuck Taylor All Star với thiết kế cổ điển, phù hợp mọi phong cách.', 1200000, 1300000, N'https://images.unsplash.com/photo-1549298916-b41d501d3772?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', 20, N'Canvas + Rubber', N'Việt Nam', 180, 22, N'Giày chạy bộ', N'Converse', 1, 0),
(N'SP010', N'Áo thun Champion Reverse Weave', N'Áo thun Champion Reverse Weave với công nghệ Reverse Weave, không bị co rút sau khi giặt.', 400000, 500000, N'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', 35, N'Cotton + Polyester', N'Việt Nam', 75, 15, N'Áo thun nam', N'Champion', 1, 0),
(N'SP011', N'Quần short nữ Fila Heritage', N'Quần short nữ Fila Heritage với thiết kế retro, chất liệu mềm mại.', 250000, 320000, N'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', 28, N'Polyester + Cotton', N'Việt Nam', 55, 12, N'Quần short nữ', N'Fila', 1, 0),
(N'SP012', N'Giày Vans Old Skool', N'Giày Vans Old Skool với thiết kế skateboard cổ điển, chất liệu bền bỉ.', 1800000, 2200000, N'https://images.unsplash.com/photo-1549298916-b41d501d3772?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', 12, N'Canvas + Suede', N'Việt Nam', 90, 8, N'Giày chạy bộ', N'Vans', 1, 0);

MERGE dbo.san_pham AS T
USING (
    SELECT sp.*, dm.id AS id_dm, th.id AS id_th
    FROM #SP sp
    LEFT JOIN dbo.danh_muc dm ON dm.ten = sp.ten_danh_muc
    LEFT JOIN dbo.thuong_hieu th ON th.ten = sp.ten_thuong_hieu
) AS S
ON T.ma_san_pham = S.ma
WHEN MATCHED THEN UPDATE SET
    ten = S.ten,
    mo_ta = S.mo_ta,
    gia = S.gia,
    gia_goc = S.gia_goc,
    anh_chinh = S.anh,
    so_luong_ton = S.so_luong,
    chat_lieu = S.chat_lieu,
    xuat_xu = S.xuat_xu,
    luot_xem = S.luot_xem,
    da_ban = S.da_ban,
    id_danh_muc = S.id_dm,
    id_thuong_hieu = S.id_th,
    hoat_dong = S.hoat_dong,
    noi_bat = S.noi_bat,
    ngay_cap_nhat = SYSUTCDATETIME()
WHEN NOT MATCHED THEN INSERT
    (ma_san_pham, ten, mo_ta, gia, gia_goc, anh_chinh, so_luong_ton, chat_lieu, xuat_xu, luot_xem, da_ban, id_danh_muc, id_thuong_hieu, hoat_dong, noi_bat, ngay_tao, ngay_cap_nhat)
VALUES
    (S.ma, S.ten, S.mo_ta, S.gia, S.gia_goc, S.anh, S.so_luong, S.chat_lieu, S.xuat_xu, S.luot_xem, S.da_ban, S.id_dm, S.id_th, S.hoat_dong, S.noi_bat, SYSUTCDATETIME(), SYSUTCDATETIME());

-- 3.7 Biến thể
IF OBJECT_ID(N'tempdb..#BT') IS NOT NULL DROP TABLE #BT;
CREATE TABLE #BT(ma NVARCHAR(50), kich_co NVARCHAR(50), mau NVARCHAR(50), so_luong INT, gia_ban DECIMAL(15,2));

INSERT INTO #BT VALUES
-- SP001
(N'SP001',N'S',N'Đen',10,450000),(N'SP001',N'M',N'Đen',15,450000),(N'SP001',N'L',N'Đen',12,450000),(N'SP001',N'XL',N'Đen',8,450000),
(N'SP001',N'S',N'Trắng',10,450000),(N'SP001',N'M',N'Trắng',15,450000),(N'SP001',N'L',N'Trắng',12,450000),(N'SP001',N'XL',N'Trắng',8,450000),
-- SP002
(N'SP002',N'S',N'Đen',8,380000),(N'SP002',N'M',N'Đen',12,380000),(N'SP002',N'L',N'Đen',10,380000),
(N'SP002',N'S',N'Xanh navy',8,380000),(N'SP002',N'M',N'Xanh navy',12,380000),(N'SP002',N'L',N'Xanh navy',10,380000),
-- SP003
(N'SP003',N'39',N'Trắng',3,2500000),(N'SP003',N'40',N'Trắng',4,2500000),(N'SP003',N'41',N'Trắng',5,2500000),(N'SP003',N'42',N'Trắng',3,2500000),
(N'SP003',N'39',N'Đen',3,2500000),(N'SP003',N'40',N'Đen',4,2500000),(N'SP003',N'41',N'Đen',5,2500000),(N'SP003',N'42',N'Đen',3,2500000);

;WITH V AS (
    SELECT sp.id AS id_sp, bt.kich_co, bt.mau, bt.so_luong, bt.gia_ban
    FROM #BT bt
    JOIN dbo.san_pham sp ON sp.ma_san_pham = bt.ma
)
MERGE dbo.bien_the_san_pham AS T
USING V AS S
ON T.id_san_pham = S.id_sp AND ISNULL(T.kich_co,N'') = ISNULL(S.kich_co,N'') AND ISNULL(T.mau_sac,N'') = ISNULL(S.mau,N'')
WHEN MATCHED THEN UPDATE SET
    so_luong = S.so_luong,
    gia_ban = S.gia_ban,
    gia_khuyen_mai = NULL,
    trang_thai = 1
WHEN NOT MATCHED THEN INSERT
    (id_san_pham, kich_co, mau_sac, so_luong, gia_ban, gia_khuyen_mai, trang_thai, ngay_tao)
VALUES
    (S.id_sp, S.kich_co, S.mau, S.so_luong, S.gia_ban, NULL, 1, SYSUTCDATETIME());

-- 3.8 Ảnh sản phẩm
IF OBJECT_ID(N'tempdb..#IMG') IS NOT NULL DROP TABLE #IMG;
CREATE TABLE #IMG(ma NVARCHAR(50), url NVARCHAR(255), thu_tu INT);

INSERT INTO #IMG VALUES
-- SP001
(N'SP001',N'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80',1),
(N'SP001',N'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80',2),
(N'SP001',N'https://images.unsplash.com/photo-1571945153237-09f5e4a2c5c3?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80',3),
-- SP002
(N'SP002',N'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80',1),
(N'SP002',N'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80',2),
-- SP003
(N'SP003',N'https://images.unsplash.com/photo-1542291026-7eec264c27ff?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80',1),
(N'SP003',N'https://images.unsplash.com/photo-1549298916-b41d501d3772?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80',2),
(N'SP003',N'https://images.unsplash.com/photo-1549298916-b41d501d3772?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80',3);

INSERT INTO dbo.anh_san_pham (id_san_pham, url_anh, thu_tu, ngay_them)
SELECT sp.id, i.url, i.thu_tu, SYSUTCDATETIME()
FROM #IMG i
JOIN dbo.san_pham sp ON sp.ma_san_pham = i.ma
WHERE NOT EXISTS (
    SELECT 1 FROM dbo.anh_san_pham a
    WHERE a.id_san_pham = sp.id AND a.url_anh = i.url
);

COMMIT;
PRINT N'🎉 Seed dữ liệu mẫu hoàn tất.';
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK;
    DECLARE @msg NVARCHAR(4000) = ERROR_MESSAGE();
    RAISERROR(N'❌ Lỗi seed dữ liệu: %s', 16, 1, @msg);
END CATCH
GO

----------------------------------------------------------
-- 4) TỔNG KẾT
----------------------------------------------------------
DECLARE @n_vt INT = (SELECT COUNT(*) FROM dbo.vai_tro);
DECLARE @n_dm INT = (SELECT COUNT(*) FROM dbo.danh_muc);
DECLARE @n_th INT = (SELECT COUNT(*) FROM dbo.thuong_hieu);
DECLARE @n_nd INT = (SELECT COUNT(*) FROM dbo.nguoi_dung);
DECLARE @n_sp INT = (SELECT COUNT(*) FROM dbo.san_pham);
DECLARE @n_bt INT = (SELECT COUNT(*) FROM dbo.bien_the_san_pham);
DECLARE @n_img INT = (SELECT COUNT(*) FROM dbo.anh_san_pham);

PRINT N'📊 Tổng kết hiện tại:';
PRINT N'   - Vai trò: ' + CAST(@n_vt AS NVARCHAR(20));
PRINT N'   - Danh mục: ' + CAST(@n_dm AS NVARCHAR(20));
PRINT N'   - Thương hiệu: ' + CAST(@n_th AS NVARCHAR(20));
PRINT N'   - Người dùng: ' + CAST(@n_nd AS NVARCHAR(20));
PRINT N'   - Sản phẩm: ' + CAST(@n_sp AS NVARCHAR(20));
PRINT N'   - Biến thể sản phẩm: ' + CAST(@n_bt AS NVARCHAR(20));
PRINT N'   - Ảnh sản phẩm: ' + CAST(@n_img AS NVARCHAR(20));
PRINT N'✅ Hoàn tất tạo/cập nhật schema + dữ liệu mẫu cho DATN.';

----------------------------------------------------------
-- 5) HIBERNATE COMPATIBILITY FIXES
----------------------------------------------------------
PRINT N'🔧 Áp dụng các sửa đổi tương thích với Hibernate...';

-- Sửa lỗi constraint migration cho Hibernate
-- Drop và recreate constraints để tránh xung đột khi Hibernate alter columns

-- 5.1 Fix chi_tiet_gio_hang constraints
IF EXISTS (SELECT * FROM sys.key_constraints WHERE name = 'UQ_chi_tiet_gio_hang_gio_hang_san_pham_kich_co_mau_sac')
BEGIN
    ALTER TABLE dbo.chi_tiet_gio_hang DROP CONSTRAINT UQ_chi_tiet_gio_hang_gio_hang_san_pham_kich_co_mau_sac;
    PRINT N'✅ Dropped UQ_chi_tiet_gio_hang_gio_hang_san_pham_kich_co_mau_sac';
END

-- 5.2 Fix don_hang constraints  
IF EXISTS (SELECT * FROM sys.default_constraints WHERE name = 'DF_don_hang_trang_thai')
BEGIN
    ALTER TABLE dbo.don_hang DROP CONSTRAINT DF_don_hang_trang_thai;
    PRINT N'✅ Dropped DF_don_hang_trang_thai';
END

-- 5.3 Fix nguoi_dung constraints
IF EXISTS (SELECT * FROM sys.default_constraints WHERE name = 'DF_nguoi_dung_provider')
BEGIN
    ALTER TABLE dbo.nguoi_dung DROP CONSTRAINT DF_nguoi_dung_provider;
    PRINT N'✅ Dropped DF_nguoi_dung_provider';
END

-- 5.4 Recreate constraints with proper specifications
ALTER TABLE dbo.chi_tiet_gio_hang 
    ADD CONSTRAINT UQ_chi_tiet_gio_hang_gio_hang_san_pham_kich_co_mau_sac
    UNIQUE (gio_hang_id, san_pham_id, kich_co, mau_sac);
PRINT N'✅ Recreated UQ_chi_tiet_gio_hang_gio_hang_san_pham_kich_co_mau_sac';

ALTER TABLE dbo.don_hang 
    ADD CONSTRAINT DF_don_hang_trang_thai DEFAULT (N'CHO_XAC_NHAN') FOR trang_thai;
PRINT N'✅ Recreated DF_don_hang_trang_thai';

ALTER TABLE dbo.nguoi_dung 
    ADD CONSTRAINT DF_nguoi_dung_provider DEFAULT (N'local') FOR provider;
PRINT N'✅ Recreated DF_nguoi_dung_provider';

PRINT N'🎉 Hoàn tất sửa đổi tương thích với Hibernate!';

-- =============================================
-- ADMIN PERFORMANCE OPTIMIZATION
-- =============================================
PRINT N'⚡ Tối ưu hóa hiệu suất cho Admin Panel...';

-- Index cho tìm kiếm sản phẩm trong admin
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_san_pham_ten_ma' AND object_id = OBJECT_ID(N'dbo.san_pham'))
    CREATE INDEX IX_san_pham_ten_ma ON dbo.san_pham(ten, ma_san_pham);
PRINT N'✅ Tạo index IX_san_pham_ten_ma';

-- Index cho lọc theo danh mục
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_san_pham_danh_muc' AND object_id = OBJECT_ID(N'dbo.san_pham'))
    CREATE INDEX IX_san_pham_danh_muc ON dbo.san_pham(id_danh_muc);
PRINT N'✅ Tạo index IX_san_pham_danh_muc';

-- Index cho lọc theo thương hiệu
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_san_pham_thuong_hieu' AND object_id = OBJECT_ID(N'dbo.san_pham'))
    CREATE INDEX IX_san_pham_thuong_hieu ON dbo.san_pham(id_thuong_hieu);
PRINT N'✅ Tạo index IX_san_pham_thuong_hieu';

-- Index cho trạng thái sản phẩm
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_san_pham_hoat_dong' AND object_id = OBJECT_ID(N'dbo.san_pham'))
    CREATE INDEX IX_san_pham_hoat_dong ON dbo.san_pham(hoat_dong);
PRINT N'✅ Tạo index IX_san_pham_hoat_dong';

-- Index cho sản phẩm nổi bật
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_san_pham_noi_bat' AND object_id = OBJECT_ID(N'dbo.san_pham'))
    CREATE INDEX IX_san_pham_noi_bat ON dbo.san_pham(noi_bat);
PRINT N'✅ Tạo index IX_san_pham_noi_bat';

-- Index cho ngày tạo (để sắp xếp)
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_san_pham_ngay_tao' AND object_id = OBJECT_ID(N'dbo.san_pham'))
    CREATE INDEX IX_san_pham_ngay_tao ON dbo.san_pham(ngay_tao DESC);
PRINT N'✅ Tạo index IX_san_pham_ngay_tao';

-- Index cho ngày cập nhật
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_san_pham_ngay_cap_nhat' AND object_id = OBJECT_ID(N'dbo.san_pham'))
    CREATE INDEX IX_san_pham_ngay_cap_nhat ON dbo.san_pham(ngay_cap_nhat DESC);
PRINT N'✅ Tạo index IX_san_pham_ngay_cap_nhat';

-- Index cho tìm kiếm người dùng trong admin
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_nguoi_dung_ten_email' AND object_id = OBJECT_ID(N'dbo.nguoi_dung'))
    CREATE INDEX IX_nguoi_dung_ten_email ON dbo.nguoi_dung(ten, email);
PRINT N'✅ Tạo index IX_nguoi_dung_ten_email';

-- Index cho vai trò người dùng
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_nguoi_dung_vai_tro' AND object_id = OBJECT_ID(N'dbo.nguoi_dung'))
    CREATE INDEX IX_nguoi_dung_vai_tro ON dbo.nguoi_dung(vai_tro_id);
PRINT N'✅ Tạo index IX_nguoi_dung_vai_tro';

-- Index cho trạng thái người dùng
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_nguoi_dung_hoat_dong' AND object_id = OBJECT_ID(N'dbo.nguoi_dung'))
    CREATE INDEX IX_nguoi_dung_hoat_dong ON dbo.nguoi_dung(hoat_dong);
PRINT N'✅ Tạo index IX_nguoi_dung_hoat_dong';

-- Index cho đơn hàng theo ngày tạo
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_don_hang_ngay_tao' AND object_id = OBJECT_ID(N'dbo.don_hang'))
    CREATE INDEX IX_don_hang_ngay_tao ON dbo.don_hang(ngay_tao DESC);
PRINT N'✅ Tạo index IX_don_hang_ngay_tao';

-- Index cho trạng thái đơn hàng
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_don_hang_trang_thai' AND object_id = OBJECT_ID(N'dbo.don_hang'))
    CREATE INDEX IX_don_hang_trang_thai ON dbo.don_hang(trang_thai);
PRINT N'✅ Tạo index IX_don_hang_trang_thai';

-- Index cho người dùng trong đơn hàng
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_don_hang_nguoi_dung' AND object_id = OBJECT_ID(N'dbo.don_hang'))
    CREATE INDEX IX_don_hang_nguoi_dung ON dbo.don_hang(nguoi_dung_id);
PRINT N'✅ Tạo index IX_don_hang_nguoi_dung';

PRINT N'🎉 Hoàn tất tối ưu hóa hiệu suất Admin Panel!';

-- =============================================
-- ADMIN STORED PROCEDURES
-- =============================================
PRINT N'📊 Tạo stored procedures cho Admin Dashboard...';

-- Procedure lấy thống kê tổng quan
IF OBJECT_ID(N'dbo.sp_GetAdminStats', N'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_GetAdminStats;
GO

CREATE PROCEDURE dbo.sp_GetAdminStats
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        -- Thống kê sản phẩm
        (SELECT COUNT(*) FROM dbo.san_pham) AS TotalProducts,
        (SELECT COUNT(*) FROM dbo.san_pham WHERE hoat_dong = 1) AS ActiveProducts,
        (SELECT COUNT(*) FROM dbo.san_pham WHERE noi_bat = 1) AS FeaturedProducts,
        (SELECT COUNT(*) FROM dbo.san_pham WHERE so_luong_ton <= 10) AS LowStockProducts,
        
        -- Thống kê danh mục và thương hiệu
        (SELECT COUNT(*) FROM dbo.danh_muc) AS TotalCategories,
        (SELECT COUNT(*) FROM dbo.thuong_hieu) AS TotalBrands,
        
        -- Thống kê người dùng
        (SELECT COUNT(*) FROM dbo.nguoi_dung) AS TotalUsers,
        (SELECT COUNT(*) FROM dbo.nguoi_dung WHERE hoat_dong = 1) AS ActiveUsers,
        (SELECT COUNT(*) FROM dbo.nguoi_dung WHERE vai_tro_id = (SELECT id FROM dbo.vai_tro WHERE ten_vai_tro = N'Admin')) AS AdminUsers,
        
        -- Thống kê đơn hàng
        (SELECT COUNT(*) FROM dbo.don_hang) AS TotalOrders,
        (SELECT COUNT(*) FROM dbo.don_hang WHERE trang_thai = N'CHO_XAC_NHAN') AS PendingOrders,
        (SELECT COUNT(*) FROM dbo.don_hang WHERE trang_thai = N'DANG_GIAO') AS ShippingOrders,
        (SELECT COUNT(*) FROM dbo.don_hang WHERE trang_thai = N'DA_GIAO') AS CompletedOrders,
        
        -- Thống kê doanh thu
        (SELECT ISNULL(SUM(tong_tien), 0) FROM dbo.don_hang WHERE trang_thai = N'DA_GIAO') AS TotalRevenue,
        (SELECT ISNULL(SUM(tong_tien), 0) FROM dbo.don_hang WHERE trang_thai = N'DA_GIAO' AND ngay_tao >= DATEADD(day, -30, GETDATE())) AS MonthlyRevenue,
        (SELECT ISNULL(SUM(tong_tien), 0) FROM dbo.don_hang WHERE trang_thai = N'DA_GIAO' AND ngay_tao >= DATEADD(day, -7, GETDATE())) AS WeeklyRevenue;
END
GO

PRINT N'✅ Tạo stored procedure sp_GetAdminStats';

-- Procedure lấy sản phẩm bán chạy nhất
IF OBJECT_ID(N'dbo.sp_GetTopSellingProducts', N'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_GetTopSellingProducts;
GO

CREATE PROCEDURE dbo.sp_GetTopSellingProducts
    @TopCount INT = 10
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT TOP(@TopCount)
        sp.id,
        sp.ma_san_pham,
        sp.ten,
        sp.gia,
        sp.anh_chinh,
        sp.da_ban,
        sp.luot_xem,
        dm.ten AS danh_muc_ten,
        th.ten AS thuong_hieu_ten
    FROM dbo.san_pham sp
    LEFT JOIN dbo.danh_muc dm ON sp.id_danh_muc = dm.id
    LEFT JOIN dbo.thuong_hieu th ON sp.id_thuong_hieu = th.id
    WHERE sp.hoat_dong = 1
    ORDER BY sp.da_ban DESC, sp.luot_xem DESC;
END
GO

PRINT N'✅ Tạo stored procedure sp_GetTopSellingProducts';

-- Procedure lấy khách hàng mua nhiều nhất
IF OBJECT_ID(N'dbo.sp_GetTopCustomers', N'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_GetTopCustomers;
GO

CREATE PROCEDURE dbo.sp_GetTopCustomers
    @TopCount INT = 10
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT TOP(@TopCount)
        nd.id,
        nd.ten,
        nd.email,
        nd.so_dien_thoai,
        COUNT(dh.id) AS total_orders,
        ISNULL(SUM(dh.tong_tien), 0) AS total_spent
    FROM dbo.nguoi_dung nd
    LEFT JOIN dbo.don_hang dh ON nd.id = dh.nguoi_dung_id AND dh.trang_thai = N'DA_GIAO'
    WHERE nd.vai_tro_id = (SELECT id FROM dbo.vai_tro WHERE ten_vai_tro = N'Khách hàng')
    GROUP BY nd.id, nd.ten, nd.email, nd.so_dien_thoai
    ORDER BY total_spent DESC, total_orders DESC;
END
GO

PRINT N'✅ Tạo stored procedure sp_GetTopCustomers';

-- Procedure lấy thống kê doanh thu theo tháng
IF OBJECT_ID(N'dbo.sp_GetRevenueByMonth', N'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_GetRevenueByMonth;
GO

CREATE PROCEDURE dbo.sp_GetRevenueByMonth
    @MonthsBack INT = 12
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        YEAR(ngay_tao) AS year,
        MONTH(ngay_tao) AS month,
        COUNT(*) AS order_count,
        ISNULL(SUM(tong_tien), 0) AS total_revenue
    FROM dbo.don_hang
    WHERE trang_thai = N'DA_GIAO' 
        AND ngay_tao >= DATEADD(month, -@MonthsBack, GETDATE())
    GROUP BY YEAR(ngay_tao), MONTH(ngay_tao)
    ORDER BY year DESC, month DESC;
END
GO

PRINT N'✅ Tạo stored procedure sp_GetRevenueByMonth';

PRINT N'🎉 Hoàn tất tạo stored procedures cho Admin Dashboard!';

-- =============================================
-- ADMIN VIEWS
-- =============================================
PRINT N'👁️ Tạo views cho Admin Dashboard...';

-- View sản phẩm với thông tin đầy đủ (sẽ được tạo lại sau khi có bảng danh_muc_mon_the_thao)

-- View đơn hàng với thông tin khách hàng
IF OBJECT_ID(N'dbo.vw_OrdersWithCustomer', N'V') IS NOT NULL
    DROP VIEW dbo.vw_OrdersWithCustomer;
GO

CREATE VIEW dbo.vw_OrdersWithCustomer
AS
SELECT 
    dh.id,
    dh.ma_don_hang,
    dh.tong_tien,
    dh.phi_van_chuyen,
    dh.tong_thanh_toan,
    dh.ten_nguoi_nhan,
    dh.so_dien_thoai_nhan,
    dh.dia_chi_giao_hang,
    dh.trang_thai,
    dh.phuong_thuc_thanh_toan,
    dh.da_thanh_toan,
    dh.ghi_chu,
    dh.ngay_tao,
    dh.ngay_cap_nhat,
    nd.ten AS ten_khach_hang,
    nd.email AS email_khach_hang,
    nd.so_dien_thoai AS sdt_khach_hang,
    CASE 
        WHEN dh.trang_thai = N'CHO_XAC_NHAN' THEN N'Chờ xác nhận'
        WHEN dh.trang_thai = N'DANG_CHUAN_BI' THEN N'Đang chuẩn bị'
        WHEN dh.trang_thai = N'DANG_GIAO' THEN N'Đang giao'
        WHEN dh.trang_thai = N'DA_GIAO' THEN N'Đã giao'
        WHEN dh.trang_thai = N'DA_HUY' THEN N'Đã hủy'
        ELSE dh.trang_thai
    END AS trang_thai_display
FROM dbo.don_hang dh
LEFT JOIN dbo.nguoi_dung nd ON dh.nguoi_dung_id = nd.id;
GO

PRINT N'✅ Tạo view vw_OrdersWithCustomer';

-- View thống kê sản phẩm theo danh mục
IF OBJECT_ID(N'dbo.vw_CategoryStats', N'V') IS NOT NULL
    DROP VIEW dbo.vw_CategoryStats;
GO

CREATE VIEW dbo.vw_CategoryStats
AS
SELECT 
    dm.id,
    dm.ten AS danh_muc_ten,
    dm.mo_ta,
    COUNT(sp.id) AS tong_san_pham,
    COUNT(CASE WHEN sp.hoat_dong = 1 THEN 1 END) AS san_pham_hoat_dong,
    COUNT(CASE WHEN sp.noi_bat = 1 THEN 1 END) AS san_pham_noi_bat,
    ISNULL(SUM(sp.da_ban), 0) AS tong_da_ban,
    ISNULL(AVG(sp.gia), 0) AS gia_trung_binh,
    ISNULL(SUM(sp.so_luong_ton), 0) AS tong_ton_kho
FROM dbo.danh_muc dm
LEFT JOIN dbo.san_pham sp ON dm.id = sp.id_danh_muc
GROUP BY dm.id, dm.ten, dm.mo_ta;
GO

PRINT N'✅ Tạo view vw_CategoryStats';

-- View thống kê sản phẩm theo thương hiệu
IF OBJECT_ID(N'dbo.vw_BrandStats', N'V') IS NOT NULL
    DROP VIEW dbo.vw_BrandStats;
GO

CREATE VIEW dbo.vw_BrandStats
AS
SELECT 
    th.id,
    th.ten AS thuong_hieu_ten,
    th.ngay_tao,
    COUNT(sp.id) AS tong_san_pham,
    COUNT(CASE WHEN sp.hoat_dong = 1 THEN 1 END) AS san_pham_hoat_dong,
    COUNT(CASE WHEN sp.noi_bat = 1 THEN 1 END) AS san_pham_noi_bat,
    ISNULL(SUM(sp.da_ban), 0) AS tong_da_ban,
    ISNULL(AVG(sp.gia), 0) AS gia_trung_binh,
    ISNULL(SUM(sp.so_luong_ton), 0) AS tong_ton_kho
FROM dbo.thuong_hieu th
LEFT JOIN dbo.san_pham sp ON th.id = sp.id_thuong_hieu
GROUP BY th.id, th.ten, th.ngay_tao;
GO

PRINT N'✅ Tạo view vw_BrandStats';

PRINT N'🎉 Hoàn tất tạo views cho Admin Dashboard!';


-- =============================================
-- FINAL VIEW CREATION AFTER ALL TABLES
-- =============================================
PRINT N'🔧 Tạo view vw_ProductsWithDetails cuối cùng...';

-- Tạo view sản phẩm với thông tin đầy đủ
IF OBJECT_ID(N'dbo.vw_ProductsWithDetails', N'V') IS NOT NULL
    DROP VIEW dbo.vw_ProductsWithDetails;
GO

CREATE VIEW dbo.vw_ProductsWithDetails
AS
SELECT 
    sp.id,
    sp.ma_san_pham,
    sp.ten,
    sp.mo_ta,
    sp.gia,
    sp.gia_goc,
    sp.anh_chinh,
    sp.so_luong_ton,
    sp.chat_lieu,
    sp.xuat_xu,
    sp.luot_xem,
    sp.da_ban,
    sp.hoat_dong,
    sp.noi_bat,
    sp.ngay_tao,
    sp.ngay_cap_nhat,
    dm.ten AS danh_muc_ten,
    th.ten AS thuong_hieu_ten,
    dmtt.ten AS mon_the_thao_ten,
    CASE 
        WHEN sp.so_luong_ton <= 10 THEN N'Hết hàng'
        WHEN sp.so_luong_ton <= 50 THEN N'Sắp hết hàng'
        ELSE N'Còn hàng'
    END AS trang_thai_ton_kho,
    CASE 
        WHEN sp.gia_goc > sp.gia THEN CAST(((sp.gia_goc - sp.gia) / sp.gia_goc * 100) AS DECIMAL(5,2))
        ELSE 0
    END AS phan_tram_giam_gia
FROM dbo.san_pham sp
LEFT JOIN dbo.danh_muc dm ON sp.id_danh_muc = dm.id
LEFT JOIN dbo.thuong_hieu th ON sp.id_thuong_hieu = th.id
LEFT JOIN dbo.danh_muc_mon_the_thao dmtt ON sp.id_mon_the_thao = dmtt.id;
GO

PRINT N'✅ Tạo view vw_ProductsWithDetails thành công!';
PRINT N'🎉 HOÀN TẤT TẤT CẢ CÁC THÀNH PHẦN DATABASE!';

-- =============================================
-- HIBERNATE CONSTRAINTS FIX
-- =============================================
PRINT N'🔧 Sửa lỗi constraints cho Hibernate...';

-- Xóa các constraints gây lỗi khi Hibernate cố gắng thay đổi schema
-- 1. Xóa Unique constraint trên cột ma_don_hang trong bảng don_hang
IF EXISTS (SELECT * FROM sys.indexes WHERE name = 'UQ_don_hang_ma' AND object_id = OBJECT_ID('don_hang'))
BEGIN
    ALTER TABLE don_hang DROP CONSTRAINT UQ_don_hang_ma;
    PRINT N'✅ Đã xóa UQ_don_hang_ma';
END

-- 2. Xóa Default constraint trên cột phuong_thuc_thanh_toan trong bảng don_hang
IF EXISTS (SELECT * FROM sys.default_constraints WHERE name = 'DF_don_hang_pttt')
BEGIN
    ALTER TABLE don_hang DROP CONSTRAINT DF_don_hang_pttt;
    PRINT N'✅ Đã xóa DF_don_hang_pttt';
END

-- 3. Xóa Unique constraint trên cột email trong bảng nguoi_dung
IF EXISTS (SELECT * FROM sys.indexes WHERE name = 'UQ_nguoi_dung_email' AND object_id = OBJECT_ID('nguoi_dung'))
BEGIN
    ALTER TABLE nguoi_dung DROP CONSTRAINT UQ_nguoi_dung_email;
    PRINT N'✅ Đã xóa UQ_nguoi_dung_email';
END

-- 4. Xóa Unique constraint trên cột ma_san_pham trong bảng san_pham
IF EXISTS (SELECT * FROM sys.indexes WHERE name = 'UQ_san_pham_ma' AND object_id = OBJECT_ID('san_pham'))
BEGIN
    ALTER TABLE san_pham DROP CONSTRAINT UQ_san_pham_ma;
    PRINT N'✅ Đã xóa UQ_san_pham_ma';
END

-- 5. Xóa các indexes gây lỗi khi Hibernate thay đổi schema
-- Xóa index IX_nguoi_dung_ten_email
IF EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_nguoi_dung_ten_email' AND object_id = OBJECT_ID('nguoi_dung'))
BEGIN
    DROP INDEX IX_nguoi_dung_ten_email ON nguoi_dung;
    PRINT N'✅ Đã xóa IX_nguoi_dung_ten_email';
END

-- Xóa index IX_san_pham_ten_ma
IF EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_san_pham_ten_ma' AND object_id = OBJECT_ID('san_pham'))
BEGIN
    DROP INDEX IX_san_pham_ten_ma ON san_pham;
    PRINT N'✅ Đã xóa IX_san_pham_ten_ma';
END

-- Xóa các indexes khác có thể gây lỗi
DECLARE @sql3 NVARCHAR(MAX) = '';
SELECT @sql3 = @sql3 + 'DROP INDEX ' + name + ' ON ' + OBJECT_NAME(object_id) + ';' + CHAR(13)
FROM sys.indexes 
WHERE name LIKE 'IX_%' 
AND OBJECT_NAME(object_id) IN ('don_hang', 'nguoi_dung', 'san_pham', 'gio_hang', 'chi_tiet_gio_hang')
AND name NOT IN ('IX_nguoi_dung_ten_email', 'IX_san_pham_ten_ma');

IF @sql3 != ''
BEGIN
    PRINT N'🔧 Xóa các indexes khác có thể gây lỗi...';
    EXEC sp_executesql @sql3;
END

-- 6. Xóa các constraints khác có thể gây lỗi
-- Kiểm tra và xóa các unique constraints khác
DECLARE @sql NVARCHAR(MAX) = '';
SELECT @sql = @sql + 'ALTER TABLE ' + OBJECT_NAME(parent_object_id) + ' DROP CONSTRAINT ' + name + ';' + CHAR(13)
FROM sys.key_constraints 
WHERE type = 'UQ' 
AND OBJECT_NAME(parent_object_id) IN ('don_hang', 'nguoi_dung', 'san_pham', 'gio_hang', 'chi_tiet_gio_hang')
AND name NOT IN ('UQ_don_hang_ma', 'UQ_nguoi_dung_email', 'UQ_san_pham_ma');

IF @sql != ''
BEGIN
    PRINT N'🔧 Xóa các Unique constraints khác...';
    EXEC sp_executesql @sql;
END

-- Kiểm tra và xóa các default constraints khác
DECLARE @sql2 NVARCHAR(MAX) = '';
SELECT @sql2 = @sql2 + 'ALTER TABLE ' + OBJECT_NAME(parent_object_id) + ' DROP CONSTRAINT ' + name + ';' + CHAR(13)
FROM sys.default_constraints 
WHERE OBJECT_NAME(parent_object_id) IN ('don_hang', 'nguoi_dung', 'san_pham', 'gio_hang', 'chi_tiet_gio_hang')
AND name NOT IN ('DF_don_hang_pttt');

IF @sql2 != ''
BEGIN
    PRINT N'🔧 Xóa các Default constraints khác...';
    EXEC sp_executesql @sql2;
END

-- Tạo lại các constraints cần thiết sau khi Hibernate hoàn tất
-- (Hibernate sẽ tự động tạo lại các constraints dựa trên Entity annotations)

PRINT N'✅ Hoàn tất sửa lỗi constraints cho Hibernate!';
PRINT N'💡 Lưu ý: Sau khi ứng dụng Spring Boot chạy thành công, các constraints sẽ được tạo lại tự động.';

-- =============================================
-- FIX VIEWS AFTER ALL TABLES CREATED
-- =============================================


-- =============================================
-- BẢNG DANH MỤC MÔN THỂ THAO
-- =============================================
PRINT N'🏃 Tạo bảng danh mục môn thể thao...';

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='danh_muc_mon_the_thao' AND xtype='U')
BEGIN
    CREATE TABLE dbo.danh_muc_mon_the_thao (
        id BIGINT IDENTITY(1,1) NOT NULL,
        ten NVARCHAR(255) NOT NULL,
        mo_ta NVARCHAR(500) NULL,
        hinh_anh NVARCHAR(500) NULL,
        thu_tu INT NOT NULL DEFAULT 0,
        hoat_dong BIT NOT NULL DEFAULT 1,
        ngay_tao DATETIME2 NOT NULL DEFAULT GETDATE(),
        ngay_cap_nhat DATETIME2 NULL,
        CONSTRAINT PK_danh_muc_mon_the_thao PRIMARY KEY (id),
        CONSTRAINT UQ_danh_muc_mon_the_thao_ten UNIQUE (ten)
    );
    
    -- Thêm dữ liệu mẫu các môn thể thao
    INSERT INTO dbo.danh_muc_mon_the_thao (ten, mo_ta, hinh_anh, thu_tu, hoat_dong) VALUES
    (N'Pickleball', N'Bộ môn thể thao kết hợp giữa tennis, cầu lông và bóng bàn', N'/images/sports/pickleball.jpg', 1, 1),
    (N'Cầu lông', N'Môn thể thao sử dụng vợt và cầu lông', N'/images/sports/badminton.jpg', 2, 1),
    (N'Golf', N'Môn thể thao đánh bóng vào lỗ bằng gậy golf', N'/images/sports/golf.jpg', 3, 1),
    (N'Bóng đá', N'Môn thể thao đồng đội phổ biến nhất thế giới', N'/images/sports/football.jpg', 4, 1),
    (N'Chạy bộ', N'Môn thể thao cá nhân đơn giản và hiệu quả', N'/images/sports/running.jpg', 5, 1),
    (N'Tennis', N'Môn thể thao sử dụng vợt và bóng tennis', N'/images/sports/tennis.jpg', 6, 1),
    (N'Bóng rổ', N'Môn thể thao đồng đội với bóng rổ', N'/images/sports/basketball.jpg', 7, 1),
    (N'Tập luyện', N'Trang phục và phụ kiện cho các hoạt động tập luyện', N'/images/sports/training.jpg', 8, 1);
    
    PRINT N'✅ Tạo bảng danh mục môn thể thao thành công!';
END
ELSE
BEGIN
    PRINT N'⚠️ Bảng danh mục môn thể thao đã tồn tại!';
END

-- Thêm foreign key constraint cho san_pham -> danh_muc_mon_the_thao
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_san_pham_mon_the_thao')
    ALTER TABLE dbo.san_pham
    ADD CONSTRAINT FK_san_pham_mon_the_thao FOREIGN KEY (id_mon_the_thao) REFERENCES dbo.danh_muc_mon_the_thao(id);

-- =============================================
-- BẢNG BANNER
-- =============================================
PRINT N'📸 Tạo bảng banner...';

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='banner' AND xtype='U')
BEGIN
    CREATE TABLE dbo.banner (
        id BIGINT IDENTITY(1,1) NOT NULL,
        ten NVARCHAR(255) NOT NULL,
        hinh_anh NVARCHAR(500) NOT NULL,
        mo_ta NVARCHAR(1000) NULL,
        link NVARCHAR(500) NULL,
        vi_tri NVARCHAR(50) NOT NULL,
        thu_tu INT NOT NULL DEFAULT 0,
        hoat_dong BIT NOT NULL DEFAULT 1,
        ngay_tao DATETIME2 NOT NULL DEFAULT GETDATE(),
        ngay_cap_nhat DATETIME2 NULL,
        CONSTRAINT PK_banner PRIMARY KEY (id)
    );
    
    -- Thêm dữ liệu mẫu
    INSERT INTO dbo.banner (ten, hinh_anh, mo_ta, link, vi_tri, thu_tu, hoat_dong) VALUES
    (N'Banner chính 1', N'/images/banner/slider_1.jpg', N'Banner quảng cáo sản phẩm mới', N'/', N'main', 1, 1),
    (N'Banner chính 2', N'/images/banner/slider_2.jpg', N'Banner khuyến mãi đặc biệt', N'/', N'main', 2, 1),
    (N'Banner chính 3', N'/images/banner/slider_3.jpg', N'Banner giới thiệu thương hiệu', N'/', N'main', 3, 1),
    (N'Banner header', N'/images/banner/header_banner.jpg', N'Banner đầu trang', N'/', N'header', 1, 1),
    (N'Banner sidebar', N'/images/banner/sidebar_banner.jpg', N'Banner thanh bên', N'/', N'sidebar', 1, 1);
    
    PRINT N'✅ Tạo bảng banner thành công!';
END
ELSE
BEGIN
    PRINT N'⚠️ Bảng banner đã tồn tại!';
END
