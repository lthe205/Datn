package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String PROVINCES_API_BASE = "https://provinces.open-api.vn/api";

    /**
     * Lấy danh sách tất cả tỉnh/thành phố
     */
    public List<Province> getAllProvinces() {
        try {
            String url = PROVINCES_API_BASE + "/p/";
            String response = restTemplate.getForObject(url, String.class);
            
            JsonNode jsonNode = objectMapper.readTree(response);
            List<Province> provinces = new ArrayList<>();
            
            for (JsonNode node : jsonNode) {
                Province province = new Province();
                province.setCode(node.get("code").asText());
                province.setName(node.get("name").asText());
                provinces.add(province);
            }
            
            log.info("Loaded {} provinces", provinces.size());
            return provinces;
            
        } catch (Exception e) {
            log.error("Error loading provinces", e);
            return new ArrayList<>();
        }
    }

    /**
     * Lấy danh sách quận/huyện theo mã tỉnh
     */
    public List<District> getDistrictsByProvinceCode(String provinceCode) {
        try {
            String url = PROVINCES_API_BASE + "/p/" + provinceCode + "?depth=2";
            String response = restTemplate.getForObject(url, String.class);
            
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode districtsNode = jsonNode.get("districts");
            
            List<District> districts = new ArrayList<>();
            if (districtsNode != null && districtsNode.isArray()) {
                for (JsonNode node : districtsNode) {
                    District district = new District();
                    district.setCode(node.get("code").asText());
                    district.setName(node.get("name").asText());
                    district.setProvinceCode(provinceCode);
                    districts.add(district);
                }
            }
            
            log.info("Loaded {} districts for province {}", districts.size(), provinceCode);
            return districts;
            
        } catch (Exception e) {
            log.error("Error loading districts for province {}", provinceCode, e);
            return new ArrayList<>();
        }
    }

    /**
     * Lấy danh sách phường/xã theo mã quận/huyện
     */
    public List<Ward> getWardsByDistrictCode(String districtCode) {
        try {
            String url = PROVINCES_API_BASE + "/d/" + districtCode + "?depth=2";
            String response = restTemplate.getForObject(url, String.class);
            
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode wardsNode = jsonNode.get("wards");
            
            List<Ward> wards = new ArrayList<>();
            if (wardsNode != null && wardsNode.isArray()) {
                for (JsonNode node : wardsNode) {
                    Ward ward = new Ward();
                    ward.setCode(node.get("code").asText());
                    ward.setName(node.get("name").asText());
                    ward.setDistrictCode(districtCode);
                    wards.add(ward);
                }
            }
            
            log.info("Loaded {} wards for district {}", wards.size(), districtCode);
            return wards;
            
        } catch (Exception e) {
            log.error("Error loading wards for district {}", districtCode, e);
            return new ArrayList<>();
        }
    }

    /**
     * Lấy thông tin chi tiết địa chỉ theo mã
     */
    public AddressDetail getAddressDetail(String provinceCode, String districtCode, String wardCode) {
        try {
            AddressDetail detail = new AddressDetail();
            
            // Lấy thông tin tỉnh
            String provinceUrl = PROVINCES_API_BASE + "/p/" + provinceCode;
            String provinceResponse = restTemplate.getForObject(provinceUrl, String.class);
            JsonNode provinceNode = objectMapper.readTree(provinceResponse);
            detail.setProvinceName(provinceNode.get("name").asText());
            
            // Lấy thông tin quận/huyện
            String districtUrl = PROVINCES_API_BASE + "/d/" + districtCode;
            String districtResponse = restTemplate.getForObject(districtUrl, String.class);
            JsonNode districtNode = objectMapper.readTree(districtResponse);
            detail.setDistrictName(districtNode.get("name").asText());
            
            // Lấy thông tin phường/xã
            String wardUrl = PROVINCES_API_BASE + "/w/" + wardCode;
            String wardResponse = restTemplate.getForObject(wardUrl, String.class);
            JsonNode wardNode = objectMapper.readTree(wardResponse);
            detail.setWardName(wardNode.get("name").asText());
            
            return detail;
            
        } catch (Exception e) {
            log.error("Error getting address detail", e);
            return null;
        }
    }

    // DTO classes
    public static class Province {
        private String code;
        private String name;
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class District {
        private String code;
        private String name;
        private String provinceCode;
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getProvinceCode() { return provinceCode; }
        public void setProvinceCode(String provinceCode) { this.provinceCode = provinceCode; }
    }

    public static class Ward {
        private String code;
        private String name;
        private String districtCode;
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDistrictCode() { return districtCode; }
        public void setDistrictCode(String districtCode) { this.districtCode = districtCode; }
    }

    public static class AddressDetail {
        private String provinceName;
        private String districtName;
        private String wardName;
        
        public String getProvinceName() { return provinceName; }
        public void setProvinceName(String provinceName) { this.provinceName = provinceName; }
        public String getDistrictName() { return districtName; }
        public void setDistrictName(String districtName) { this.districtName = districtName; }
        public String getWardName() { return wardName; }
        public void setWardName(String wardName) { this.wardName = wardName; }
        
        public String getFullAddress() {
            return wardName + ", " + districtName + ", " + provinceName;
        }
    }
}
