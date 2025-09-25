package com.example.datn.contact;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactController {

    @PostMapping("/lien-he/gui-tin-nhan")
    public String guiTinNhan(@RequestParam String hoTen,
                            @RequestParam String email,
                            @RequestParam(required = false) String soDienThoai,
                            @RequestParam(required = false) String chuDe,
                            @RequestParam String noiDung,
                            RedirectAttributes redirectAttributes) {
        
        try {
            // TODO: Lưu tin nhắn vào database
            // ContactMessage contactMessage = new ContactMessage();
            // contactMessage.setHoTen(hoTen);
            // contactMessage.setEmail(email);
            // contactMessage.setSoDienThoai(soDienThoai);
            // contactMessage.setChuDe(chuDe);
            // contactMessage.setNoiDung(noiDung);
            // contactMessage.setNgayTao(LocalDateTime.now());
            // contactMessageRepository.save(contactMessage);
            
            // Tạm thời chỉ hiển thị thông báo thành công
            redirectAttributes.addFlashAttribute("success", 
                "Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi trong thời gian sớm nhất.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Có lỗi xảy ra khi gửi tin nhắn. Vui lòng thử lại sau.");
        }
        
        return "redirect:/lien-he";
    }
}
