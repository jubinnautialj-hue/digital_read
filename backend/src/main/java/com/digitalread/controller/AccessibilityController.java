package com.digitalread.controller;

import com.digitalread.entity.AccessibilitySetting;
import com.digitalread.service.AccessibilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accessibility")
@CrossOrigin(origins = "*")
public class AccessibilityController {

    @Autowired
    private AccessibilityService accessibilityService;

    @GetMapping("/settings")
    public ResponseEntity<?> getAllSettings() {
        try {
            List<AccessibilitySetting> settings = accessibilityService.getAllSettings();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", settings);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/settings/active")
    public ResponseEntity<?> getActiveSettings() {
        try {
            List<AccessibilitySetting> settings = accessibilityService.getActiveSettings();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", settings);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/settings")
    public ResponseEntity<?> createSetting(@RequestBody AccessibilitySetting setting) {
        try {
            AccessibilitySetting savedSetting = accessibilityService.createSetting(setting);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", savedSetting);
            result.put("message", "创建成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PutMapping("/settings/{id}")
    public ResponseEntity<?> updateSetting(@PathVariable Long id,
            @RequestBody AccessibilitySetting settingDetails) {
        try {
            AccessibilitySetting updatedSetting = accessibilityService.updateSetting(id, settingDetails);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", updatedSetting);
            result.put("message", "更新成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/settings/{id}")
    public ResponseEntity<?> deleteSetting(@PathVariable Long id) {
        try {
            accessibilityService.deleteSetting(id);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/audit")
    public ResponseEntity<?> performAudit() {
        try {
            Map<String, Object> audit = accessibilityService.performAccessibilityAudit();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", audit);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/global-settings")
    public ResponseEntity<?> getGlobalSettings() {
        try {
            Map<String, String> settings = accessibilityService.getGlobalAccessibilitySettings();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", settings);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
