package com.example.demo.service;

import com.example.demo.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VNPayService {

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.secretKey}")
    private String secretKey;

    @Value("${vnpay.payUrl}")
    private String payUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    @Value("${vnpay.version}")
    private String version;

    @Value("${vnpay.command}")
    private String command;

    @Value("${vnpay.currCode}")
    private String currCode;

    /**
     * Tạo URL thanh toán VNPay
     */
    public String createPaymentUrl(Order order, String ipAddress) {
        try {
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", version);
            vnpParams.put("vnp_Command", command);
            vnpParams.put("vnp_TmnCode", tmnCode);
            vnpParams.put("vnp_Amount", String.valueOf(order.getTongThanhToan().multiply(new java.math.BigDecimal("100")).longValue()));
            vnpParams.put("vnp_CurrCode", currCode);
            vnpParams.put("vnp_TxnRef", order.getMaDonHang());
            vnpParams.put("vnp_OrderInfo", "Thanh toan don hang " + order.getMaDonHang());
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", returnUrl);
            vnpParams.put("vnp_IpAddr", ipAddress);
            
            // Thêm thời gian
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnpCreateDate = formatter.format(cld.getTime());
            vnpParams.put("vnp_CreateDate", vnpCreateDate);
            
            cld.add(Calendar.MINUTE, 15);
            String vnpExpireDate = formatter.format(cld.getTime());
            vnpParams.put("vnp_ExpireDate", vnpExpireDate);

            // Sắp xếp tham số theo thứ tự alphabet
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);
            
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnpParams.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    // Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            
            String queryUrl = query.toString();
            String vnpSecureHash = hmacSHA512(secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
            String paymentUrl = payUrl + "?" + queryUrl;
            
            log.info("Created VNPay payment URL for order: {}", order.getMaDonHang());
            return paymentUrl;
            
        } catch (Exception e) {
            log.error("Error creating VNPay payment URL", e);
            throw new RuntimeException("Không thể tạo URL thanh toán VNPay", e);
        }
    }

    /**
     * Xác thực kết quả thanh toán từ VNPay
     */
    public boolean validatePayment(Map<String, String> vnpParams) {
        try {
            String vnpSecureHash = vnpParams.get("vnp_SecureHash");
            vnpParams.remove("vnp_SecureHash");
            vnpParams.remove("vnp_SecureHashType");
            
            // Sắp xếp tham số theo thứ tự alphabet
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);
            
            StringBuilder hashData = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnpParams.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }
            
            String secureHash = hmacSHA512(secretKey, hashData.toString());
            return secureHash.equals(vnpSecureHash);
            
        } catch (Exception e) {
            log.error("Error validating VNPay payment", e);
            return false;
        }
    }

    /**
     * Tạo mã hash HMAC SHA512
     */
    private String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hashBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Error creating HMAC SHA512", e);
            throw new RuntimeException("Không thể tạo mã hash", e);
        }
    }

    /**
     * Lấy thông tin kết quả thanh toán
     */
    public Map<String, Object> getPaymentResult(Map<String, String> vnpParams) {
        Map<String, Object> result = new HashMap<>();
        
        String vnpResponseCode = vnpParams.get("vnp_ResponseCode");
        String vnpTransactionStatus = vnpParams.get("vnp_TransactionStatus");
        String vnpTxnRef = vnpParams.get("vnp_TxnRef");
        String vnpAmount = vnpParams.get("vnp_Amount");
        String vnpPayDate = vnpParams.get("vnp_PayDate");
        String vnpTransactionNo = vnpParams.get("vnp_TransactionNo");
        
        result.put("orderId", vnpTxnRef);
        result.put("amount", vnpAmount);
        result.put("payDate", vnpPayDate);
        result.put("transactionNo", vnpTransactionNo);
        result.put("responseCode", vnpResponseCode);
        result.put("transactionStatus", vnpTransactionStatus);
        
        // Kiểm tra kết quả thanh toán
        boolean isValid = validatePayment(vnpParams);
        boolean isSuccess = "00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus) && isValid;
        
        result.put("isValid", isValid);
        result.put("isSuccess", isSuccess);
        
        if (isSuccess) {
            result.put("message", "Thanh toán thành công");
        } else {
            result.put("message", "Thanh toán thất bại");
        }
        
        return result;
    }
}
