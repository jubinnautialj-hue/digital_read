package com.digitalread.repository;

import com.digitalread.entity.ReadingActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReadingActivityRepository extends JpaRepository<ReadingActivity, Long> {
    List<ReadingActivity> findByUserId(Long userId);
    List<ReadingActivity> findByDocumentId(Long documentId);
    
    @Query("SELECT ra FROM ReadingActivity ra JOIN FETCH ra.document WHERE ra.user.id = ?1 AND ra.startTime >= ?2")
    List<ReadingActivity> findRecentActivities(Long userId, LocalDateTime since);
    
    @Query("SELECT COUNT(ra) FROM ReadingActivity ra WHERE ra.startTime >= ?1")
    long countActivitiesSince(LocalDateTime since);
    
    @Query("SELECT DISTINCT ra.user.id FROM ReadingActivity ra WHERE ra.startTime >= ?1")
    List<Long> findActiveUserIdsSince(LocalDateTime since);
    
    @Query("SELECT COUNT(ra) FROM ReadingActivity ra WHERE ra.document.id = ?1")
    long countByDocumentId(Long documentId);
}
