package com.digitalread.service;

import com.digitalread.entity.AccessibilitySetting;
import com.digitalread.repository.AccessibilitySettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AccessibilityService {

    @Autowired
    private AccessibilitySettingRepository settingRepository;

    public List<AccessibilitySetting> getAllSettings() {
        return settingRepository.findAll();
    }

    public List<AccessibilitySetting> getActiveSettings() {
        return settingRepository.findByIsActiveTrue();
    }

    public Optional<AccessibilitySetting> getSettingByKey(String key) {
        return settingRepository.findBySettingKey(key);
    }

    public AccessibilitySetting createSetting(AccessibilitySetting setting) {
        if (settingRepository.existsBySettingKey(setting.getSettingKey())) {
            throw new RuntimeException("设置键已存在");
        }
        return settingRepository.save(setting);
    }

    public AccessibilitySetting updateSetting(Long id, AccessibilitySetting settingDetails) {
        return settingRepository.findById(id).map(setting -> {
            if (settingDetails.getSettingKey() != null) setting.setSettingKey(settingDetails.getSettingKey());
            if (settingDetails.getSettingValue() != null) setting.setSettingValue(settingDetails.getSettingValue());
            if (settingDetails.getDescription() != null) setting.setDescription(settingDetails.getDescription());
            if (settingDetails.getIsActive() != null) setting.setIsActive(settingDetails.getIsActive());
            return settingRepository.save(setting);
        }).orElseThrow(() -> new RuntimeException("设置不存在"));
    }

    public void deleteSetting(Long id) {
        settingRepository.deleteById(id);
    }

    public Map<String, Object> performAccessibilityAudit() {
        Map<String, Object> audit = new HashMap<>();
        
        List<AccessibilitySetting> activeSettings = settingRepository.findByIsActiveTrue();
        Map<String, String> settingsMap = new HashMap<>();
        for (AccessibilitySetting s : activeSettings) {
            settingsMap.put(s.getSettingKey(), s.getSettingValue());
        }
        
        audit.put("activeSettings", activeSettings.size());
        audit.put("settings", settingsMap);
        
        Map<String, Object> compliance = new HashMap<>();
        compliance.put("screenReaderSupport", "enabled");
        compliance.put("keyboardNavigation", "enabled");
        compliance.put("textToSpeech", "enabled");
        compliance.put("highContrastMode", "available");
        compliance.put("fontAdjustment", "available");
        audit.put("complianceFeatures", compliance);
        
        List<String> recommendations = new java.util.ArrayList<>();
        recommendations.add("建议定期测试屏幕阅读器兼容性");
        recommendations.add("建议为所有交互元素提供清晰的焦点指示器");
        recommendations.add("建议为所有图片提供替代文本");
        audit.put("recommendations", recommendations);
        
        return audit;
    }

    public Map<String, String> getGlobalAccessibilitySettings() {
        Map<String, String> result = new HashMap<>();
        for (AccessibilitySetting setting : settingRepository.findByIsActiveTrue()) {
            result.put(setting.getSettingKey(), setting.getSettingValue());
        }
        return result;
    }
}
