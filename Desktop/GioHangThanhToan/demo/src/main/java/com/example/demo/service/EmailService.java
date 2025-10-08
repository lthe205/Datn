package com.example.demo.service;

import com.example.demo.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${app.email.from}")
    private String fromEmail;
    
    @Value("${app.email.support}")
    private String supportEmail;
    
    @Value("${app.name}")
    private String appName;
    
    @Value("${app.url:http://localhost:8080}")
    private String baseUrl;
    
    /**
     * Gửi email xác nhận đơn hàng
     */
    public void sendOrderConfirmationEmail(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(order.getNguoiDung().getEmail());
            helper.setSubject("Xác nhận đơn hàng #" + order.getMaDonHang() + " - " + appName);
            
            // Chuẩn bị dữ liệu cho template
            Map<String, Object> variables = new HashMap<>();
            variables.put("order", order);
            variables.put("customer", order.getNguoiDung());
            variables.put("appName", appName);
            variables.put("supportEmail", supportEmail);
            variables.put("orderDate", order.getNgayTao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            variables.put("totalAmount", formatCurrency(order.getTongTien()));
            variables.put("shippingFee", formatCurrency(order.getPhiVanChuyen()));
            variables.put("finalAmount", formatCurrency(order.getTongThanhToan()));
            variables.put("baseUrl", baseUrl);
            
            Context context = new Context();
            context.setVariables(variables);
            
            String htmlContent = templateEngine.process("email/order-confirmation", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Order confirmation email sent successfully to: {}", order.getNguoiDung().getEmail());
            
        } catch (MessagingException e) {
            log.error("Failed to send order confirmation email to: {}", order.getNguoiDung().getEmail(), e);
        }
    }
    
    /**
     * Gửi email cập nhật trạng thái đơn hàng
     */
    public void sendOrderStatusUpdateEmail(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(order.getNguoiDung().getEmail());
            helper.setSubject("Cập nhật trạng thái đơn hàng #" + order.getMaDonHang() + " - " + appName);
            
            Map<String, Object> variables = new HashMap<>();
            variables.put("order", order);
            variables.put("customer", order.getNguoiDung());
            variables.put("appName", appName);
            variables.put("supportEmail", supportEmail);
            variables.put("statusDisplay", order.getTrangThaiDisplay());
            
            Context context = new Context();
            context.setVariables(variables);
            
            String htmlContent = templateEngine.process("email/order-status-update", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Order status update email sent successfully to: {}", order.getNguoiDung().getEmail());
            
        } catch (MessagingException e) {
            log.error("Failed to send order status update email to: {}", order.getNguoiDung().getEmail(), e);
        }
    }
    
    /**
     * Gửi email hủy đơn hàng
     */
    public void sendOrderCancellationEmail(Order order, String reason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(order.getNguoiDung().getEmail());
            helper.setSubject("Hủy đơn hàng #" + order.getMaDonHang() + " - " + appName);
            
            Map<String, Object> variables = new HashMap<>();
            variables.put("order", order);
            variables.put("customer", order.getNguoiDung());
            variables.put("appName", appName);
            variables.put("supportEmail", supportEmail);
            variables.put("cancellationReason", reason);
            
            Context context = new Context();
            context.setVariables(variables);
            
            String htmlContent = templateEngine.process("email/order-cancellation", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Order cancellation email sent successfully to: {}", order.getNguoiDung().getEmail());
            
        } catch (MessagingException e) {
            log.error("Failed to send order cancellation email to: {}", order.getNguoiDung().getEmail(), e);
        }
    }
    
    /**
     * Gửi email đơn giản
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            log.info("Simple email sent successfully to: {}", to);
            
        } catch (Exception e) {
            log.error("Failed to send simple email to: {}", to, e);
        }
    }
    
    /**
     * Format tiền tệ
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0 VNĐ";
        }
        return String.format("%,.0f VNĐ", amount.doubleValue());
    }
}
 