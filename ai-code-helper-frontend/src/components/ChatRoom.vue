<template>
  <div class="chat-room">
    <div class="chat-header">
      <h1>AI 编程小助手</h1>
      <div class="session-info">
        <span>会话ID: {{ memoryId }}</span>
        <div class="status-indicator">
          <span>🟢 在线</span>
        </div>
      </div>
    </div>

    <div class="chat-messages" ref="messagesContainer">
      <div
          v-for="message in messages"
          :key="message.id"
          :class="['message', message.type]"
      >
        <div class="message-avatar">
          <div v-if="message.type === 'ai'" class="ai-avatar">AI</div>
          <div v-else class="user-avatar">小白</div>
        </div>
        <div class="message-content">
          <div class="message-text" v-html="formatMessage(message.content)"></div>
          <div class="message-time">{{ formatTime(message.timestamp) }}</div>
        </div>
      </div>

      <div v-if="isTyping" class="message ai">
        <div class="message-avatar">
          <div class="ai-avatar">AI</div>
        </div>
        <div class="message-content">
          <div class="typing-indicator">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>
    </div>

    <div class="chat-input-container">
      <MessageInput
          @send-message="handleSendMessage"
          :disabled="isLoading"
      />
    </div>
  </div>
</template>

<script>
import MessageInput from './MessageInput.vue'
import { generateMemoryId } from '../utils/memoryId'
import { getApiUrl } from '../config/api'

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
      eventSource: null
    }
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

      // 添加用户消息
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
        console.log('发送请求到:', url)

        // 关闭之前的连接
        if (this.eventSource) {
          this.eventSource.close()
        }

        // 创建AI消息对象，初始内容为"正在思考..."
        let aiMessage = {
          id: Date.now() + 1,
          type: 'ai',
          content: '正在思考...',
          timestamp: new Date()
        }

        // 将AI消息添加到消息列表中
        this.messages.push(aiMessage)

        // 创建SSE连接
        this.createSSEConnection(url, aiMessage, resolve, reject)
      })
    },

    createSSEConnection(url, aiMessage, resolve, reject) {
      this.eventSource = new EventSource(url)

      this.eventSource.onopen = () => {
        console.log('SSE连接已建立')
      }

      this.eventSource.onmessage = (event) => {
        console.log('收到消息:', event.data)

        // 检查是否是结束标记
        if (event.data === '[DONE]') {
          console.log('SSE流结束')
          this.finishAIResponse(aiMessage)
          if (this.eventSource) {
            this.eventSource.close()
          }
          resolve()
          return
        }

        // 处理正常消息 - 直接添加到消息内容中
        if (event.data) {
          // 如果是第一条实际内容，替换掉"正在思考..."提示
          if (aiMessage.content === '正在思考...') {
            aiMessage.content = event.data
          } else {
            aiMessage.content += event.data
          }
          this.scrollToBottom()

          // 强制更新DOM以立即显示新内容
          this.$forceUpdate()
        }
      }

      this.eventSource.onerror = (error) => {
        console.error('SSE连接错误:', error)

        // 检查是否已经收到了完整消息
        if (aiMessage.content && aiMessage.content !== '正在思考...' && aiMessage.content.length > 0) {
          // 如果已经有内容，认为消息接收完成
          this.finishAIResponse(aiMessage)
          resolve()
        } else {
          // 真正的连接错误
          aiMessage.content = '连接出现错误，请稍后重试。'
          if (this.eventSource) {
            this.eventSource.close()
          }
        }

        reject(error)
      }

      // 设置超时
      setTimeout(() => {
        if (this.eventSource && this.eventSource.readyState !== EventSource.CLOSED) {
          // 如果已经有内容且不是初始提示，认为消息接收完成
          if (aiMessage.content && aiMessage.content !== '正在思考...' && aiMessage.content.length > 0) {
            this.finishAIResponse(aiMessage)
            resolve()
          } else {
            console.log('请求超时，关闭连接')
            aiMessage.content = '请求超时，请稍后重试。'
          }
          if (this.eventSource) {
            this.eventSource.close()
          }
        }
      }, 30000) // 30秒超时
    },

    // 新增方法：完成AI响应处理
    finishAIResponse(aiMessage) {
      if (this.eventSource) {
        this.eventSource.close()
      }
      this.isLoading = false

      // 确保消息末尾没有多余的空格或特殊字符
      if (aiMessage.content) {
        aiMessage.content = aiMessage.content.trim()
      }

      console.log('AI响应完成:', aiMessage.content)
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
      // 简单的换行处理
      return content.replace(/\n/g, '<br>')
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
.chat-room {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #ffffff;
}

.chat-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 2rem;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.chat-header h1 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.session-info {
  font-size: 0.9rem;
  opacity: 0.9;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.25rem;
  position:absolute;
  right: 2rem;
}

.status-indicator {
  font-size: 0.8rem;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  background-color: #f8f9fa;
}

.message {
  display: flex;
  margin-bottom: 1rem;
  animation: fadeIn 0.3s ease-in;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  margin: 0 0.5rem;
}

.ai-avatar{
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 600;
  color: white;
}
.user-avatar{
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 600;
  color: black;
}

.ai-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.user-avatar {
  background: linear-gradient(135deg, #ffffff 0%, #ffffff 100%);
}

.message-content {
  max-width: 70%;
  min-width: 200px;
}

.message.user .message-content {
  text-align: right;
}

.message-text {
  background: white;
  padding: 0.75rem 1rem;
  border-radius: 18px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  word-wrap: break-word;
  line-height: 1.4;
}

.message.user .message-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message-time {
  font-size: 0.75rem;
  color: #666;
  margin-top: 0.25rem;
  padding: 0 0.5rem;
}

.typing-indicator {
  display: flex;
  align-items: center;
  padding: 0.75rem 1rem;
  background: white;
  border-radius: 18px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.typing-indicator span {
  height: 8px;
  width: 8px;
  background-color: #999;
  border-radius: 50%;
  display: inline-block;
  margin-right: 4px;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: -0.16s;
}

.chat-input-container {
  padding: 1rem 2rem;
  background: white;
  border-top: 1px solid #e9ecef;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-header {
    padding: 1rem;
  }

  .chat-header h1 {
    font-size: 1.2rem;
  }

  .session-info {
    font-size: 0.8rem;
    right: 1rem;
  }

  .message-content {
    max-width: 85%;
    min-width: 150px;
  }

  .chat-input-container {
    padding: 1rem;
  }
}
</style>
