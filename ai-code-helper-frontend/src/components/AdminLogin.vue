<template>
  <div class="login-wrapper">
    <!-- 背景装饰图形 -->
    <div class="bg-shape shape-1"></div>
    <div class="bg-shape shape-2"></div>

    <div class="login-card">
      <div class="card-header">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
          </svg>
        </div>
        <h2>管理员登录</h2>
        <p class="subtitle">智聊系统后台管理中心</p>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">账号</label>
          <div class="input-wrapper">
            <span class="input-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
            </span>
            <input
                id="username"
                v-model="loginForm.username"
                type="text"
                placeholder="请输入管理员账号"
                required
                :class="{ 'has-error': errorMessage }"
            />
          </div>
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <div class="input-wrapper">
            <span class="input-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
              </svg>
            </span>
            <input
                id="password"
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                required
                :class="{ 'has-error': errorMessage }"
            />
          </div>
        </div>

        <div v-if="errorMessage" class="error-alert">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="8" x2="12" y2="12"></line>
            <line x1="12" y1="16" x2="12.01" y2="16"></line>
          </svg>
          <span>{{ errorMessage }}</span>
        </div>

        <button type="submit" class="submit-btn" :disabled="isLoading">
          <span v-if="isLoading" class="spinner"></span>
          {{ isLoading ? '正在验证...' : '立即登录' }}
        </button>

        <div class="form-footer">
          <button type="button" class="back-link" @click="goBack">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M19 12H5M12 19l-7-7 7-7"/>
            </svg>
            返回首页
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { API_CONFIG } from '../config/api'

export default {
  name: 'AdminLogin',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      errorMessage: '',
      isLoading: false
    }
  },
  methods: {
    async handleLogin() {
      this.errorMessage = ''
      this.isLoading = true



      try {
        const response = await axios.post(
            `${API_CONFIG.BASE_URL}/api/admin/login`,
            this.loginForm
        )

        if (response.data.success) {
          sessionStorage.setItem('adminToken', response.data.token)
          sessionStorage.setItem('adminUsername', this.loginForm.username)
          this.$router.push('/admin/dashboard')
        } else {
          this.errorMessage = response.data.message || '账号或密码错误'
        }
      } catch (error) {
        console.error('登录错误:', error)
        this.errorMessage = error.response?.data?.message || '服务器连接失败，请稍后重试'
      } finally {
        this.isLoading = false
      }
    },
    goBack() {
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
/* 变量定义，保持与 Dashboard 一致 */
:root {
  --primary-color: #6366f1;
  --primary-hover: #4f46e5;
  --text-main: #1f2937;
  --text-light: #6b7280;
  --bg-input: #f9fafb;
}

.login-wrapper {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f3f4f6;
  position: relative;
  overflow: hidden;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

/* 背景装饰球 */
.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
  opacity: 0.6;
}

.shape-1 {
  width: 400px;
  height: 400px;
  background: #c7d2fe; /* Indigo-200 */
  top: -100px;
  left: -100px;
  animation: float 10s infinite ease-in-out;
}

.shape-2 {
  width: 300px;
  height: 300px;
  background: #ddd6fe; /* Violet-200 */
  bottom: -50px;
  right: -50px;
  animation: float 12s infinite ease-in-out reverse;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(30px, 50px); }
}

/* 登录卡片 */
.login-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  padding: 3rem;
  border-radius: 24px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  width: 100%;
  max-width: 420px;
  position: relative;
  z-index: 1;
  border: 1px solid rgba(255, 255, 255, 0.5);
  animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 卡片头部 */
.card-header {
  text-align: center;
  margin-bottom: 2.5rem;
}

.logo-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin: 0 auto 1.5rem;
  box-shadow: 0 10px 15px -3px rgba(99, 102, 241, 0.3);
}

.logo-icon svg {
  width: 28px;
  height: 28px;
}

.card-header h2 {
  color: #1f2937;
  font-size: 1.75rem;
  font-weight: 700;
  margin: 0 0 0.5rem;
}

.subtitle {
  color: #6b7280;
  font-size: 0.95rem;
  margin: 0;
}

/* 表单样式 */
.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #374151;
  font-size: 0.9rem;
  font-weight: 500;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  display: flex;
  align-items: center;
}

.input-icon svg {
  width: 20px;
  height: 20px;
}

.input-wrapper input {
  width: 100%;
  padding: 0.85rem 1rem 0.85rem 3rem; /* Left padding makes room for icon */
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  font-size: 1rem;
  color: #1f2937;
  transition: all 0.2s;
  box-sizing: border-box;
}

.input-wrapper input:focus {
  outline: none;
  background: white;
  border-color: #6366f1;
  box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
}

.input-wrapper input.has-error {
  border-color: #ef4444;
  background: #fef2f2;
}

/* 错误提示 */
.error-alert {
  background: #fef2f2;
  color: #ef4444;
  padding: 0.75rem;
  border-radius: 8px;
  font-size: 0.875rem;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  border: 1px solid #fee2e2;
}

.error-alert svg {
  width: 18px;
  height: 18px;
}

/* 按钮样式 */
.submit-btn {
  width: 100%;
  padding: 0.85rem;
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  box-shadow: 0 4px 6px -1px rgba(99, 102, 241, 0.3);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 10px 15px -3px rgba(99, 102, 241, 0.4);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

/* 底部链接 */
.form-footer {
  margin-top: 1.5rem;
  text-align: center;
}

.back-link {
  background: none;
  border: none;
  color: #6b7280;
  font-size: 0.9rem;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.5rem;
  transition: color 0.2s;
}

.back-link:hover {
  color: #1f2937;
}

.back-link svg {
  width: 16px;
  height: 16px;
}

/* Loading 动画 */
.spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 移动端适配 */
@media (max-width: 480px) {
  .login-card {
    padding: 2rem 1.5rem;
    box-shadow: none;
    background: transparent;
    border: none;
  }

  .bg-shape {
    display: none;
  }

  .login-wrapper {
    background: white;
    align-items: flex-start;
    padding-top: 2rem;
  }
}
</style>