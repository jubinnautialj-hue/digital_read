package com.digitalread.config;

import com.digitalread.entity.*;
import com.digitalread.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private AccessibilitySettingRepository accessibilitySettingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        initAdminUser();
        initTestUser();
        initDocuments();
        initAccessibilitySettings();
    }

    private void initAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@digitalread.com");
            admin.setRealName("系统管理员");
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            userRepository.save(admin);
            System.out.println("默认管理员用户已创建: admin / admin123");
        }
    }

    private void initTestUser() {
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@digitalread.com");
            user.setRealName("测试用户");
            user.setRole("USER");
            user.setEnabled(true);
            user.setVisualImpairmentType("BLIND");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            User savedUser = userRepository.save(user);
            
            UserSettings settings = new UserSettings();
            settings.setUser(savedUser);
            settings.setCreatedAt(LocalDateTime.now());
            settings.setUpdatedAt(LocalDateTime.now());
            userSettingsRepository.save(settings);
            
            System.out.println("测试用户已创建: user / user123");
        }
    }

    private void initDocuments() {
        if (documentRepository.count() == 0) {
            Document doc1 = new Document();
            doc1.setTitle("数字无障碍阅读系统使用指南");
            doc1.setDescription("本指南帮助用户了解如何使用数字无障碍阅读系统的各项功能");
            doc1.setAuthor("系统管理员");
            doc1.setCategory("使用指南");
            doc1.setContent("第一章：系统介绍\n\n数字无障碍阅读系统是为盲人和低视力用户设计的文档阅读平台。本系统提供以下核心功能：\n\n1. 辅助文档阅读 - 支持多种文档格式\n2. 本地语音合成 - 文本转语音功能\n3. 键盘操控命令导航 - 无需鼠标操作\n4. 无障碍合规审计 - 检查无障碍状态\n5. 结构化内容架构 - 清晰的内容层级\n\n第二章：登录系统\n\n使用用户名和密码登录系统。登录后可以：\n- 浏览文档库\n- 设置个人偏好\n- 开始阅读\n\n第三章：键盘快捷键\n\nTab键：切换焦点\nEnter键：确认/打开\nEsc键：取消/返回\n方向键：导航菜单\n空格键：激活按钮");
            doc1.setFileType("TEXT");
            doc1.setStructured(true);
            doc1.setAccessible(true);
            doc1.setViewCount(0L);
            doc1.setCreatedAt(LocalDateTime.now());
            doc1.setUpdatedAt(LocalDateTime.now());
            documentRepository.save(doc1);

            Document doc2 = new Document();
            doc2.setTitle("无障碍设计最佳实践");
            doc2.setDescription("介绍数字产品无障碍设计的标准和最佳实践");
            doc2.setAuthor("无障碍专家");
            doc2.setCategory("技术文档");
            doc2.setContent("无障碍设计原则\n\n1. 可感知性\n   - 提供文本替代\n   - 提供多媒体替代\n   - 创建可适应的内容\n   - 区分前景和背景\n\n2. 可操作性\n   - 全部功能可通过键盘访问\n   - 提供足够的时间阅读内容\n   - 避免引起癫痫发作的内容\n   - 提供导航方式\n\n3. 可理解性\n   - 文本可读性和可理解性\n   - 网页内容以可预测的方式运行\n   - 帮助用户避免和纠正错误\n\n4. 稳健性\n   - 最大化与当前和未来的用户代理兼容性");
            doc2.setFileType("TEXT");
            doc2.setStructured(true);
            doc2.setAccessible(true);
            doc2.setViewCount(0L);
            doc2.setCreatedAt(LocalDateTime.now());
            doc2.setUpdatedAt(LocalDateTime.now());
            documentRepository.save(doc2);

            Document doc3 = new Document();
            doc3.setTitle("屏幕阅读器使用基础教程");
            doc3.setDescription("帮助新手用户掌握屏幕阅读器的基本操作");
            doc3.setAuthor("培训师");
            doc3.setCategory("教程");
            doc3.setContent("屏幕阅读器是盲人和低视力用户访问计算机的重要工具。\n\n常用屏幕阅读器：\n- NVDA（免费开源）\n- JAWS（商业软件）\n- VoiceOver（Mac/iOS内置）\n\n基本操作：\n- 读取当前行：Insert+Down\n- 读取下一行：Down\n- 读取前一行：Up\n- 读取全部：Insert+Down (长按)\n- 停止读取：Ctrl\n\n导航技巧：\n- 使用标题导航（H1-H6）\n- 使用链接列表\n- 使用表单控件导航\n- 学习常用快捷键");
            doc3.setFileType("TEXT");
            doc3.setStructured(true);
            doc3.setAccessible(true);
            doc3.setViewCount(0L);
            doc3.setCreatedAt(LocalDateTime.now());
            doc3.setUpdatedAt(LocalDateTime.now());
            documentRepository.save(doc3);

            System.out.println("示例文档已创建");
        }
    }

    private void initAccessibilitySettings() {
        String[][] settings = {
            {"screen_reader_support", "enabled", "屏幕阅读器支持"},
            {"keyboard_navigation", "enabled", "键盘导航"},
            {"text_to_speech", "enabled", "文本转语音"},
            {"high_contrast", "available", "高对比度模式"},
            {"font_adjustment", "available", "字体调整"},
            {"line_height_adjustment", "available", "行高调整"},
            {"skip_links", "enabled", "跳过链接"},
            {"aria_labels", "enforced", "ARIA标签"}
        };

        for (String[] setting : settings) {
            if (!accessibilitySettingRepository.existsBySettingKey(setting[0])) {
                AccessibilitySetting as = new AccessibilitySetting();
                as.setSettingKey(setting[0]);
                as.setSettingValue(setting[1]);
                as.setDescription(setting[2]);
                as.setActive(true);
                as.setCreatedAt(LocalDateTime.now());
                as.setUpdatedAt(LocalDateTime.now());
                accessibilitySettingRepository.save(as);
            }
        }
        System.out.println("无障碍设置已初始化");
    }
}
