# DATN - Activewear Store

Dự án đồ án tốt nghiệp - Cửa hàng thời trang thể thao

## 📥 Hướng dẫn Git Clone

**Để chạy dự án từ Git Clone, xem hướng dẫn chi tiết tại:** [GIT_CLONE_GUIDE.md](GIT_CLONE_GUIDE.md)

### 🚀 Chạy nhanh:
```bash
git clone https://github.com/lthe205/Datn.git
cd Datn
# Windows: run.bat
# Linux/Mac: ./run.sh
```

## Cấu trúc dự án

Dự án sử dụng Spring Boot với cấu trúc truyền thống:

```
DATN/
├── src/
│   ├── main/
│   │   ├── java/              # Java source code
│   │   └── resources/
│   │       ├── static/        # CSS, JS, Images
│   │       ├── templates/     # HTML templates (Thymeleaf)
│   │       └── application.properties
│   └── test/                  # Test files
├── uploads/                   # File uploads
├── pom.xml                    # Maven dependencies
└── README.md                  # File này
```

## Công nghệ sử dụng

- **Spring Boot 3.5.6** - Framework chính
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Database ORM
- **Thymeleaf** - Template engine
- **SQL Server** - Database
- **Maven** - Build tool

## Cài đặt và chạy

### 1. Yêu cầu hệ thống
- **Java 17+**
- **Maven 3.6+** (hoặc sử dụng Maven Wrapper)
- **SQL Server**
- **IDE** (IntelliJ IDEA, Eclipse) - Khuyến nghị

### 2. Chạy dự án

**Phương pháp 1: Sử dụng IDE (Khuyến nghị)**
1. Mở dự án trong IntelliJ IDEA hoặc Eclipse
2. Import Maven project
3. Chạy class `DatnApplication.java`

**Phương pháp 2: Sử dụng Maven Wrapper**
```bash
./mvnw spring-boot:run
```

**Phương pháp 3: Sử dụng Maven**
```bash
mvn spring-boot:run
```

### 3. Truy cập ứng dụng
- **Trang chủ**: http://localhost:8080
- **Admin**: http://localhost:8080/admin
- **API**: http://localhost:8080/api

## Tính năng chính

- **Authentication**: Đăng nhập/đăng ký với email và Google OAuth2
- **Product Management**: Quản lý sản phẩm, danh mục, thương hiệu
- **Shopping Cart**: Giỏ hàng và thanh toán
- **Admin Panel**: Quản lý hệ thống
- **File Upload**: Upload hình ảnh sản phẩm
- **Responsive Design**: Giao diện thân thiện mobile

## Cấu hình Database

1. Cài đặt SQL Server
2. Tạo database tên `DATN`
3. Chạy script trong `backend/src/main/resources/db/DATN.sql`
4. Cập nhật thông tin database trong `backend/src/main/resources/application.properties`

## Lưu ý

- Đảm bảo cả backend và frontend đều chạy cùng lúc
- Backend cung cấp API cho frontend
- CORS đã được cấu hình để cho phép kết nối giữa frontend và backend