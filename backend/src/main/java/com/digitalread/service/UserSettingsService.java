package com.digitalread.service;

import com.digitalread.entity.User;
import com.digitalread.entity.UserSettings;
import com.digitalread.repository.UserRepository;
import com.digitalread.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserSettingsService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<UserSettings> getSettingsByUserId(Long userId) {
        return userSettingsRepository.findByUserId(userId);
    }

    public UserSettings getOrCreateSettings(Long userId) {
        return userSettingsRepository.findByUserId(userId).orElseGet(() -> {
            UserSettings settings = new UserSettings();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            settings.setUser(user);
            return userSettingsRepository.save(settings);
        });
    }

    public UserSettings updateSettings(Long userId, UserSettings settingsDetails) {
        UserSettings settings = getOrCreateSettings(userId);
        
        if (settingsDetails.getVoiceType() != null) settings.setVoiceType(settingsDetails.getVoiceType());
        if (settingsDetails.getVoiceSpeed() != null) settings.setVoiceSpeed(settingsDetails.getVoiceSpeed());
        if (settingsDetails.getVoicePitch() != null) settings.setVoicePitch(settingsDetails.getVoicePitch());
        if (settingsDetails.getTextSize() != null) settings.setTextSize(settingsDetails.getTextSize());
        if (settingsDetails.getContrastMode() != null) settings.setContrastMode(settingsDetails.getContrastMode());
        if (settingsDetails.getFont() != null) settings.setFont(settingsDetails.getFont());
        if (settingsDetails.getLineHeight() != null) settings.setLineHeight(settingsDetails.getLineHeight());
        if (settingsDetails.getNavigationMode() != null) settings.setNavigationMode(settingsDetails.getNavigationMode());
        if (settingsDetails.getAutoRead() != null) settings.setAutoRead(settingsDetails.getAutoRead());
        if (settingsDetails.getHighContrast() != null) settings.setHighContrast(settingsDetails.getHighContrast());
        
        return userSettingsRepository.save(settings);
    }

    public void resetSettings(Long userId) {
        userSettingsRepository.findByUserId(userId).ifPresent(settings -> {
            settings.setVoiceType("male");
            settings.setVoiceSpeed(100);
            settings.setVoicePitch(100);
            settings.setTextSize("medium");
            settings.setContrastMode("normal");
            settings.setFont("system");
            settings.setLineHeight(150);
            settings.setNavigationMode("keyboard");
            settings.setAutoRead(false);
            settings.setHighContrast(false);
            userSettingsRepository.save(settings);
        });
    }
}
