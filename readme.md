# 数字无障碍阅读与导航系统

一套为盲人和低视力用户打造的数字无障碍阅读与导航系统，提供辅助文档阅读、本地语音合成、键盘操控命令导航等核心功能。

## 系统特性

### 核心功能
- **辅助文档阅读**：支持文本格式文档的结构化展示和阅读
- **本地语音合成**：基于 Web Speech API 的浏览器端文本转语音功能
- **键盘操控命令导航**：全键盘操作，无需鼠标即可完成所有操作
- **无障碍合规审计**：检查和配置系统无障碍设置
- **结构化内容架构**：清晰的层级结构，便于屏幕阅读器导航
- **自适应控制台**：为盲人和低视力用户优化的界面

### 用户角色
- **终端用户（盲人和低视力人群）**：浏览文档、阅读内容、配置个人设置
- **管理员/看护人员**：用户管理、文档管理、数据分析、无障碍配置

### 无障碍特性
- ARIA 语义化标签
- 跳过链接（Skip Links）
- 高对比度模式
- 大字体模式
- 完整键盘导航
- 键盘快捷键提示
- 焦点可见性

## 技术栈

### 后端
- **Spring Boot 2.7.18** - 后端框架
- **Spring Security** - 安全框架
- **Spring Data JPA** - 持久层
- **JWT** - 身份认证
- **MySQL 8.0** - 数据库
- **Apache POI** - 文档处理
- **iText 7** - PDF 处理

### 前端
- **Vue 2.7.14** - 前端框架
- **Vue Router 3.6.5** - 路由管理
- **Vuex 3.6.2** - 状态管理
- **Element UI 2.15.13** - UI 组件库
- **Axios** - HTTP 客户端
- **Web Speech API** - 语音合成

## 项目结构

```
digital_read/
├── backend/                    # 后端项目
│   ├── src/main/java/com/digitalread/
│   │   ├── config/            # 配置类
│   │   ├── controller/        # 控制器
│   │   ├── dto/               # 数据传输对象
│   │   ├── entity/            # 实体类
│   │   ├── repository/        # 数据访问层
│   │   ├── service/           # 业务逻辑层
│   │   ├── util/              # 工具类
│   │   └── DigitalReadApplication.java
│   ├── src/main/resources/
│   │   └── application.yml
│   ├── .mvn/wrapper/
│   ├── mvnw.cmd
│   └── pom.xml
├── frontend/                   # 前端项目
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── api/               # API 接口
│   │   ├── assets/styles/     # 全局样式
│   │   ├── router/            # 路由配置
│   │   ├── store/             # 状态管理
│   │   ├── views/             # 页面组件
│   │   │   ├── admin/         # 管理员页面
│   │   │   └── user/          # 用户页面
│   │   ├── App.vue
│   │   └── main.js
│   ├── babel.config.js
│   ├── package.json
│   └── vue.config.js
├── database/                   # 数据库脚本
│   ├── schema.sql
│   └── data.sql
├── scripts/                    # 启动脚本
│   ├── init-database.bat
│   ├── start-backend.bat
│   └── start-frontend.bat
├── start-all.bat              # 一键启动脚本
└── readme.md
```

## 环境要求

- **JDK**: 17 或更高版本
- **Node.js**: 14.x 或更高版本
- **MySQL**: 8.0 或更高版本
- **Maven**: 3.6.x 或更高版本（可选，项目已包含 Maven Wrapper）

## 快速开始

### 1. 准备数据库

确保 MySQL 服务已启动，并创建数据库。

使用脚本初始化数据库：
```
双击 scripts/init-database.bat
```

或者手动执行：
```sql
CREATE DATABASE IF NOT EXISTS digital_read DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

数据库默认配置：
- 主机: localhost:3306
- 用户名: root
- 密码: 123456

如需修改数据库配置，请编辑 `backend/src/main/resources/application.yml`

### 2. 启动后端服务

方式一：使用启动脚本
```
双击 scripts/start-backend.bat
```

方式二：使用命令行
```bash
cd backend
mvn spring-boot:run
```

或使用 Maven Wrapper（无需安装 Maven）：
```bash
cd backend
mvnw.cmd spring-boot:run
```

后端服务将在 `http://localhost:8080/api` 启动

### 3. 启动前端服务

方式一：使用启动脚本
```
双击 scripts/start-frontend.bat
```

方式二：使用命令行
```bash
cd frontend
npm install
npm run serve
```

前端服务将在 `http://localhost:8081` 启动

### 4. 一键启动（推荐）

如果你已经完成数据库准备，可以直接使用一键启动脚本：

```
双击 start-all.bat
```

此脚本将同时启动后端和前端服务。

## 默认账号

后端首次启动时会自动创建以下用户：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | ADMIN | 系统管理员 |
| user | user123 | USER | 普通测试用户 |

## 键盘快捷键

### 全局快捷键
| 快捷键 | 功能 |
|--------|------|
| Tab | 切换焦点 |
| Enter | 确认/打开 |
| Esc | 取消/关闭 |
| Alt + L | 登录页面快速登录 |
| Alt + S | 聚焦搜索框 |

