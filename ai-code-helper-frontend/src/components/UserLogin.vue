<template>
  <div class="login-page-container">
    <div class="login-card">
      <!-- 左侧品牌/装饰区域 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="brand-logo">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h1 class="brand-title">AI 编程助手</h1>
          <p class="brand-subtitle">智能代码生成，提升开发效率</p>

          <!-- 抽象背景装饰 -->
          <div class="abstract-shapes">
            <div class="shape shape-1"></div>
            <div class="shape shape-2"></div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单区域 -->
      <div class="form-section">
        <div class="form-wrapper">
          <div class="form-header">
            <h2>欢迎回来</h2>
            <p>请输入您的账号信息以继续</p>
          </div>

          <form @submit.prevent="handleLogin" class="auth-form">
            <!-- 用户名输入 -->
            <div class="input-group">
              <label for="username" class="input-label">用户名</label>
              <div class="input-wrapper" :class="{ 'has-error': errors.username }">
                <span class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="12" cy="7" r="4" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <input
                    id="username"
                    v-model="loginForm.username"
                    type="text"
                    placeholder="name@example.com"
                    autocomplete="username"
                    @blur="validateField('username')"
                    @input="clearError('username')"
                />
              </div>
              <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
            </div>

            <!-- 密码输入 -->
            <div class="input-group">
              <div class="label-row">
                <label for="password" class="input-label">密码</label>
              </div>
              <div class="input-wrapper" :class="{ 'has-error': errors.password }">
                <span class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <input
                    id="password"
                    v-model="loginForm.password"
                    :type="showPassword ? 'text' : 'password'"
                    placeholder="••••••••"
                    autocomplete="current-password"
                    @blur="validateField('password')"
                    @input="clearError('password')"
                />
                <button
                    type="button"
                    class="password-toggle-btn"
                    @click="togglePasswordVisibility"
                    tabindex="-1"
                >
                  <svg v-if="showPassword" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" stroke-linecap="round" stroke-linejoin="round"/>
                    <line x1="1" y1="1" x2="23" y2="23" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="12" cy="12" r="3" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </button>
              </div>
              <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
            </div>

            <!-- 辅助选项 -->
            <div class="form-options">
              <label class="checkbox-label">
                <input v-model="rememberMe" type="checkbox" />
                <span class="checkbox-custom"></span>
                <span class="checkbox-text">记住我</span>
              </label>
              <a href="#" class="forgot-link" @click.prevent="handleForgotPassword">忘记密码？</a>
            </div>

            <!-- 全局错误提示 -->
            <div v-if="errorMessage" class="alert-box">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="12" y1="8" x2="12" y2="12" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="12" y1="16" x2="12.01" y2="16" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>{{ errorMessage }}</span>
            </div>

            <!-- 登录按钮 -->
            <button
                type="submit"
                class="submit-btn"
                :disabled="isLoading || !isFormValid"
            >
              <span v-if="isLoading" class="spinner"></span>
              <span>{{ isLoading ? '登录中...' : '立即登录' }}</span>
            </button>

            <!-- 注册引导 -->
            <div class="register-prompt">
              还没有账户？
              <a href="#" @click.prevent="handleRegister">免费注册</a>
            </div>

            <!-- 返回首页按钮 -->
            <div class="back-link-container">
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
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { API_CONFIG } from '../config/api'

export default {
  name: 'UserLogin',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      errors: {
        username: '',
        password: ''
      },
      errorMessage: '',
      isLoading: false,
      showPassword: false,
      rememberMe: false
    }
  },
  computed: {
    isFormValid() {
      return this.loginForm.username.trim() !== '' &&
          this.loginForm.password.trim() !== '' &&
          !this.errors.username &&
          !this.errors.password
    }
  },
  mounted() {
    const savedUsername = localStorage.getItem('savedUsername')
    if (savedUsername) {
      this.loginForm.username = savedUsername
      this.rememberMe = true
    }
  },
  methods: {
    validateField(field) {
      this.errors[field] = ''

      if (field === 'username') {
        if (!this.loginForm.username.trim()) {
          this.errors.username = '请输入用户名'
        } else if (this.loginForm.username.trim().length < 3) {
          this.errors.username = '用户名长度不能少于3位'
        }
      } else if (field === 'password') {
        if (!this.loginForm.password) {
          this.errors.password = '请输入密码'
        } else if (this.loginForm.password.length < 6) {
          this.errors.password = '密码长度不能少于6位'
        }
      }
    },
    clearError(field) {
      this.errors[field] = ''
      this.errorMessage = ''
    },
    togglePasswordVisibility() {
      this.showPassword = !this.showPassword
    },
    async handleLogin() {
      this.errorMessage = ''
      this.validateField('username')
      this.validateField('password')

      if (!this.isFormValid) return

      this.isLoading = true

      try {
        const response = await axios.post(
            `${API_CONFIG.BASE_URL}/api/user/login`,
            this.loginForm,
            { timeout: API_CONFIG.TIMEOUT }
        )

        if (response.data.success) {
          const token = response.data.token
          sessionStorage.setItem('userToken', token)
          sessionStorage.setItem('username', this.loginForm.username)

          if (this.rememberMe) {
            localStorage.setItem('savedUsername', this.loginForm.username)
          } else {
            localStorage.removeItem('savedUsername')
          }

          const redirect = this.$route.query.redirect || '/chat'
          this.$router.push(redirect)
        } else {
          this.errorMessage = response.data.message || '登录失败，请检查账号信息'
        }
      } catch (error) {
        console.error('登录错误:', error)
        if (error.response?.status === 401 || error.response?.status === 403) {
          this.errorMessage = '用户名或密码错误'
        } else if (error.response?.data?.message) {
          this.errorMessage = error.response.data.message
        } else {
          this.errorMessage = '网络连接失败，请稍后重试'
        }
      } finally {
        this.isLoading = false
      }
    },
    handleForgotPassword() {
      // TODO: Implement forgot password logic
      console.log('Forgot password clicked')
    },
    handleRegister() {
      // TODO: Implement register logic
      console.log('Register clicked')
    },
    goBack() {
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
/* 容器与布局 */
.login-page-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f3f4f6; /* Gray-100 */
  padding: 1.5rem;
}

.login-card {
  display: flex;
  width: 100%;
  max-width: 1000px;
  min-height: 600px;
  background: #ffffff;
  border-radius: 1.5rem;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.1);
}

