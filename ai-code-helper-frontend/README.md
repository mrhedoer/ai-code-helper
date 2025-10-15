# AI 编程小助手前端

这是一个基于Vue3的AI编程小助手前端应用，提供聊天室风格的界面来与AI进行编程学习和求职面试相关的对话。

## 功能特性

- 🎨 现代化的聊天室界面设计
- 💬 实时SSE流式对话
- 📱 响应式设计，支持移动端
- 🔄 自动生成会话ID
- ⚡ 基于Vue3 + Vite的快速开发体验

## 技术栈

- **Vue 3** - 渐进式JavaScript框架
- **Vite** - 快速的前端构建工具
- **Axios** - HTTP客户端库
- **Server-Sent Events (SSE)** - 实时数据流

## 项目结构

```
src/
├── components/
│   ├── ChatRoom.vue      # 聊天室主组件
│   └── MessageInput.vue  # 消息输入组件
├── utils/
│   └── memoryId.js       # 会话ID生成工具
├── App.vue               # 根组件
├── main.js               # 应用入口
└── style.css             # 全局样式
```

## 安装和运行

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

应用将在 `http://localhost:3000` 启动

### 3. 构建生产版本

```bash
npm run build
```

### 4. 预览生产版本

```bash
npm run preview
```

## 后端接口

应用需要连接到运行在 `http://localhost:8081` 的SpringBoot后端服务。

### 接口信息

- **基础URL**: `http://localhost:8081/api`
- **聊天接口**: `GET /ai/chat`
- **参数**: 
  - `memoryId`: 会话ID (数字)
  - `message`: 用户消息 (字符串)
- **响应**: Server-Sent Events流

## 使用说明

1. 确保后端服务已启动并运行在 `http://localhost:8081`
2. 打开应用后会自动生成一个唯一的会话ID
3. 在输入框中输入你的编程问题或面试相关问题
4. AI会通过SSE流式返回回答
5. 支持多轮对话，会话ID保持不变

## 开发说明

### 主要组件

- **ChatRoom**: 聊天室主组件，管理消息列表和SSE连接
- **MessageInput**: 消息输入组件，支持多行输入和快捷键发送

### 关键功能

- 自动生成会话ID用于区分不同对话
- SSE连接管理，支持错误处理和自动重连
- 消息格式化显示，支持换行
- 响应式设计，适配不同屏幕尺寸

## 后端要求

- SpringBoot应用运行在8081端口
- 支持SSE (Server-Sent Events)
- 正确的CORS配置
- API接口路径：`/api/ai/chat`

## 浏览器支持

- Chrome (推荐)
- Firefox
- Safari
- Edge

## 注意事项

- 确保后端服务已启动并运行在8081端口
- 需要现代浏览器支持SSE和ES6+语法
- 建议使用Chrome浏览器以获得最佳体验