### 用户端导航快捷键
| 快捷键 | 功能 |
|--------|------|
| Alt + 1 | 用户中心 |
| Alt + 2 | 文档库 |
| Alt + 3 | 个人设置 |
| Alt + Q | 退出登录 |

### 阅读器快捷键
| 快捷键 | 功能 |
|--------|------|
| Space | 播放/暂停语音 |
| → | 下一段 |
| ← | 上一段 |
| ↑ | 加快语速 |
| ↓ | 减慢语速 |
| Esc | 返回文档列表 |

## API 接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `GET /api/auth/ping` - 健康检查

### 用户接口
- `GET /api/users` - 获取所有用户
- `GET /api/users/{id}` - 获取用户详情
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户

### 文档接口
- `GET /api/documents` - 获取所有文档
- `GET /api/documents/accessible` - 获取无障碍文档
- `GET /api/documents/{id}` - 获取文档详情
- `GET /api/documents/search?keyword=` - 搜索文档
- `POST /api/documents` - 创建文档
- `PUT /api/documents/{id}` - 更新文档
- `DELETE /api/documents/{id}` - 删除文档
- `GET /api/documents/stats` - 获取文档统计

### 用户设置接口
- `GET /api/user-settings/user/{userId}` - 获取用户设置
- `PUT /api/user-settings/user/{userId}` - 更新用户设置
- `POST /api/user-settings/user/{userId}/reset` - 重置用户设置

### 阅读活动接口
- `GET /api/activities` - 获取所有活动
- `GET /api/activities/user/{userId}` - 获取用户活动
- `GET /api/activities/user/{userId}/recent` - 获取最近活动
- `POST /api/activities/start` - 开始阅读活动
- `POST /api/activities/{id}/end` - 结束阅读活动
- `PUT /api/activities/{id}/progress` - 更新阅读进度
- `GET /api/activities/analytics` - 获取活动分析

### 无障碍接口
- `GET /api/accessibility/settings` - 获取所有无障碍设置
- `GET /api/accessibility/settings/active` - 获取活动设置
- `POST /api/accessibility/settings` - 创建设置
- `PUT /api/accessibility/settings/{id}` - 更新设置
- `DELETE /api/accessibility/settings/{id}` - 删除设置
- `GET /api/accessibility/audit` - 执行无障碍审计
- `GET /api/accessibility/global-settings` - 获取全局设置

## 功能说明

### 文档阅读器
文档阅读器提供以下功能：
- 文本转语音朗读
- 段落导航
- 语速调节（50%-200%）
- 字体大小调整（12px-32px）
- 阅读进度追踪
- 完整键盘操作支持

### 个人设置
用户可以配置：
- 语音类型（男声/女声）
- 语速
- 音调
- 文字大小
- 字体
- 行高
- 高对比度模式
- 自动朗读开关
- 导航模式

### 管理功能
管理员可以：
- 管理用户（增删改查）
- 管理文档（增删改查）
- 查看阅读数据分析
- 配置全局无障碍设置
- 执行无障碍合规审计

## 无障碍合规

本系统遵循以下无障碍设计原则：

1. **可感知性**
   - 所有内容都有文本替代
   - 高对比度模式支持
   - 可调整的字体大小

2. **可操作性**
   - 全部功能可通过键盘访问
   - 清晰的键盘导航指示
   - 焦点状态可见

3. **可理解性**
   - 清晰的页面结构
   - 一致的交互模式
   - ARIA 语义标签

4. **稳健性**
   - 与主流屏幕阅读器兼容
   - 标准 HTML 语义
   - 向后兼容设计

## 常见问题

### 1. 后端启动失败
- 检查 MySQL 服务是否启动
- 检查数据库连接配置（application.yml）
- 检查 Maven 依赖是否下载完成

### 2. 前端启动失败
- 检查 Node.js 版本（需要 14.x 以上）
- 删除 node_modules 后重新执行 npm install
- 检查 8081 端口是否被占用

### 3. 登录失败
- 检查数据库是否正确初始化
- 确认用户账号是否已创建
- 检查后端服务是否正常运行

### 4. 语音合成不工作
- 确保使用支持 Web Speech API 的浏览器
- Chrome、Edge、Safari 都支持该功能
- Firefox 支持有限

### 5. 高对比度模式不生效
- 登录后在个人设置中开启
- 或者在用户端布局中点击"开启高对比度"按钮

## 开发说明

### 后端开发
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

### 前端开发
```bash
cd frontend
npm install
npm run serve
```

### 打包部署
后端打包：
```bash
cd backend
mvn clean package
java -jar target/digital-read-backend-1.0.0.jar
```

前端打包：
```bash
cd frontend
npm run build
```

## 浏览器兼容性

推荐使用以下浏览器以获得最佳无障碍体验：
- Google Chrome 80+
- Microsoft Edge 80+
- Mozilla Firefox 75+
- Apple Safari 13+

## 许可证

本项目仅供学习和研究使用。

## 联系方式

如有问题，请联系系统管理员。
