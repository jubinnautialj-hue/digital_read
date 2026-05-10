CREATE DATABASE IF NOT EXISTS digital_read DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE digital_read;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    visual_impairment_type VARCHAR(20),
    last_login_time DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    author VARCHAR(100),
    category VARCHAR(50),
    content TEXT,
    file_type VARCHAR(50),
    file_path VARCHAR(500),
    view_count BIGINT NOT NULL DEFAULT 0,
    is_structured BOOLEAN NOT NULL DEFAULT FALSE,
    is_accessible BOOLEAN NOT NULL DEFAULT FALSE,
    created_by BIGINT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    voice_type VARCHAR(20) NOT NULL DEFAULT 'male',
    voice_speed INT NOT NULL DEFAULT 100,
    voice_pitch INT NOT NULL DEFAULT 100,
    text_size VARCHAR(10) NOT NULL DEFAULT 'medium',
    contrast_mode VARCHAR(20) NOT NULL DEFAULT 'normal',
    font VARCHAR(50) NOT NULL DEFAULT 'system',
    line_height INT NOT NULL DEFAULT 150,
    navigation_mode VARCHAR(20) NOT NULL DEFAULT 'keyboard',
    auto_read BOOLEAN NOT NULL DEFAULT FALSE,
    high_contrast BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS reading_activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    reading_mode VARCHAR(100),
    navigation_type VARCHAR(100),
    sections_read VARCHAR(500),
    progress_percent INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (document_id) REFERENCES documents(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS accessibility_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value VARCHAR(500) NOT NULL,
    description VARCHAR(200),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_documents_category ON documents(category);
CREATE INDEX idx_documents_created_by ON documents(created_by);
CREATE INDEX idx_activities_user_id ON reading_activities(user_id);
CREATE INDEX idx_activities_document_id ON reading_activities(document_id);
CREATE INDEX idx_activities_start_time ON reading_activities(start_time);
