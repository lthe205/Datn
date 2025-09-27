package com.example.datn.web;

import com.example.datn.auth.AuthHelper;
import com.example.datn.user.NguoiDung;
import com.example.datn.user.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class FileUploadController {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping("/upload-avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @RequestParam("avatar") MultipartFile file,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Kiểm tra user đã đăng nhập chưa
            NguoiDung user = AuthHelper.getCurrentUser(authentication);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để upload avatar");
                return ResponseEntity.badRequest().body(response);
            }

            // Kiểm tra file có tồn tại không
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Vui lòng chọn file ảnh");
                return ResponseEntity.badRequest().body(response);
            }

            // Kiểm tra định dạng file
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "Chỉ được upload file ảnh (JPG, PNG, GIF)");
                return ResponseEntity.badRequest().body(response);
            }

            // Kiểm tra kích thước file (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "Kích thước file không được vượt quá 5MB");
                return ResponseEntity.badRequest().body(response);
            }

            // Tạo thư mục upload nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir, "avatars");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Tạo tên file unique
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                response.put("success", false);
                response.put("message", "Tên file không hợp lệ");
                return ResponseEntity.badRequest().body(response);
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            // Lưu file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // Cập nhật avatar URL trong database
            String avatarUrl = "/uploads/avatars/" + filename;
            user.setAvatarUrl(avatarUrl);
            user.setNgayCapNhat(LocalDateTime.now());
            nguoiDungRepository.save(user);

            // Debug logging
            System.out.println("Debug - Upload path: " + uploadPath);
            System.out.println("Debug - File path: " + filePath);
            System.out.println("Debug - Avatar URL: " + avatarUrl);
            System.out.println("Debug - File exists: " + Files.exists(filePath));

            response.put("success", true);
            response.put("message", "Upload avatar thành công");
            response.put("avatarUrl", avatarUrl);
            
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Lỗi khi upload file: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/remove-avatar")
    public ResponseEntity<Map<String, Object>> removeAvatar(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Kiểm tra user đã đăng nhập chưa
            NguoiDung user = AuthHelper.getCurrentUser(authentication);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để thực hiện thao tác này");
                return ResponseEntity.badRequest().body(response);
            }

            // Xóa avatar URL trong database
            user.setAvatarUrl(null);
            user.setNgayCapNhat(LocalDateTime.now());
            nguoiDungRepository.save(user);

            response.put("success", true);
            response.put("message", "Xóa avatar thành công");
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
