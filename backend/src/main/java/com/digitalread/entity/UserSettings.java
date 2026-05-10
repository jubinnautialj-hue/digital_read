package com.digitalread.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_settings")
public class UserSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 20)
    private String voiceType = "male";

    @Column(nullable = false)
    private Integer voiceSpeed = 100;

    @Column(nullable = false)
    private Integer voicePitch = 100;

    @Column(nullable = false, length = 10)
    private String textSize = "medium";

    @Column(nullable = false, length = 20)
    private String contrastMode = "normal";

    @Column(nullable = false, length = 50)
    private String font = "system";

    @Column(nullable = false)
    private Integer lineHeight = 150;

    @Column(nullable = false, length = 20)
    private String navigationMode = "keyboard";

    @Column(nullable = false)
    private Boolean autoRead = false;

    @Column(nullable = false)
    private Boolean highContrast = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(String voiceType) {
        this.voiceType = voiceType;
    }

    public Integer getVoiceSpeed() {
        return voiceSpeed;
    }

    public void setVoiceSpeed(Integer voiceSpeed) {
        this.voiceSpeed = voiceSpeed;
    }

    public Integer getVoicePitch() {
        return voicePitch;
    }

    public void setVoicePitch(Integer voicePitch) {
        this.voicePitch = voicePitch;
    }

    public String getTextSize() {
        return textSize;
    }

    public void setTextSize(String textSize) {
        this.textSize = textSize;
    }

    public String getContrastMode() {
        return contrastMode;
    }

    public void setContrastMode(String contrastMode) {
        this.contrastMode = contrastMode;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public Integer getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(Integer lineHeight) {
        this.lineHeight = lineHeight;
    }

    public String getNavigationMode() {
        return navigationMode;
    }

    public void setNavigationMode(String navigationMode) {
        this.navigationMode = navigationMode;
    }

    public Boolean getAutoRead() {
        return autoRead;
    }

    public void setAutoRead(Boolean autoRead) {
        this.autoRead = autoRead;
    }

    public Boolean getHighContrast() {
        return highContrast;
    }

    public void setHighContrast(Boolean highContrast) {
        this.highContrast = highContrast;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