/* 左侧品牌区 */
.brand-section {
  flex: 1;
  background: linear-gradient(135deg, #4f46e5 0%, #4338ca 100%); /* Indigo-600 to 700 */
  padding: 4rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.brand-content {
  position: relative;
  z-index: 10;
}

.brand-logo {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 2rem;
  backdrop-filter: blur(4px);
}

.brand-logo svg {
  width: 32px;
  height: 32px;
  color: white;
}

.brand-title {
  font-size: 2.5rem;
  font-weight: 800;
  margin-bottom: 1rem;
  line-height: 1.2;
}

.brand-subtitle {
  font-size: 1.125rem;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 400;
}

/* 抽象背景装饰 */
.abstract-shapes .shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.shape-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  right: -50px;
}

.shape-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  left: -50px;
}

/* 右侧表单区 */
.form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4rem;
  background: white;
}

.form-wrapper {
  width: 100%;
  max-width: 360px;
}

.form-header {
  margin-bottom: 2.5rem;
}

.form-header h2 {
  font-size: 1.875rem;
  font-weight: 700;
  color: #111827; /* Gray-900 */
  margin-bottom: 0.5rem;
}

.form-header p {
  color: #6b7280; /* Gray-500 */
  font-size: 0.95rem;
}

.back-link-container {
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

/* 表单控件 */
.input-group {
  margin-bottom: 1.5rem;
}

.input-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151; /* Gray-700 */
  margin-bottom: 0.5rem;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  border: 1px solid #d1d5db; /* Gray-300 */
  border-radius: 0.5rem;
  transition: all 0.2s;
  background: #fff;
}

.input-wrapper:focus-within {
  border-color: #4f46e5; /* Indigo-600 */
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

.input-wrapper.has-error {
  border-color: #ef4444; /* Red-500 */
}

.input-wrapper.has-error:focus-within {
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.input-icon {
  padding-left: 0.75rem;
  color: #9ca3af; /* Gray-400 */
  display: flex;
  align-items: center;
}

.input-icon svg {
  width: 20px;
  height: 20px;
}

.input-wrapper input {
  width: 100%;
  padding: 0.75rem;
  border: none;
  background: transparent;
  font-size: 0.95rem;
  color: #1f2937;
  outline: none;
}

.input-wrapper input::placeholder {
  color: #9ca3af;
}

.password-toggle-btn {
  background: none;
  border: none;
  padding: 0.5rem 0.75rem;
  color: #9ca3af;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: color 0.2s;
}

.password-toggle-btn:hover {
  color: #4b5563;
}

.password-toggle-btn svg {
  width: 20px;
  height: 20px;
}

.error-message {
  display: block;
  color: #ef4444;
  font-size: 0.8rem;
  margin-top: 0.375rem;
}

/* 辅助选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.checkbox-label input {
  display: none;
}

.checkbox-custom {
  width: 16px;
  height: 16px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  margin-right: 0.5rem;
  position: relative;
  transition: all 0.2s;
}

.checkbox-label input:checked + .checkbox-custom {
  background-color: #4f46e5;
  border-color: #4f46e5;
}

.checkbox-label input:checked + .checkbox-custom::after {
  content: '';
  position: absolute;
  left: 5px;
  top: 2px;
  width: 4px;
  height: 8px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.checkbox-text {
  font-size: 0.875rem;
  color: #4b5563;
}

.forgot-link {
  font-size: 0.875rem;
  color: #4f46e5;
  text-decoration: none;
  font-weight: 500;
}

.forgot-link:hover {
  text-decoration: underline;
}

/* 全局错误提示框 */
.alert-box {
  background-color: #fef2f2;
  border: 1px solid #fee2e2;
  color: #b91c1c;
  padding: 0.75rem;
  border-radius: 0.5rem;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
}

.alert-box svg {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  padding: 0.75rem;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 0.5rem;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.submit-btn:hover:not(:disabled) {
  background-color: #4338ca;
  transform: translateY(-1px);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
}

.submit-btn:disabled {
  background-color: #a5b4fc;
  cursor: not-allowed;
}

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

/* 注册引导 */
.register-prompt {
  margin-top: 1.5rem;
  text-align: center;
  font-size: 0.875rem;
  color: #6b7280;
}

.register-prompt a {
  color: #4f46e5;
  text-decoration: none;
  font-weight: 600;
  margin-left: 0.25rem;
}

.register-prompt a:hover {
  text-decoration: underline;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    min-height: auto;
  }

  .brand-section {
    padding: 2rem;
    min-height: 200px;
  }

  .brand-logo {
    margin-bottom: 1rem;
  }

  .brand-title {
    font-size: 1.75rem;
  }

  .form-section {
    padding: 2rem 1.5rem;
  }
}
</style>
