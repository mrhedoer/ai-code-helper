<template>
  <div class="chat-container">
    <header class="chat-header">
      <div class="header-left">
        <div class="brand-logo">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
          </svg>
        </div>
        <div class="header-title">
          <h1>AI 编程小助手</h1>
          <span class="status-badge">在线</span>
        </div>
      </div>

      <div class="header-right">
        <div class="session-tag">
          <span class="tag-label">会话 ID</span>
          <span class="tag-value">{{ memoryId }}</span>
        </div>
        <button @click="handleLogout" class="login-btn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
            <polyline points="16 17 21 12 16 7"></polyline>
            <line x1="21" y1="12" x2="9" y2="12"></line>
          </svg>
          <span>退出登录</span>
        </button>
      </div>
    </header>

    <!-- 消息列表区域 -->
    <div class="messages-area" ref="messagesContainer">
      <div class="messages-wrapper">
        <div
            v-for="message in messages"
            :key="message.id"
            :class="['message-row', message.type]"
        >
          <!-- 头像 -->
          <div class="avatar">
            <div v-if="message.type === 'ai'" class="avatar-icon ai">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 2a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2 2 2 0 0 1-2-2V4a2 2 0 0 1 2-2z"></path>
                <path d="M12 16a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2z"></path>
                <path d="M2 12a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2 2 2 0 0 1-2 2H4a2 2 0 0 1-2-2z"></path>
                <path d="M16 12a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2z"></path>
                <rect x="7" y="7" width="10" height="10" rx="2"></rect>
              </svg>
            </div>
            <div v-else class="avatar-icon user">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
            </div>
          </div>

          <!-- 消息内容 -->
          <div class="message-bubble">
            <!-- 注意：这里添加了 markdown-body 类，方便后续扩展样式 -->
            <div class="bubble-content markdown-body" v-html="formatMessage(message.content)"></div>
            <div class="bubble-meta">{{ formatTime(message.timestamp) }}</div>
          </div>
        </div>

        <!-- 输入中状态 -->
        <div v-if="isTyping" class="message-row ai">
          <div class="avatar">
            <div class="avatar-icon ai">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="7" y="7" width="10" height="10" rx="2"></rect>
              </svg>
            </div>
          </div>
          <div class="message-bubble typing">
            <div class="typing-dots">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部输入区域 -->
    <div class="input-area">
      <div class="input-wrapper">
        <MessageInput
            @send-message="handleSendMessage"
            :disabled="isLoading"
        />
      </div>
      <div class="input-footer">
        AI生成内容仅供参考，请仔细甄别
      </div>
    </div>
  </div>
</template>

<script>
import MessageInput from './MessageInput.vue'
import { generateMemoryId } from '../utils/memoryId'
import { getApiUrl } from '../config/api'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-dark.css'

