# 🚀 Hướng dẫn chạy nhanh

## ⚡ Cách chạy nhanh nhất

### 1. Sử dụng IDE (Khuyến nghị)
1. **Mở IntelliJ IDEA** (hoặc Eclipse, VS Code)
2. **Open** → chọn thư mục dự án
3. **Đợi** IDE load Maven dependencies (2-3 phút)
4. **Tìm** file `DatnApplication.java`
5. **Click chuột phải** → "Run 'DatnApplication'"

### 2. Sử dụng script (Windows)
```cmd
# Double-click file run.bat
# Hoặc chạy trong Command Prompt:
run.bat
```

### 3. Sử dụng script (Linux/Mac)
```bash
# Cấp quyền thực thi:
chmod +x run.sh

# Chạy:
./run.sh
```

### 4. Sử dụng Maven (nếu đã cài)
```cmd
# Windows
mvn spring-boot:run

# Linux/Mac
mvn spring-boot:run
```

## 🔧 Yêu cầu tối thiểu

- ✅ **Java 17+** - [Tải xuống](https://www.oracle.com/java/technologies/downloads/)
- ✅ **SQL Server** - [Tải xuống](https://www.microsoft.com/en-us/sql-server/sql-server-downloads)
- ✅ **IDE** (IntelliJ IDEA, Eclipse, VS Code)

## 🗄️ Cài đặt Database

1. **Cài đặt SQL Server Express**
2. **Tạo database** tên `DATN`
3. **Chạy script** `src/main/resources/db/DATN.sql`
4. **Cập nhật** password trong `src/main/resources/application.properties`

## 🌐 Truy cập ứng dụng

- **Trang chủ**: http://localhost:8080
- **Admin**: http://localhost:8080/admin
- **Đăng nhập**: http://localhost:8080/dang-nhap

## 👤 Tài khoản mặc định

- **Admin**: admin@activewear.com / admin123
- **User**: Tạo mới hoặc đăng nhập Google

## ❌ Xử lý lỗi

### Port 8080 đã được sử dụng:
```cmd
# Tìm process
netstat -ano | findstr :8080

# Kill process
taskkill /PID [PID] /F
```

### Lỗi database:
- Kiểm tra SQL Server đã chạy
- Kiểm tra password trong `application.properties`

### Lỗi Java:
- Cài đặt Java 17+
- Cập nhật JAVA_HOME

## 📞 Hỗ trợ

- **Email**: datnfpolysd45@gmail.com
- **GitHub**: https://github.com/lthe205/Datn