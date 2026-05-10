package com.digitalread.service;

import com.digitalread.entity.Document;
import com.digitalread.entity.ReadingActivity;
import com.digitalread.entity.User;
import com.digitalread.repository.DocumentRepository;
import com.digitalread.repository.ReadingActivityRepository;
import com.digitalread.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ReadingActivityService {

    @Autowired
    private ReadingActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;

    public List<ReadingActivity> getActivitiesByUserId(Long userId) {
        return activityRepository.findByUserId(userId);
    }

    public List<ReadingActivity> getRecentActivities(Long userId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return activityRepository.findRecentActivities(userId, since);
    }

    public ReadingActivity startActivity(Long userId, Long documentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("文档不存在"));
        
        ReadingActivity activity = new ReadingActivity();
        activity.setUser(user);
        activity.setDocument(document);
        activity.setStartTime(LocalDateTime.now());
        
        return activityRepository.save(activity);
    }

    public ReadingActivity endActivity(Long activityId, Integer progress, String sectionsRead) {
        return activityRepository.findById(activityId).map(activity -> {
            activity.setEndTime(LocalDateTime.now());
            if (progress != null) activity.setProgressPercent(progress);
            if (sectionsRead != null) activity.setSectionsRead(sectionsRead);
            return activityRepository.save(activity);
        }).orElseThrow(() -> new RuntimeException("活动不存在"));
    }

    public ReadingActivity updateActivityProgress(Long activityId, Integer progress) {
        return activityRepository.findById(activityId).map(activity -> {
            activity.setProgressPercent(progress);
            return activityRepository.save(activity);
        }).orElseThrow(() -> new RuntimeException("活动不存在"));
    }

    public Map<String, Object> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime weekStart = todayStart.minusDays(7);
        LocalDateTime monthStart = todayStart.minusDays(30);
        
        analytics.put("todayActivities", activityRepository.countActivitiesSince(todayStart));
        analytics.put("weekActivities", activityRepository.countActivitiesSince(weekStart));
        analytics.put("monthActivities", activityRepository.countActivitiesSince(monthStart));
        
        analytics.put("todayActiveUsers", activityRepository.findActiveUserIdsSince(todayStart).size());
        analytics.put("weekActiveUsers", activityRepository.findActiveUserIdsSince(weekStart).size());
        analytics.put("monthActiveUsers", activityRepository.findActiveUserIdsSince(monthStart).size());
        
        return analytics;
    }

    public long getActivityCountForDocument(Long documentId) {
        return activityRepository.countByDocumentId(documentId);
    }

    public Optional<ReadingActivity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }

    public List<ReadingActivity> getAllActivities() {
        return activityRepository.findAll();
    }
}
