USE digital_read;

INSERT INTO users (username, password, email, real_name, role, enabled, created_at, updated_at)
VALUES (
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',
    'admin@digitalread.com',
    '系统管理员',
    'ADMIN',
    TRUE,
    NOW(),
    NOW()
);

INSERT INTO users (username, password, email, real_name, role, enabled, visual_impairment_type, created_at, updated_at)
VALUES (
    'user',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',
    'user@digitalread.com',
    '测试用户',
    'USER',
    TRUE,
    'BLIND',
    NOW(),
    NOW()
);

INSERT INTO documents (title, description, author, category, content, file_type, is_structured, is_accessible, view_count, created_at, updated_at)
VALUES (
    '数字无障碍阅读系统使用指南',
    '本指南帮助用户了解如何使用数字无障碍阅读系统的各项功能',
    '系统管理员',
    '使用指南',
    '第一章：系统介绍

数字无障碍阅读系统是为盲人和低视力用户设计的文档阅读平台。本系统提供以下核心功能：

1. 辅助文档阅读 - 支持多种文档格式
2. 本地语音合成 - 文本转语音功能
3. 键盘操控命令导航 - 无需鼠标操作
4. 无障碍合规审计 - 检查无障碍状态
5. 结构化内容架构 - 清晰的内容层级

第二章：登录系统

使用用户名和密码登录系统。登录后可以：
- 浏览文档库
- 设置个人偏好
- 开始阅读

第三章：键盘快捷键

Tab键：切换焦点
Enter键：确认/打开
Esc键：取消/返回
方向键：导航菜单
空格键：激活按钮',
    'TEXT',
    TRUE,
    TRUE,
    0,
    NOW(),
    NOW()
);

INSERT INTO documents (title, description, author, category, content, file_type, is_structured, is_accessible, view_count, created_at, updated_at)
VALUES (
    '无障碍设计最佳实践',
    '介绍数字产品无障碍设计的标准和最佳实践',
    '无障碍专家',
    '技术文档',
    '无障碍设计原则

1. 可感知性
   - 提供文本替代
   - 提供多媒体替代
   - 创建可适应的内容
   - 区分前景和背景

2. 可操作性
   - 全部功能可通过键盘访问
   - 提供足够的时间阅读内容
   - 避免引起癫痫发作的内容
   - 提供导航方式

3. 可理解性
   - 文本可读性和可理解性
   - 网页内容以可预测的方式运行
   - 帮助用户避免和纠正错误

4. 稳健性
   - 最大化与当前和未来的用户代理兼容性',
    'TEXT',
    TRUE,
    TRUE,
    0,
    NOW(),
    NOW()
);

INSERT INTO documents (title, description, author, category, content, file_type, is_structured, is_accessible, view_count, created_at, updated_at)
VALUES (
    '屏幕阅读器使用基础教程',
    '帮助新手用户掌握屏幕阅读器的基本操作',
    '培训师',
    '教程',
    '屏幕阅读器是盲人和低视力用户访问计算机的重要工具。

常用屏幕阅读器：
- NVDA（免费开源）
- JAWS（商业软件）
- VoiceOver（Mac/iOS内置）

基本操作：
- 读取当前行：Insert+Down
- 读取下一行：Down
- 读取前一行：Up
- 读取全部：Insert+Down (长按)
- 停止读取：Ctrl

导航技巧：
- 使用标题导航（H1-H6）
- 使用链接列表
- 使用表单控件导航
- 学习常用快捷键',
    'TEXT',
    TRUE,
    TRUE,
    0,
    NOW(),
    NOW()
);

INSERT INTO accessibility_settings (setting_key, setting_value, description, is_active, created_at, updated_at)
VALUES 
('screen_reader_support', 'enabled', '屏幕阅读器支持', TRUE, NOW(), NOW()),
('keyboard_navigation', 'enabled', '键盘导航', TRUE, NOW(), NOW()),
('text_to_speech', 'enabled', '文本转语音', TRUE, NOW(), NOW()),
('high_contrast', 'available', '高对比度模式', TRUE, NOW(), NOW()),
('font_adjustment', 'available', '字体调整', TRUE, NOW(), NOW()),
('line_height_adjustment', 'available', '行高调整', TRUE, NOW(), NOW()),
('skip_links', 'enabled', '跳过链接', TRUE, NOW(), NOW()),
('aria_labels', 'enforced', 'ARIA标签', TRUE, NOW(), NOW());
