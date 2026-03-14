<template>
  <div class="message-input-wrapper">
    <div class="input-group" :class="{ 'is-focused': isFocused, 'is-disabled': disabled }">
      
      <!-- 语音输入按钮 -->
      <button
        @click="toggleRecording"
        class="mic-btn"
        :class="{ 'is-recording': isRecording }"
        type="button"
        title="语音输入"
        :disabled="disabled || isTranscribing"
      >
        <div v-if="isTranscribing" class="loading-spinner mic-loading"></div>
        <svg v-else viewBox="0 0 24 24" fill="none" class="mic-icon" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M12 2a3 3 0 0 0-3 3v7a3 3 0 0 0 6 0V5a3 3 0 0 0-3-3Z"></path>
          <path d="M19 10v2a7 7 0 0 1-14 0v-2"></path>
          <line x1="12" y1="19" x2="12" y2="22"></line>
        </svg>
      </button>

      <textarea
          ref="textarea"
          v-model="message"
          :disabled="disabled"
          placeholder="输入消息..."
          @keydown="handleKeyDown"
          @input="adjustHeight"
          @focus="isFocused = true"
          @blur="isFocused = false"
          class="chat-textarea"
          rows="1"
      ></textarea>

      <div class="action-area">
        <button
            @click="sendMessage"
            :disabled="!isValidMessage || disabled"
            class="send-btn"
            :class="{ 'has-content': isValidMessage }"
            type="button"
            aria-label="发送消息"
        >
          <div v-if="disabled && message.length > 0" class="loading-spinner"></div>
          <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="22" y1="2" x2="11" y2="13"></line>
            <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
          </svg>
        </button>
      </div>
    </div>

    <!-- 可选：字数统计或提示 -->
    <!-- <div class="input-footer" v-if="message.length > 500">
      {{ message.length }}/1000
    </div> -->
  </div>
</template>

<script>
export default {
  name: 'MessageInput',
  props: {
    disabled: {
      type: Boolean,
      default: false
    },
    hasFiles: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      message: '',
      isFocused: false,
      isRecording: false,
      isTranscribing: false,
      mediaRecorder: null,
      audioChunks: [],
      audioContext: null,
      analyser: null,
      silenceTimer: null
    }
  },
  computed: {
    isValidMessage() {
      return this.message.trim().length > 0 || this.hasFiles
    }
  },
  methods: {
    handleKeyDown(event) {
      // Enter 发送，Shift+Enter 换行
      if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault()
        this.sendMessage()
      }
    },

    sendMessage() {
      if (this.isValidMessage && !this.disabled) {
        this.$emit('send-message', this.message.trim())
        this.message = ''
        // 发送后重置高度
        this.$nextTick(() => {
          this.resetHeight()
        })
      }
    },

    adjustHeight() {
      const textarea = this.$refs.textarea
      if (textarea) {
        // 先重置高度以获得正确的 scrollHeight (处理删除文字的情况)
        textarea.style.height = 'auto'

        const scrollHeight = textarea.scrollHeight
        const maxHeight = 150 // 最大高度限制

        textarea.style.height = Math.min(scrollHeight, maxHeight) + 'px'

        // 如果超过最大高度，允许滚动
        textarea.style.overflowY = scrollHeight > maxHeight ? 'auto' : 'hidden'
      }
    },

    resetHeight() {
      const textarea = this.$refs.textarea
      if (textarea) {
        textarea.style.height = 'auto'
        textarea.style.overflowY = 'hidden'
      }
    },

    focus() {
      this.$refs.textarea?.focus()
    },

    async toggleRecording() {
      if (this.isRecording) {
        this.stopRecording()
      } else {
        await this.startRecording()
      }
    },

    async startRecording() {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
        this.isRecording = true
        this.audioChunks = []
        
        this.mediaRecorder = new MediaRecorder(stream, { mimeType: 'audio/webm' })
        
        this.mediaRecorder.ondataavailable = (event) => {
          if (event.data.size > 0) {
            this.audioChunks.push(event.data)
          }
        }
        
        this.mediaRecorder.onstop = async () => {
          this.stopAudioStreams(stream)
          await this.transcribeAudio()
        }

        this.setupVAD(stream)
        this.mediaRecorder.start(100) // 收集音频块（100ms）

      } catch (err) {
        console.error("无法启动录音:", err)
        alert("无法访问麦克风，请检查权限。")
      }
    },

    stopRecording() {
      if (this.mediaRecorder && this.mediaRecorder.state !== 'inactive') {
        this.mediaRecorder.stop()
      }
      this.isRecording = false
      if (this.silenceTimer) {
        clearTimeout(this.silenceTimer)
        this.silenceTimer = null
      }
    },

    stopAudioStreams(stream) {
      stream.getTracks().forEach(track => track.stop())
      if (this.audioContext && this.audioContext.state !== 'closed') {
        this.audioContext.close()
      }
      if (this.silenceTimer) {
        clearTimeout(this.silenceTimer)
        this.silenceTimer = null
      }
    },

    setupVAD(stream) {
      this.audioContext = new (window.AudioContext || window.webkitAudioContext)()
      this.analyser = this.audioContext.createAnalyser()
      const microphoneNode = this.audioContext.createMediaStreamSource(stream)
      
      this.analyser.fftSize = 256
      microphoneNode.connect(this.analyser)
      
      const bufferLength = this.analyser.frequencyBinCount
      const dataArray = new Uint8Array(bufferLength)
      
      const checkSilence = () => {
        if (!this.isRecording) return
        
        this.analyser.getByteFrequencyData(dataArray)
        const average = dataArray.reduce((acc, val) => acc + val, 0) / bufferLength
        
        // 当音量低于阈值 10 时视为静音
        if (average < 10) { 
          if (!this.silenceTimer) {
            this.silenceTimer = setTimeout(() => {
              console.log("检测到2秒静音，自动停止录音...")
              this.stopRecording()
            }, 2000)
          }
        } else {
          // 有声音，重置静音定时器
          if (this.silenceTimer) {
            clearTimeout(this.silenceTimer)
            this.silenceTimer = null
          }
        }
        
        if (this.isRecording) {
          requestAnimationFrame(checkSilence)
        }
      }
      
      checkSilence()
    },

    async transcribeAudio() {
      if (this.audioChunks.length === 0) return
      
      this.isTranscribing = true
      
      const audioBlob = new Blob(this.audioChunks, { type: 'audio/webm' })
      const formData = new FormData()
      formData.append('audio', audioBlob, 'recording.webm')
      
      try {
        // 请求后端的 STT 接口
        const response = await fetch('http://localhost:8081/api/stt', {
          method: 'POST',
          body: formData
        })
        
        if (response.ok) {
          const result = await response.json()
          if (result.transcript) {
            this.message = this.message ? this.message + ' ' + result.transcript : result.transcript
            this.$nextTick(() => {
              this.adjustHeight()
              this.focus()
            })
          }
        } else {
          console.error("STT 识别失败:", await response.text())
        }
      } catch (error) {
        console.error("STT 请求异常:", error)
      } finally {
        this.isTranscribing = false
      }
    }
  },

  mounted() {
    this.focus()
    this.resetHeight()
  }
}
</script>

