package com.digitalread.controller;

import com.digitalread.entity.UserSettings;
import com.digitalread.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user-settings")
@CrossOrigin(origins = "*")
public class UserSettingsController {

    @Autowired
    private UserSettingsService settingsService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getSettingsByUserId(@PathVariable Long userId) {
        try {
            UserSettings settings = settingsService.getOrCreateSettings(userId);
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

    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateSettings(@PathVariable Long userId,
            @RequestBody UserSettings settingsDetails) {
        try {
            UserSettings updatedSettings = settingsService.updateSettings(userId, settingsDetails);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", updatedSettings);
            result.put("message", "更新成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/user/{userId}/reset")
    public ResponseEntity<?> resetSettings(@PathVariable Long userId) {
        try {
            settingsService.resetSettings(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "设置已重置为默认值");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
