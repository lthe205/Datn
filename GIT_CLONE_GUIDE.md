# 📥 Hướng dẫn chạy dự án từ Git Clone

## 🚀 Cách 1: Clone và chạy nhanh

### Bước 1: Clone dự án
```bash
# Mở Command Prompt hoặc Terminal
git clone https://github.com/lthe205/Datn.git
cd Datn
```

### Bước 2: Chạy bằng script (Windows)
```cmd
# Double-click file run.bat
# Hoặc chạy trong Command Prompt:
run.bat
```

### Bước 3: Chạy bằng script (Linux/Mac)
```bash
# Cấp quyền thực thi:
chmod +x run.sh

# Chạy:
./run.sh
```

## 🔧 Cách 2: Clone và chạy bằng IDE

### Bước 1: Clone dự án
```bash
git clone https://github.com/lthe205/Datn.git
cd Datn
```

### Bước 2: Mở bằng IDE

#### **IntelliJ IDEA:**
1. Mở IntelliJ IDEA
2. **File** → **Open** → chọn thư mục `Datn`
3. Đợi IDE load Maven dependencies (2-3 phút)
4. Tìm file `src/main/java/com/example/datn/DatnApplication.java`
5. Click chuột phải → **"Run 'DatnApplication'"**

#### **Eclipse:**
1. Mở Eclipse
2. **File** → **Import** → **Maven** → **Existing Maven Projects**
3. Chọn thư mục `Datn`
4. Đợi Eclipse load dependencies
5. Tìm class `DatnApplication.java` → **Run As** → **Java Application**

#### **VS Code:**
1. Mở VS Code
2. **File** → **Open Folder** → chọn thư mục `Datn`
3. Cài đặt extension **"Extension Pack for Java"**
4. Mở file `DatnApplication.java`
5. Click **"Run"** button hoặc **Ctrl+F5**

## 🗄️ Cách 3: Clone và cài đặt đầy đủ

### Bước 1: Clone dự án
```bash
git clone https://github.com/lthe205/Datn.git
cd Datn
```

### Bước 2: Cài đặt yêu cầu hệ thống

#### **Java 17+:**
- **Windows**: [Tải xuống](https://www.oracle.com/java/technologies/downloads/)
- **Linux**: `sudo apt install openjdk-17-jdk`
- **Mac**: `brew install openjdk@17`

#### **SQL Server:**
- **Windows**: [SQL Server Express](https://www.microsoft.com/en-us/sql-server/sql-server-downloads)
- **Linux**: [SQL Server on Linux](https://docs.microsoft.com/en-us/sql/linux/sql-server-linux-setup)
- **Mac**: [SQL Server on Mac](https://docs.microsoft.com/en-us/sql/linux/sql-server-linux-setup)

#### **Maven (tùy chọn):**
- **Windows**: [Tải xuống](https://maven.apache.org/download.cgi)
- **Linux**: `sudo apt install maven`
- **Mac**: `brew install maven`

### Bước 3: Cài đặt database

#### **Tạo database:**
1. Mở **SQL Server Management Studio (SSMS)**
2. Kết nối với SQL Server
3. Tạo database mới tên `DATN`
4. Chạy script SQL trong file `src/main/resources/db/DATN.sql`

#### **Cấu hình kết nối:**
Chỉnh sửa file `src/main/resources/application.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=DATN;encrypt=true;trustServerCertificate=true;characterEncoding=UTF-8;useUnicode=true
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD_HERE
```

### Bước 4: Chạy dự án

#### **Bằng Maven:**
```bash
# Windows
mvn spring-boot:run

# Linux/Mac
mvn spring-boot:run
```

#### **Bằng JAR file:**
```bash
# Build
mvn clean package

# Chạy
java -jar target/datn-0.0.1-SNAPSHOT.jar
```

## 🌐 Kiểm tra ứng dụng

### Sau khi chạy thành công:
- Mở trình duyệt
- Truy cập: **http://localhost:8080**
- Bạn sẽ thấy trang chủ của website

### Các trang chính:
- **Trang chủ**: http://localhost:8080
- **Admin**: http://localhost:8080/admin
- **Đăng nhập**: http://localhost:8080/dang-nhap
- **Đăng ký**: http://localhost:8080/dang-ky
- **Giỏ hàng**: http://localhost:8080/gio-hang

## 👤 Tài khoản mặc định

### Admin:
- **Email**: admin@activewear.com
- **Password**: admin123

### User thường:
- Tạo tài khoản mới qua trang đăng ký
- Hoặc đăng nhập bằng Google OAuth2

## ❌ Xử lý lỗi thường gặp

### Lỗi "Cannot find path .mvn/wrapper/maven-wrapper.properties":
```bash
# Giải pháp: Sử dụng Maven trực tiếp
mvn spring-boot:run

# Hoặc sử dụng IDE
```

### Lỗi kết nối database:
```
Error: Cannot connect to SQL Server
```
**Giải pháp:**
1. Kiểm tra SQL Server đã chạy chưa
2. Kiểm tra username/password trong `application.properties`
3. Kiểm tra port 1433 có bị chặn không

### Lỗi port 8080 đã được sử dụng:
```
Error: Port 8080 was already in use
```
**Giải pháp:**
```cmd
# Windows - Tìm process
netstat -ano | findstr :8080

# Windows - Kill process
taskkill /PID [PID] /F

# Linux/Mac - Tìm process
lsof -i :8080

# Linux/Mac - Kill process
kill -9 [PID]
```

### Lỗi Java version:
```
Error: Unsupported major.minor version
```
**Giải pháp:**
1. Kiểm tra Java version: `java -version`
2. Cài đặt Java 17 hoặc cao hơn
3. Cập nhật JAVA_HOME environment variable

### Lỗi Maven dependencies:
```
Error: Could not resolve dependencies
```
**Giải pháp:**
```bash
# Xóa cache Maven
mvn clean

# Tải lại dependencies
mvn dependency:resolve

# Hoặc xóa thư mục .m2 và tải lại
rm -rf ~/.m2/repository
mvn clean install
```

## 📋 Checklist nhanh

- [ ] Clone repository: `git clone https://github.com/lthe205/Datn.git`
- [ ] Cài đặt Java 17+
- [ ] Cài đặt SQL Server
- [ ] Tạo database `DATN`
- [ ] Chạy script SQL
- [ ] Cập nhật password trong `application.properties`
- [ ] Chạy dự án bằng IDE hoặc Maven
- [ ] Truy cập http://localhost:8080

## 🎯 Các phương pháp chạy được khuyến nghị

### 1. **IDE (Khuyến nghị nhất)**
- Dễ dàng debug
- Tự động load dependencies
- Hỗ trợ hot reload

### 2. **Script tự động**
- Kiểm tra môi trường tự động
- Hướng dẫn rõ ràng khi lỗi
- Phù hợp cho người mới

### 3. **Maven trực tiếp**
- Phù hợp cho developer có kinh nghiệm
- Kiểm soát hoàn toàn quá trình build

## 📞 Hỗ trợ

Nếu gặp vấn đề:
1. Kiểm tra **console logs** để xem lỗi chi tiết
2. Kiểm tra **database connection** có hoạt động không
3. Kiểm tra **Java version** có đúng không
4. Kiểm tra **port 8080** có bị chiếm không

**Liên hệ:** datnfpolysd45@gmail.com
**GitHub:** https://github.com/lthe205/Datn