export default {
  name: 'ChatRoom',
  components: {
    MessageInput
  },
  data() {
    return {
      memoryId: null,
      messages: [],
      isLoading: false,
      isTyping: false,
      eventSource: null,
      md: null
    }
  },
  created() {

    const escapeHtml = (unsafe) => {
      return unsafe
          .replace(/&/g, "&amp;")
          .replace(/</g, "&lt;")
          .replace(/>/g, "&gt;")
          .replace(/"/g, "&quot;")
          .replace(/'/g, "&#039;");
    }

    this.md = new MarkdownIt({
      html: false,
      linkify: true,
      typographer: true,
      highlight: (str, lang) => {
        if (lang && hljs.getLanguage(lang)) {
          try {
            return '<pre class="hljs"><code>' +
                hljs.highlight(str, {language: lang, ignoreIllegals: true}).value +
                '</code></pre>';
          } catch (__) {
          }
        }
        // 使用自定义的 escapeHtml
        return '<pre class="hljs"><code>' + escapeHtml(str) + '</code></pre>';
      }
    })
  },
  mounted() {
    this.memoryId = generateMemoryId()
    this.addWelcomeMessage()
  },
  beforeUnmount() {
    if (this.eventSource) {
      this.eventSource.close()
    }
  },
  methods: {
    handleLogout() {
      sessionStorage.removeItem('userToken')
      sessionStorage.removeItem('username')
      this.$router.push('/login')
    },
    addWelcomeMessage() {
      this.messages.push({
        id: Date.now(),
        type: 'ai',
        content: '你好！我是AI编程小助手，可以帮助你解答编程学习和求职面试相关的问题。有什么问题尽管问我吧！',
        timestamp: new Date()
      })
    },

    async handleSendMessage(message) {
      if (!message.trim()) return

      this.messages.push({
        id: Date.now(),
        type: 'user',
        content: message,
        timestamp: new Date()
      })

      this.isLoading = true
      this.scrollToBottom()

      try {
        await this.sendMessageToAI(message)
      } catch (error) {
        console.error('发送消息失败:', error)
        this.addErrorMessage('抱歉，发送消息时出现错误，请稍后重试。')
      } finally {
        this.isLoading = false
      }
    },

    sendMessageToAI(message) {
      return new Promise((resolve, reject) => {
        const url = getApiUrl('/api/ai/chat', {
          memoryId: this.memoryId,
          message: message
        })

        if (this.eventSource) {
          this.eventSource.close()
        }

        let aiMessage = {
          id: Date.now() + 1,
          type: 'ai',
          content: '正在思考...',
          timestamp: new Date()
        }

        this.messages.push(aiMessage)
        this.createSSEConnection(url, aiMessage, resolve, reject)
      })
    },

    createSSEConnection(url, aiMessage, resolve, reject) {
      this.eventSource = new EventSource(url)

      // 标记是否是第一次收到有效内容
      let isFirstChunk = true;

      this.eventSource.onmessage = (event) => {
        if (event.data === '[DONE]') {
          this.finishAIResponse(aiMessage)
          resolve()
          return
        }

        if (event.data) {
          // 如果是第一次收到内容，且当前内容是占位符，则清空
          if (isFirstChunk && aiMessage.content === '正在思考...') {
            aiMessage.content = '';
            isFirstChunk = false;
          }

          aiMessage.content += event.data;


          this.$forceUpdate()
          this.scrollToBottom()
        }
      }

      this.eventSource.onerror = (error) => {
        // 只有在完全没有收到内容时才报错，否则视为连接中断但已接收部分数据
        if (aiMessage.content === '正在思考...') {
          aiMessage.content = '连接出现错误，请稍后重试。'
          reject(error)
        } else {
          // 已经有内容了，就当作结束处理
          this.finishAIResponse(aiMessage)
          resolve()
        }
        if (this.eventSource) this.eventSource.close()
      }
    },

    finishAIResponse(aiMessage) {
      if (this.eventSource) this.eventSource.close()
      this.isLoading = false
      if (aiMessage.content) {
        aiMessage.content = aiMessage.content.trim()
      }
    },

    addErrorMessage(message) {
      this.messages.push({
        id: Date.now(),
        type: 'ai',
        content: message,
        timestamp: new Date()
      })
    },

    formatMessage(content) {
      if (!content) return ''
      // 增加安全防护，防止 XSS
      return this.md.render(content)
    },

    formatTime(timestamp) {
      return timestamp.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit'
      })
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    }
  }
}
</script>

<style scoped>


:root {
  --primary-color: #6366f1;
  --primary-light: #e0e7ff;
  --text-main: #1f2937;
  --text-sub: #6b7280;
  --bg-main: #f3f4f6;
  --bg-white: #ffffff;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f3f4f6;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.chat-header {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  padding: 0.75rem 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.brand-logo {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.brand-logo svg {
  width: 20px;
  height: 20px;
}

.header-title h1 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 700;
  color: #1f2937;
}

.status-badge {
  font-size: 0.75rem;
  color: #10b981;
  background: #ecfdf5;
  padding: 0.1rem 0.4rem;
  border-radius: 99px;
  margin-left: 0.5rem;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.session-tag {
  display: flex;
  align-items: center;
  background: #f3f4f6;
  padding: 0.35rem 0.75rem;
  border-radius: 6px;
  font-size: 0.8rem;
  color: #4b5563;
}

.tag-label {
  color: #9ca3af;
  margin-right: 0.5rem;
}

.tag-value {
  font-family: monospace;
  font-weight: 600;
}

.login-btn {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.5rem 1rem;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  color: #4b5563;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.login-btn:hover {
  border-color: #6366f1;
  color: #6366f1;
  background: #f5f7ff;
}

.login-btn svg {
  width: 16px;
  height: 16px;
}

/* 消息区域 */
.messages-area {
  flex: 1;
  overflow-y: auto;
  padding: 2rem 1rem;
  scroll-behavior: smooth;
}

.messages-wrapper {
  max-width: 800px;
  margin: 0 auto;
}

.message-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  align-items: flex-start;
  animation: slideIn 0.3s ease-out;
}

.message-row.user {
  flex-direction: row-reverse;
}

.avatar-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar-icon svg {
  width: 20px;
  height: 20px;
}

