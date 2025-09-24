# Activewear Store - Website Bán Quần Áo Thể Thao

## Mô tả dự án
Website bán quần áo thể thao được xây dựng bằng Spring Boot với giao diện hiện đại và responsive. Dự án bao gồm hệ thống quản lý sản phẩm, danh mục, thương hiệu và các tính năng tìm kiếm, lọc sản phẩm.

## Công nghệ sử dụng
- **Backend**: Spring Boot 3.x, Spring Security, Spring Data JPA
- **Database**: SQL Server
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
- **UI Framework**: Font Awesome, Google Fonts
- **Build Tool**: Maven

## Tính năng chính

### Trang chủ
- Hiển thị sản phẩm nổi bật
- Sản phẩm mới nhất
- Sản phẩm bán chạy nhất
- Sản phẩm khuyến mãi
- Thống kê tổng quan
- Tìm kiếm sản phẩm
- Danh mục và thương hiệu

### Quản lý sản phẩm
- Entity cho sản phẩm, danh mục, thương hiệu
- Biến thể sản phẩm (kích cỡ, màu sắc)
- Ảnh sản phẩm
- Repository với các query tùy chỉnh
- Service layer xử lý business logic

### Giao diện người dùng
- Responsive design
- Giao diện hiện đại với gradient và shadow
- Animation và transition mượt mà
- Tối ưu cho mobile và desktop

## Cấu trúc dự án

```
src/main/java/com/example/datn/
├── auth/                    # Xác thực và phân quyền
├── common/                  # Các service chung
├── config/                  # Cấu hình Spring
├── product/                 # Quản lý sản phẩm
│   ├── DanhMuc.java
│   ├── ThuongHieu.java
│   ├── SanPham.java
│   ├── BienTheSanPham.java
│   ├── AnhSanPham.java
│   └── *Repository.java
├── user/                    # Quản lý người dùng
└── web/                     # Controller và Service
    ├── HomeController.java
    └── HomeService.java

src/main/resources/
├── static/css/
│   └── home.css            # CSS cho trang chủ
├── templates/
│   ├── index.html          # Trang chủ
│   ├── search.html         # Trang tìm kiếm
│   └── product-detail.html # Chi tiết sản phẩm
└── db/
    ├── DATN.sql           # Schema database
    └── sample_data.sql    # Dữ liệu mẫu
```

## Cài đặt và chạy dự án

### 1. Yêu cầu hệ thống
- Java 17+
- Maven 3.6+
- SQL Server 2019+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### 2. Cấu hình database
1. Tạo database `DATN` trong SQL Server
2. Chạy file `src/main/resources/db/DATN.sql` để tạo schema
3. Chạy file `src/main/resources/db/sample_data.sql` để thêm dữ liệu mẫu
4. Cập nhật thông tin kết nối trong `application.properties`:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=DATN;encrypt=true;trustServerCertificate=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Chạy ứng dụng
```bash
# Clone repository
git clone <repository-url>
cd Datn-feature-login

# Build project
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

### 4. Truy cập ứng dụng
- URL: http://localhost:8080
- Trang chủ: http://localhost:8080/
- Tìm kiếm: http://localhost:8080/tim-kiem?q=keyword
- Chi tiết sản phẩm: http://localhost:8080/san-pham/{id}

## API Endpoints

### Trang chủ
- `GET /` - Trang chủ với sản phẩm nổi bật, mới nhất, bán chạy

### Tìm kiếm và lọc
- `GET /tim-kiem?q={keyword}&page={page}&size={size}` - Tìm kiếm sản phẩm
- `GET /danh-muc/{id}?page={page}&size={size}` - Lọc theo danh mục
- `GET /thuong-hieu/{id}?page={page}&size={size}` - Lọc theo thương hiệu

### Chi tiết sản phẩm
- `GET /san-pham/{id}` - Chi tiết sản phẩm
- `GET /api/san-pham/{id}/anh` - API lấy ảnh sản phẩm

## Dữ liệu mẫu

Dự án đã bao gồm dữ liệu mẫu với:
- 3 vai trò (Admin, Khách hàng, Nhân viên)
- 15 danh mục sản phẩm
- 10 thương hiệu
- 12 sản phẩm với đầy đủ thông tin
- 16 biến thể sản phẩm
- 8 ảnh sản phẩm

## Tính năng nổi bật

### 1. Responsive Design
- Tối ưu cho mọi thiết bị
- Mobile-first approach
- Grid layout linh hoạt

### 2. Performance
- Lazy loading cho ảnh
- Pagination cho danh sách sản phẩm
- Caching với Spring Cache

### 3. User Experience
- Tìm kiếm real-time
- Filter và sort sản phẩm
- Smooth scrolling và animation
- Loading states

### 4. SEO Friendly
- Semantic HTML
- Meta tags
- Clean URLs
- Sitemap ready

## Phát triển tiếp

### Tính năng có thể thêm
1. **Giỏ hàng và thanh toán**
   - Thêm sản phẩm vào giỏ
   - Checkout process
   - Payment integration

2. **Quản lý đơn hàng**
   - Theo dõi đơn hàng
   - Lịch sử mua hàng
   - Invoice generation

3. **Hệ thống đánh giá**
   - Rating sản phẩm
   - Review và comment
   - Photo reviews

4. **Admin Dashboard**
   - Quản lý sản phẩm
   - Thống kê bán hàng
   - Quản lý đơn hàng

5. **Tính năng nâng cao**
   - Wishlist
   - Compare products
   - Recently viewed
   - Recommendations

## Đóng góp

1. Fork dự án
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## License

Distributed under the MIT License. See `LICENSE` for more information.

## Liên hệ

- Email: info@activewear.com
- Phone: 0123 456 789
- Address: 123 Đường ABC, Quận 1, TP.HCM

---

**Lưu ý**: Đây là dự án demo cho mục đích học tập. Để sử dụng trong production, cần thêm các tính năng bảo mật, validation và error handling đầy đủ.
