<template>
  <div class="message-input">
    <div class="input-container">
      <textarea
        ref="textarea"
        v-model="message"
        :disabled="disabled"
        placeholder="输入你的问题..."
        @keydown="handleKeyDown"
        @input="adjustHeight"
        class="input-field"
        rows="1"
      ></textarea>
      <button
        @click="sendMessage"
        :disabled="!message.trim() || disabled"
        class="send-button"
        type="button"
      >
        <svg v-if="!disabled" width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z" fill="currentColor"/>
        </svg>
        <div v-else class="loading-spinner"></div>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MessageInput',
  props: {
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      message: ''
    }
  },
  methods: {
    handleKeyDown(event) {
      if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault()
        this.sendMessage()
      }
    },
    
    sendMessage() {
      if (this.message.trim() && !this.disabled) {
        this.$emit('send-message', this.message.trim())
        this.message = ''
        this.adjustHeight()
      }
    },
    
    adjustHeight() {
      this.$nextTick(() => {
        const textarea = this.$refs.textarea
        if (textarea) {
          textarea.style.height = 'auto'
          const scrollHeight = textarea.scrollHeight
          const maxHeight = 120 // 最大高度限制
          textarea.style.height = Math.min(scrollHeight, maxHeight) + 'px'
        }
      })
    },
    
    focus() {
      this.$refs.textarea?.focus()
    }
  },
  
  mounted() {
    this.focus()
  }
}
</script>

<style scoped>
.message-input {
  width: 100%;
}

.input-container {
  display: flex;
  align-items: flex-end;
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  border-radius: 24px;
  padding: 0.5rem;
  transition: border-color 0.2s ease;
}

.input-container:focus-within {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-field {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  line-height: 1.4;
  resize: none;
  min-height: 24px;
  max-height: 120px;
  font-family: inherit;
}

.input-field::placeholder {
  color: #6c757d;
}

.input-field:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.send-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-left: 0.5rem;
  flex-shrink: 0;
}

.send-button:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.send-button:active:not(:disabled) {
  transform: scale(0.95);
}

.send-button:disabled {
  background: #6c757d;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .input-container {
    padding: 0.4rem;
  }
  
  .input-field {
    padding: 0.6rem 0.8rem;
    font-size: 0.9rem;
  }
  
  .send-button {
    width: 36px;
    height: 36px;
  }
}
</style>
