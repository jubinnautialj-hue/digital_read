package com.digitalread.repository;

import com.digitalread.entity.AccessibilitySetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessibilitySettingRepository extends JpaRepository<AccessibilitySetting, Long> {
    Optional<AccessibilitySetting> findBySettingKey(String settingKey);
    List<AccessibilitySetting> findByIsActiveTrue();
    boolean existsBySettingKey(String settingKey);
}
