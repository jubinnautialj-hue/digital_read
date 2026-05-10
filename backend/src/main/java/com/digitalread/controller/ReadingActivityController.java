package com.digitalread.controller;

import com.digitalread.entity.ReadingActivity;
import com.digitalread.service.ReadingActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activities")
@CrossOrigin(origins = "*")
public class ReadingActivityController {

    @Autowired
    private ReadingActivityService activityService;

    @GetMapping
    public ResponseEntity<?> getAllActivities() {
        try {
            List<ReadingActivity> activities = activityService.getAllActivities();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", activities);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getActivitiesByUser(@PathVariable Long userId) {
        try {
            List<ReadingActivity> activities = activityService.getActivitiesByUserId(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", activities);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<?> getRecentActivities(@PathVariable Long userId,
            @RequestParam(defaultValue = "7") int days) {
        try {
            List<ReadingActivity> activities = activityService.getRecentActivities(userId, days);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", activities);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/start")
    public ResponseEntity<?> startActivity(@RequestBody Map<String, Long> params) {
        try {
            Long userId = params.get("userId");
            Long documentId = params.get("documentId");
            ReadingActivity activity = activityService.startActivity(userId, documentId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", activity);
            result.put("message", "阅读活动已开始");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<?> endActivity(@PathVariable Long id,
            @RequestBody Map<String, Object> params) {
        try {
            Integer progress = params.get("progress") != null 
                ? ((Number) params.get("progress")).intValue() : null;
            String sectionsRead = params.get("sectionsRead") != null 
                ? params.get("sectionsRead").toString() : null;
            ReadingActivity activity = activityService.endActivity(id, progress, sectionsRead);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", activity);
            result.put("message", "阅读活动已结束");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PutMapping("/{id}/progress")
    public ResponseEntity<?> updateProgress(@PathVariable Long id,
            @RequestBody Map<String, Integer> params) {
        try {
            Integer progress = params.get("progress");
            ReadingActivity activity = activityService.updateActivityProgress(id, progress);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", activity);
            result.put("message", "进度已更新");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics() {
        try {
            Map<String, Object> analytics = activityService.getAnalytics();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", analytics);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
