<template>
  <div class="chat-room">
    <div class="chat-header">
      <h1>AI 编程小助手</h1>
      <div class="session-info">
        <span>会话ID: {{ memoryId }}</span>
        <div class="status-indicator" :class="{ 'test-mode': testMode }">
          <span v-if="!testMode">🟢 在线</span>
          <span v-else>🟡 测试模式</span>
          <button v-if="testMode" @click="resetTestMode" class="reset-btn">重置</button>
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
          <div v-else class="user-avatar">我</div>
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
      eventSource: null,
      testMode: false, // 测试模式，当后端不可用时使用
      reconnectAttempts: 0,
      maxReconnectAttempts: 1  // 减少重连次数，避免过多重试
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
      this.isTyping = true
      this.scrollToBottom()
      
      try {
        await this.sendMessageToAI(message)
        // 连接成功，确保退出测试模式
        if (this.testMode) {
          this.testMode = false
          console.log('后端连接成功，退出测试模式')
        }
      } catch (error) {
        console.error('发送消息失败:', error)
        // 如果连接失败，启用测试模式
        if (!this.testMode) {
          this.testMode = true
          this.addErrorMessage('后端服务连接失败，已切换到测试模式。')
          // 模拟AI回复
          setTimeout(() => {
            this.simulateAIResponse(message)
          }, 1000)
        } else {
          this.addErrorMessage('抱歉，发送消息时出现错误，请稍后重试。')
        }
      } finally {
        this.isLoading = false
        this.isTyping = false
      }
    },
    
    sendMessageToAI(message) {
      return new Promise((resolve, reject) => {
        // 使用配置文件中的API地址
        const url = getApiUrl('/api/ai/chat', {
          memoryId: this.memoryId,
          message: message
        })
        console.log('发送请求到:', url)
        
        // 关闭之前的连接
        if (this.eventSource) {
          this.eventSource.close()
        }
        
        let aiMessage = {
          id: Date.now() + 1,
          type: 'ai',
          content: '',
          timestamp: new Date()
        }
        
        this.messages.push(aiMessage)
        
        // 创建SSE连接
        this.createSSEConnection(url, aiMessage, resolve, reject, message)
      })
    },

    createSSEConnection(url, aiMessage, resolve, reject, originalMessage) {
      this.eventSource = new EventSource(url)

      this.eventSource.onopen = () => {
        console.log('SSE连接已建立')
        console.log('连接状态:', this.eventSource.readyState)
        // 连接成功，重置重连计数
        this.reconnectAttempts = 0
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
          this.isLoading = false
          this.isTyping = false
          resolve()
          return
        }

        // 处理正常消息
        if (event.data) {
          aiMessage.content += event.data
          this.scrollToBottom()
        }
      }

      this.eventSource.onerror = (error) => {
        console.error('SSE连接错误:', error)
        console.error('连接状态:', this.eventSource.readyState)

        // 检查是否已经收到了完整消息
        if (aiMessage.content && aiMessage.content.length > 0) {
          // 如果已经有内容，认为消息接收完成
          this.finishAIResponse(aiMessage)
          this.isLoading = false
          this.isTyping = false
          resolve()
        } else {
          // 真正的连接错误
          // 只有在连接真正失败且没有收到任何数据时才重连
          if (this.eventSource.readyState === EventSource.CLOSED &&
              this.reconnectAttempts < this.maxReconnectAttempts &&
              !aiMessage.content) {
            this.reconnectAttempts++
            console.log(`连接中断，尝试重连 (${this.reconnectAttempts}/${this.maxReconnectAttempts})`)
            aiMessage.content = `连接中断，正在重试... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`

            setTimeout(() => {
              if (this.eventSource) {
                this.eventSource.close()
              }
              // 重新创建连接
              this.createSSEConnection(url, aiMessage, resolve, reject, originalMessage)
            }, 1000)
            return
          }

          // 重连失败或连接状态异常，显示错误信息
          if (this.eventSource.readyState === EventSource.CONNECTING) {
            aiMessage.content = '正在连接服务器...'
          } else if (this.eventSource.readyState === EventSource.CLOSED) {
            aiMessage.content = '连接已关闭，请检查后端服务是否启动。'
          } else {
            aiMessage.content = '连接出现错误，请稍后重试。'
          }

          if (this.eventSource) {
            this.eventSource.close()
          }
          this.isLoading = false
          this.isTyping = false
          this.reconnectAttempts = 0
        }

        reject(error)
      }

      // 设置超时
      setTimeout(() => {
        if (this.eventSource && this.eventSource.readyState !== EventSource.CLOSED) {
          // 如果已经有内容，认为消息接收完成
          if (aiMessage.content && aiMessage.content.length > 0) {
            this.finishAIResponse(aiMessage)
            this.isLoading = false
            this.isTyping = false
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
      this.isTyping = false

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
    
    simulateAIResponse(userMessage) {
      const responses = [
        '这是一个很好的问题！在编程学习中，理解基础概念非常重要。',
        '对于这个问题，我建议你从以下几个方面来思考...',
        '在面试中，这类问题经常被问到。让我为你详细解释一下。',
        '这是一个常见的编程问题，有多种解决方案。',
        '从你的问题来看，你正在学习这个领域，这很棒！',
        '让我为你提供一个详细的解答和示例代码。',
        '这个问题涉及到几个重要的编程概念，我来逐一解释。',
        '在解决这类问题时，我们需要考虑时间复杂度和空间复杂度。'
      ]
      
      const randomResponse = responses[Math.floor(Math.random() * responses.length)]
      
      this.messages.push({
        id: Date.now(),
        type: 'ai',
        content: randomResponse,
        timestamp: new Date()
      })
      
      this.scrollToBottom()
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
    },
    
    resetTestMode() {
      this.testMode = false
      this.reconnectAttempts = 0
      console.log('已重置测试模式，下次发送消息时将尝试连接真实后端')
      this.addErrorMessage('已重置测试模式，请重新发送消息测试后端连接。')
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
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.chat-header h1 {
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
}

.status-indicator {
  font-size: 0.8rem;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
}

.status-indicator.test-mode {
  background: rgba(255, 193, 7, 0.3);
}

.reset-btn {
  background: #dc3545;
  color: white;
  border: none;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.7rem;
  cursor: pointer;
  margin-left: 0.5rem;
}

.reset-btn:hover {
  background: #c82333;
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

.ai-avatar, .user-avatar {
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

.ai-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.user-avatar {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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
