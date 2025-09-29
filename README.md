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

### ✅ **Đã hoàn thành**

#### **🔐 Authentication & Authorization**
- Đăng nhập/đăng ký với email và mật khẩu
- Google OAuth2 integration
- Xác thực email qua OTP
- Phân quyền Admin/User
- Session management

#### **👤 User Management**
- Profile management
- Avatar upload
- User information update
- Password change

#### **🛍️ Product Management**
- Hiển thị danh sách sản phẩm
- Chi tiết sản phẩm
- Tìm kiếm sản phẩm
- Lọc theo danh mục, thương hiệu
- Phân trang sản phẩm

#### **🛒 Shopping Cart**
- Thêm/xóa sản phẩm vào giỏ hàng
- Cập nhật số lượng
- Tính tổng tiền
- Lưu giỏ hàng theo session

#### **⭐ Favorites System**
- Thêm/xóa sản phẩm yêu thích
- Danh sách sản phẩm yêu thích
- Persistent storage

#### **🏷️ Category & Brand Management**
- Hiển thị danh mục sản phẩm
- Hiển thị thương hiệu
- Lọc sản phẩm theo danh mục/thương hiệu

#### **🏃 Sports Management**
- Quản lý các môn thể thao
- Upload hình ảnh môn thể thao
- Hiển thị sản phẩm theo môn thể thao

#### **🎨 Banner Management**
- Upload banner trang chủ
- Quản lý slider banner
- Hiển thị banner động

#### **📱 Responsive Design**
- Giao diện thân thiện mobile
- Bootstrap responsive
- Mobile-first approach

### 🚧 **Đang phát triển**

#### **👨‍💼 Admin Panel**
- Dashboard tổng quan
- Quản lý sản phẩm (CRUD)
- Quản lý danh mục (CRUD)
- Quản lý thương hiệu (CRUD)
- Quản lý môn thể thao (CRUD)
- Quản lý banner (CRUD)
- Quản lý người dùng
- Upload hình ảnh sản phẩm

#### **💳 Payment System**
- Tích hợp cổng thanh toán
- Xử lý đơn hàng
- Lịch sử mua hàng

#### **📦 Order Management**
- Tạo đơn hàng
- Theo dõi trạng thái đơn hàng
- Quản lý đơn hàng cho admin

#### **📧 Notification System**
- Email thông báo đơn hàng
- Thông báo trong ứng dụng

#### **🔍 Advanced Search**
- Tìm kiếm nâng cao
- Filter phức tạp
- Auto-complete search

#### **📊 Analytics & Reports**
- Thống kê bán hàng
- Báo cáo doanh thu
- Dashboard analytics

### 📋 **Roadmap tương lai**

#### **🛒 E-commerce Features**
- Inventory management
- Stock tracking
- Product variants (size, color)
- Discount & promotion system

#### **👥 Social Features**
- User reviews & ratings
- Wishlist sharing
- Social login (Facebook, Twitter)

#### **📱 Mobile App**
- React Native mobile app
- Push notifications
- Offline support

#### **🌐 Multi-language**
- English/Vietnamese support
- Internationalization

#### **🔒 Security Enhancements**
- Two-factor authentication
- Advanced security logging
- Rate limiting

## Cấu hình Database

1. Cài đặt SQL Server
2. Tạo database tên `DATN`
3. Chạy script trong `src/main/resources/db/DATN.sql`
4. Cập nhật thông tin database trong `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=DATN;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD_HERE
```

### 📊 **Database Schema**
- **Users**: Thông tin người dùng
- **Products**: Sản phẩm
- **Categories**: Danh mục sản phẩm
- **Brands**: Thương hiệu
- **Sports**: Môn thể thao
- **Banners**: Banner trang chủ
- **Cart**: Giỏ hàng
- **Favorites**: Sản phẩm yêu thích

## Lưu ý

- **Single Application**: Dự án sử dụng Spring Boot với Thymeleaf (không tách frontend/backend)
- **Database**: Đảm bảo SQL Server đang chạy và database `DATN` đã được tạo
- **Java Version**: Cần Java 17+ để chạy ứng dụng
- **Port**: Ứng dụng chạy trên port 8080 (có thể thay đổi trong application.properties)
- **File Upload**: Thư mục uploads/ sẽ được tạo tự động khi upload file

## 📞 Hỗ trợ

- **Email**: datnfpolysd45@gmail.com
- **GitHub**: https://github.com/lthe205/Datn
- **Issues**: Tạo issue trên GitHub nếu gặp vấn đề