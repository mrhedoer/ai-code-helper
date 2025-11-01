# 🤖 AI 编程小助手

> 基于 Spring Boot + Vue3 + LangChain4j + 通义千问 的智能编程学习与求职辅导助手

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.3.4-4FC08D.svg)](https://vuejs.org/)
[![LangChain4j](https://img.shields.io/badge/LangChain4j-1.1.0-blue.svg)](https://github.com/langchain4j/langchain4j)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)

## ✨ 核心特性

| 特性 | 说明 |
|------|------|
| 🤖 **AI 能力** | 集成 LangChain4j + 通义千问，支持对话、嵌入、流式输出 |
| ⚡ **实时交互** | SSE 流式输出，打字机效果，响应迅速 |
| 🛡️ **安全防护** | 输入安全检测（Guardrail），智能拦截敏感内容 |
| 🔧 **工具增强** | RAG 检索、本地知识库、MCP 协议、面试题库 |
| 📊 **管理后台** | 数据统计、查询、搜索、删除等完整管理功能 |

## 📸 功能预览

<table>
  <tr>
    <td align="center"><b>前端界面</b><br/><img src="img_5.png" width="200"/></td>
    <td align="center"><b>流式输出</b><br/><img src="img_8.png" width="200"/></td>
    <td align="center"><b>上下文对话</b><br/><img src="img_6.png" width="200"/></td>
    <td align="center"><b>后台界面</b><br/><img src="img_4.png" width="200"/></td>
  </tr>
</table>

## 📁 项目结构

```
ai-code-helper/
├─ ai-code-helper-frontend/            # 前端（Vue3 + Vite）
│  ├─ src/
│  │  ├─ components/
│  │  │  ├─ ChatRoom.vue             # 聊天室组件
│  │  │  ├─ MessageInput.vue         # 消息输入组件
│  │  │  ├─ AdminLogin.vue           # 管理员登录页面
│  │  │  └─ AdminDashboard.vue       # 管理员后台页面
│  │  ├─ router/
│  │  │  └─ index.js                 # 路由配置
│  │  ├─ utils/
│  │  ├─ App.vue
│  │  └─ main.js
│  └─ README.md
├─ src/main/java/com/hejunhao/aicodehelper/   # 后端（Spring Boot）
│  ├─ AiController.java                # SSE `GET /ai/chat`
│  ├─ ChatController.java             # 其它REST接口
│  ├─ AdminController.java            # 管理员接口
│  ├─ AiCodeHelperService.java        # 会话接口定义
│  ├─ AiCodeHelperServiceFactory.java # 装配AiServices
│  ├─ AiCodeHelperApplication.java    # 启动类
│  ├─ AiCodeHelper.java               # 简单示例服务
│  ├─ MySqlChatMemoryStore.java       # 会话记忆存储
│  ├─ ChatMessageEntity.java          # JPA实体
│  ├─ ChatMessageRepository.java      # JPA仓库
│  ├─ config/                         # CORS、RAG等配置
│  ├─ listener/                       # 模型监听
│  ├─ guardrail/                      # 安全防护
│  ├─ mcp/                            # MCP 客户端与工具
│  ├─ model/                          # 模型装配（如 `QwenChatModelConfig`）
│  └─ tools/                          # 工具集成（面试题等）
├─ src/main/resources/                 # `application.yml`、静态资源、系统提示词
├─ pom.xml
└─ README.md
```


## ⚙️ 环境要求

| 环境 | 版本要求 |
|------|----------|
| Java | 21+ |
| Maven | 3.6+ |
| Node.js | 16+ |
| MySQL | 5.7+ (可选) |
| API Key | 通义千问 / 智谱 GLM |

## 🚀 快速开始

### 1️⃣ 克隆项目

```bash
git clone <repository-url>
cd ai-code-helper
```

### 2️⃣ 配置后端

创建 `src/main/resources/application.yml`，配置必要参数：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_chat_db
    username: root
    password: 123456

langchain4j:
  community:
    dashscope:
      chat-model:
        api-key: <YOUR_DASHSCOPE_API_KEY>  # 通义千问密钥

server:
  port: 8081
  servlet:
    context-path: /api
```

> 💡 **提示**：完整配置示例请查看项目中的 `application.yml.example`

### 3️⃣ 启动服务

**启动后端：**
```bash
mvn spring-boot:run
```

**启动前端：**
```bash
cd ai-code-helper-frontend
npm install
npm run dev
```

### 4️⃣ 访问应用

| 服务 | 地址 | 说明 |
|------|------|------|
| 🎨 **前端** | http://localhost:3000 | 聊天界面 |
| 🔌 **后端** | http://localhost:8081/api | API 服务 |
| 👨‍💼 **管理后台** | 点击首页"管理员入口" | 用户名: `hejunhao` / 密码: `123456` |


## 🧩 技术架构

```
┌─────────────────┐    ┌─────────────────┐
│   Vue.js 前端    │────│  Spring Boot   │
│   - 聊天界面     │    │    后端服务      │
│   - 实时流式     │    │   - RESTful API │
│   - Markdown    │    │   - SSE 推送     │
└─────────────────┘    └─────────────────┘
                              │
                    ┌─────────────────┐
                    │   LangChain4j   │
                    │   - AI服务层    │
                    │   - 工具集成    │
                    │   - 安全防护    │
                    └─────────────────┘
                              │
                    ┌─────────────────┐
                    │   通义千问API    │
                    │   - 对话模型    │
                    │   - 嵌入模型    │
                    │   - 流式输出    │
                    └─────────────────┘
```


## 🔌 API 接口

**基础地址：** `http://localhost:8081/api`

### 💬 聊天接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/ai/chat` | GET (SSE) | 流式对话接口，参数：`memoryId`、`message` |

### 👨‍💼 管理员接口

| 接口 | 方法 | 说明 | 鉴权 |
|------|------|------|------|
| `/admin/login` | POST | 管理员登录 | - |
| `/admin/messages` | GET | 获取所有消息 | ✓ |
| `/admin/messages/search` | GET | 搜索消息 | ✓ |
| `/admin/messages/{id}` | DELETE | 删除单条消息 | ✓ |
| `/admin/conversations/{id}` | DELETE | 删除整个会话 | ✓ |

> 🔑 需要鉴权的接口请在请求头添加 `Authorization: <token>`

## 🔐 管理后台

### ✨ 主要功能

- **🔐 登录认证**：用户名/密码验证，Token-based 身份验证
- **📊 数据统计**：总消息数、会话数、用户/AI消息统计
- **🔍 智能搜索**：支持按会话ID或消息内容快速搜索
- **📝 数据展示**：表格形式展示所有聊天记录
- **🗑️ 数据管理**：支持删除单条消息或整个会话
- **🛡️ 安全保护**：路由守卫 + Token验证

### 📝 使用指南

1. 点击首页右上角 **"管理员入口"** 按钮
2. 使用默认凭证登录（用户名：`hejunhao` / 密码：`123456`）
3. 登录成功后即可进行数据管理操作

### ⚠️ 安全提示

> **生产环境必做：**
> - 修改默认账号密码
> - 使用加密算法（如 BCrypt）
> - 考虑使用 JWT 替代简单 Token
> - 添加操作日志和请求频率限制

## 🧠 核心模块

| 模块 | 说明 |
|------|------|
| `AiCodeHelperService` | 核心对话与编排 |
| `QwenChatModelConfig` | 通义千问模型配置 |
| `MySqlChatMemoryStore` | 会话记忆存储 |
| `SafeInputGuardrail` | 输入安全防护 |
| `InterviewQuestionTool` | 面试题搜索工具 |
| `AdminController` | 管理员后台接口 |


## 📦 常用命令

<table>
<tr>
<td width="50%">

**后端命令**
```bash
# 编译打包
mvn clean package

# 启动服务
mvn spring-boot:run

# 跳过测试
mvn clean install -DskipTests
```

</td>
<td width="50%">

**前端命令**
```bash
# 安装依赖
npm install

# 开发模式
npm run dev

# 生产构建
npm run build
```

</td>
</tr>
</table>

## 🛠️ 开发建议

- ✅ 确保 CORS 配置允许前端访问
- ✅ Windows 环境建议使用管理员权限首次运行
- ✅ 建议使用 IDEA 进行开发
- ✅ 开发时打开浏览器控制台便于调试

## 📚 技术文档

- 📖 [LangChain4j 官方文档](https://github.com/langchain4j/langchain4j)
- 📖 [Spring Boot 文档](https://spring.io/projects/spring-boot)
- 📖 [Vue.js 3 文档](https://vuejs.org/)
- 📖 [通义千问 API](https://dashscope.aliyun.com/)


## 🙏 致谢

- [LangChain4j](https://github.com/langchain4j/langchain4j)
- [阿里云通义千问](https://dashscope.aliyun.com/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)


---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给一个 Star ⭐**

Made with ❤️ by [hejunhao]

</div>
