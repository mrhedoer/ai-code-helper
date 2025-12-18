<template>
  <div class="chat-container">
    <!-- 顶部导航栏 -->
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
        <button @click="showLoginModal = true" class="login-btn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
          <span>登录 / 注册</span>
        </button>
      </div>
    </header>

    <!-- 登录入口对话框 -->
    <LoginModal :show="showLoginModal" @close="showLoginModal = false" />

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
            <div class="bubble-content" v-html="formatMessage(message.content)"></div>
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
import LoginModal from './LoginModal.vue'
import { generateMemoryId } from '../utils/memoryId'
import { getApiUrl } from '../config/api'

export default {
  name: 'ChatRoom',
  components: {
    MessageInput,
    LoginModal
  },
  data() {
    return {
      memoryId: null,
      messages: [],
      isLoading: false,
      isTyping: false,
      eventSource: null,
      showLoginModal: false
    }
  },
  mounted() {
    this.memoryId = generateMemoryId()
    this.addWelcomeMessage()
    this.checkLoginStatus()
  },
  beforeUnmount() {
    if (this.eventSource) {
      this.eventSource.close()
    }
  },
  methods: {
    checkLoginStatus() {
      const userToken = sessionStorage.getItem('userToken')
      const adminToken = sessionStorage.getItem('adminToken')
      if (!userToken && !adminToken) {
        this.showLoginModal = true
      }
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

      this.eventSource.onmessage = (event) => {
        if (event.data === '[DONE]') {
          this.finishAIResponse(aiMessage)
          if (this.eventSource) this.eventSource.close()
          resolve()
          return
        }

        if (event.data) {
          if (aiMessage.content === '正在思考...') {
            aiMessage.content = event.data
          } else {
            aiMessage.content += event.data
          }
          this.scrollToBottom()
          this.$forceUpdate()
        }
      }

      this.eventSource.onerror = (error) => {
        if (aiMessage.content && aiMessage.content !== '正在思考...' && aiMessage.content.length > 0) {
          this.finishAIResponse(aiMessage)
          resolve()
        } else {
          aiMessage.content = '连接出现错误，请稍后重试。'
          if (this.eventSource) this.eventSource.close()
        }
        reject(error)
      }

      setTimeout(() => {
        if (this.eventSource && this.eventSource.readyState !== EventSource.CLOSED) {
          if (aiMessage.content && aiMessage.content !== '正在思考...' && aiMessage.content.length > 0) {
            this.finishAIResponse(aiMessage)
            resolve()
          } else {
            aiMessage.content = '请求超时，请稍后重试。'
          }
          if (this.eventSource) this.eventSource.close()
        }
      }, 30000)
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
      // 不再使用 replace(/\n/g, '<br>')，而是依赖 CSS 的 white-space: pre-wrap
      // 这样可以保留代码缩进等格式
      return content
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
/* 全局变量 */
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

/* 顶部导航栏 */
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

/* 头像样式 */
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
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
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
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
  line-height: 1.6;
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
  white-space: pre-wrap; /* 关键：保留换行和空格 */
  word-wrap: break-word;
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

/* 输入中动画 */
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

.typing-dots span:nth-child(1) { animation-delay: -0.32s; }
.typing-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

@keyframes slideIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 底部输入区 */
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