<style scoped>
.message-input-wrapper {
  width: 100%;
  padding: 0 0.5rem;
}

.input-group {
  display: flex;
  align-items: flex-end;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 1.5rem; /* 24px */
  padding: 0.5rem;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
  position: relative;
}

.input-group.is-focused {
  border-color: #6366f1; /* Indigo-500 */
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15);
}

.input-group.is-disabled {
  background: #f9fafb;
  border-color: #f3f4f6;
}

.chat-textarea {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  padding: 0.75rem 0 0.75rem 0.5rem;
  font-size: 0.95rem;
  line-height: 1.5;
  resize: none;
  min-height: 24px; /* 单行高度 */

  max-height: 150px;
  font-family: inherit;
  color: #1f2937;
  overflow-y: hidden; /* 默认隐藏滚动条 */
}

.chat-textarea::placeholder {
  color: #9ca3af;
}

.chat-textarea:disabled {
  cursor: not-allowed;
  color: #9ca3af;
}

.action-area {
  padding: 0.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-btn {
  background: #f3f4f6; /* 默认灰色背景 */
  border: none;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af; /* 默认灰色图标 */
  cursor: not-allowed;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
}

/* 当有内容且未禁用时的状态 */
.send-btn.has-content:not(:disabled) {
  background: #4f46e5; /* Indigo-600 */
  color: white;
  cursor: pointer;
  transform: scale(1);
  box-shadow: 0 2px 5px rgba(79, 70, 229, 0.3);
}

.send-btn.has-content:not(:disabled):hover {
  background: #4338ca; /* Indigo-700 */
  transform: scale(1.05);
}

.send-btn.has-content:not(:disabled):active {
  transform: scale(0.95);
}

.send-btn svg {
  width: 18px;
  height: 18px;
  /* 稍微调整图标位置以视觉居中 */
  margin-left: -1px;
  margin-top: 1px;
}

/* Loading 动画 */
.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid #ffffff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 语音按钮样式 */
.mic-btn {
  background: transparent;
  border: none;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
  margin-bottom: 0.25rem;
  margin-left: 0.25rem;
}

.mic-btn:hover:not(:disabled) {
  background: #f3f4f6;
  color: #4b5563;
}

.mic-btn.is-recording {
  color: #ef4444; /* 红色图标表示录音中 */
  animation: pulse 1.5s infinite;
}

.mic-icon {
  width: 20px;
  height: 20px;
}

.mic-loading {
  border-top-color: #6366f1; /* 正在识别时的加载动画颜色 */
  width: 18px;
  height: 18px;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); color: #dc2626; }
  100% { transform: scale(1); }
}

/* 响应式微调 */
@media (max-width: 640px) {
  .input-group {
    padding: 0.35rem;
    border-radius: 1.25rem;
  }

  .chat-textarea {
    padding: 0.6rem 0 0.6rem 0.8rem;
    font-size: 16px; /* 防止 iOS 缩放 */
  }

  .send-btn {
    width: 32px;
    height: 32px;
  }

  .send-btn svg {
    width: 16px;
    height: 16px;
  }
}
</style>