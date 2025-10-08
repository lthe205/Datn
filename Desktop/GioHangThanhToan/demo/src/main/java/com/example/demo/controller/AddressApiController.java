package com.example.demo.controller;

import com.example.demo.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
@Slf4j
public class AddressApiController {

    private final AddressService addressService;

    /**
     * Lấy danh sách tất cả tỉnh/thành phố
     */
    @GetMapping("/provinces")
    public ResponseEntity<Map<String, Object>> getAllProvinces() {
        try {
            List<AddressService.Province> provinces = addressService.getAllProvinces();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", provinces);
            response.put("message", "Lấy danh sách tỉnh thành thành công");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting provinces", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Có lỗi xảy ra khi lấy danh sách tỉnh thành"));
        }
    }

    /**
     * Lấy danh sách quận/huyện theo mã tỉnh
     */
    @GetMapping("/districts/{provinceCode}")
    public ResponseEntity<Map<String, Object>> getDistrictsByProvince(@PathVariable String provinceCode) {
        try {
            List<AddressService.District> districts = addressService.getDistrictsByProvinceCode(provinceCode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", districts);
            response.put("message", "Lấy danh sách quận/huyện thành công");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting districts for province {}", provinceCode, e);
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Có lỗi xảy ra khi lấy danh sách quận/huyện"));
        }
    }

    /**
     * Lấy danh sách phường/xã theo mã quận/huyện
     */
    @GetMapping("/wards/{districtCode}")
    public ResponseEntity<Map<String, Object>> getWardsByDistrict(@PathVariable String districtCode) {
        try {
            List<AddressService.Ward> wards = addressService.getWardsByDistrictCode(districtCode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", wards);
            response.put("message", "Lấy danh sách phường/xã thành công");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting wards for district {}", districtCode, e);
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Có lỗi xảy ra khi lấy danh sách phường/xã"));
        }
    }

    /**
     * Lấy thông tin chi tiết địa chỉ
     */
    @GetMapping("/detail")
    public ResponseEntity<Map<String, Object>> getAddressDetail(
            @RequestParam String provinceCode,
            @RequestParam String districtCode,
            @RequestParam String wardCode) {
        try {
            AddressService.AddressDetail detail = addressService.getAddressDetail(provinceCode, districtCode, wardCode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", detail);
            response.put("message", "Lấy thông tin địa chỉ thành công");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting address detail", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Có lỗi xảy ra khi lấy thông tin địa chỉ"));
        }
    }
}