.avatar-icon.ai {
  background: white;
  color: #6366f1;
  border: 1px solid #e5e7eb;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.avatar-icon.user {
  background: #6366f1;
  color: white;
}

/* 气泡样式 */
.message-bubble {
  max-width: 80%;
  padding: 1rem;
  border-radius: 12px;
  position: relative;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  line-height: 1.6;
  overflow: hidden; /* 防止代码块溢出圆角 */
}

.message-row.ai .message-bubble {
  background: white;
  border-top-left-radius: 2px;
  color: #374151;
}

.message-row.user .message-bubble {
  background: #6366f1;
  color: white;
  border-top-right-radius: 2px;
}

.bubble-content {
  font-size: 0.95rem;
  word-wrap: break-word;
  line-height: 1.6;
}



.bubble-content :deep(p) {
  margin: 0.5em 0;
}

.bubble-content :deep(p:first-child) {
  margin-top: 0;
}

.bubble-content :deep(p:last-child) {
  margin-bottom: 0;
}

.bubble-content :deep(pre) {
  border-radius: 6px;
  padding: 0;
  margin: 0.8em 0;
  overflow-x: auto;
}

/* 确保 hljs 容器充满 pre */
.bubble-content :deep(pre code.hljs) {
  display: block;
  padding: 1em;
  border-radius: 6px;
  font-family: 'Fira Code', Consolas, Monaco, monospace;
  font-size: 0.9em;
  line-height: 1.5;
}

/* 行内代码样式 (非代码块) */
.bubble-content :deep(:not(pre) > code) {
  font-family: 'Fira Code', Consolas, monospace;
  font-size: 0.9em;
  background: rgba(0, 0, 0, 0.06);
  color: #c7254e; /* 类似 GitHub 的行内代码红 */
  padding: 0.2em 0.4em;
  border-radius: 3px;
}

/* 列表和链接 */
.bubble-content :deep(ul), .bubble-content :deep(ol) {
  padding-left: 1.5em;
  margin: 0.5em 0;
}

.bubble-content :deep(li) {
  margin: 0.25em 0;
}

.bubble-content :deep(a) {
  color: #6366f1;
  text-decoration: none;
}

.bubble-content :deep(a:hover) {
  text-decoration: underline;
}

.bubble-content :deep(blockquote) {
  border-left: 4px solid #e5e7eb;
  margin: 0.5em 0;
  padding-left: 1em;
  color: #6b7280;
}

/*
   用户消息中的 Markdown 样式调整
   (用户发送的消息通常不包含复杂代码块，保持简单即可)
*/
.message-row.user .bubble-content :deep(code) {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.message-row.user .bubble-content :deep(pre) {
  background: rgba(0, 0, 0, 0.3); /* 用户侧代码块保持半透明深色 */
}

.message-row.user .bubble-content :deep(pre code.hljs) {
  background: transparent; /* 覆盖主题背景，使用上面的半透明 */
  color: #e0e7ff;
}

.message-row.user .bubble-content :deep(a) {
  color: #e0e7ff;
  text-decoration: underline;
}

.message-row.user .bubble-content :deep(blockquote) {
  border-left-color: rgba(255, 255, 255, 0.3);
  color: rgba(255, 255, 255, 0.8);
}

.bubble-meta {
  font-size: 0.7rem;
  margin-top: 0.4rem;
  opacity: 0.6;
  text-align: right;
}

.message-row.user .bubble-meta {
  color: rgba(255, 255, 255, 0.9);
}

/* 输入中动画 (保持不变) */
.typing-dots {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0.25rem 0;
}

.typing-dots span {
  width: 6px;
  height: 6px;
  background: #9ca3af;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.typing-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 底部输入区 (保持不变) */
.input-area {
  background: white;
  padding: 1rem 1.5rem 1.5rem;
  border-top: 1px solid #e5e7eb;
}

.input-wrapper {
  max-width: 800px;
  margin: 0 auto;
}

.input-footer {
  text-align: center;
  font-size: 0.75rem;
  color: #9ca3af;
  margin-top: 0.75rem;
}

/* 响应式 */
@media (max-width: 640px) {
  .chat-header {
    padding: 0.75rem 1rem;
  }

  .header-title h1 {
    font-size: 1rem;
  }

  .status-badge, .session-tag {
    display: none;
  }

  .login-btn span {
    display: none;
  }

  .login-btn {
    padding: 0.5rem;
  }

  .message-bubble {
    max-width: 90%;
  }

  .messages-area {
    padding: 1rem 0.5rem;
  }
}
</style>