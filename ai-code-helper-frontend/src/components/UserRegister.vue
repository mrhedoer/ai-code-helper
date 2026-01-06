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
          <h1 class="brand-title">加入我们</h1>
          <p class="brand-subtitle">开启您的智能编程之旅</p>

          <!-- 抽象背景装饰 -->
          <div class="abstract-shapes">
            <div class="shape shape-1"></div>
            <div class="shape shape-2"></div>
          </div>
        </div>
      </div>

      <!-- 右侧注册表单区域 -->
      <div class="form-section">
        <div class="form-wrapper">
          <div class="form-header">
            <h2>创建账户</h2>
            <p>填写以下信息完成注册</p>
          </div>

          <form @submit.prevent="handleRegister" class="auth-form">
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
                    v-model="registerForm.username"
                    type="text"
                    placeholder="请输入用户名"
                    autocomplete="username"
                    @blur="validateField('username')"
                    @input="clearError('username')"
                />
              </div>
              <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
            </div>

            <!-- 邮箱输入 -->
            <div class="input-group">
              <label for="email" class="input-label">邮箱（可选）</label>
              <div class="input-wrapper" :class="{ 'has-error': errors.email }">
                <span class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" stroke-linecap="round" stroke-linejoin="round"/>
                    <polyline points="22,6 12,13 2,6" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <input
                    id="email"
                    v-model="registerForm.email"
                    type="email"
                    placeholder="name@example.com"
                    autocomplete="email"
                    @blur="validateField('email')"
                    @input="clearError('email')"
                />
              </div>
              <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
            </div>

            <!-- 密码输入 -->
            <div class="input-group">
              <label for="password" class="input-label">密码</label>
              <div class="input-wrapper" :class="{ 'has-error': errors.password }">
                <span class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <input
                    id="password"
                    v-model="registerForm.password"
                    :type="showPassword ? 'text' : 'password'"
                    placeholder="••••••••"
                    autocomplete="new-password"
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

            <!-- 确认密码输入 -->
            <div class="input-group">
              <label for="confirmPassword" class="input-label">确认密码</label>
              <div class="input-wrapper" :class="{ 'has-error': errors.confirmPassword }">
                <span class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
                <input
                    id="confirmPassword"
                    v-model="registerForm.confirmPassword"
                    :type="showPassword ? 'text' : 'password'"
                    placeholder="••••••••"
                    autocomplete="new-password"
                    @blur="validateField('confirmPassword')"
                    @input="clearError('confirmPassword')"
                />
              </div>
              <span v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</span>
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

            <!-- 注册按钮 -->
            <button
                type="submit"
                class="submit-btn"
                :disabled="isLoading || !isFormValid"
            >
              <span v-if="isLoading" class="spinner"></span>
              <span>{{ isLoading ? '注册中...' : '立即注册' }}</span>
            </button>

            <!-- 登录引导 -->
            <div class="register-prompt">
              已有账户？
              <router-link to="/login">立即登录</router-link>
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
  name: 'UserRegister',
  data() {
    return {
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        email: ''
      },
      errors: {
        username: '',
        password: '',
        confirmPassword: '',
        email: ''
      },
      errorMessage: '',
      isLoading: false,
      showPassword: false
    }
  },
  computed: {
    isFormValid() {
      return this.registerForm.username.trim() !== '' &&
          this.registerForm.password.trim() !== '' &&
          this.registerForm.confirmPassword.trim() !== '' &&
          !this.errors.username &&
          !this.errors.password &&
          !this.errors.confirmPassword &&
          !this.errors.email
    }
  },
  methods: {
    validateField(field) {
      this.errors[field] = ''

      if (field === 'username') {
        if (!this.registerForm.username.trim()) {
          this.errors.username = '请输入用户名'
        } else if (this.registerForm.username.trim().length < 3) {
          this.errors.username = '用户名长度不能少于3位'
        }
      } else if (field === 'password') {
        if (!this.registerForm.password) {
          this.errors.password = '请输入密码'
        } else if (this.registerForm.password.length < 6) {
          this.errors.password = '密码长度不能少于6位'
        }
      } else if (field === 'confirmPassword') {
        if (this.registerForm.confirmPassword !== this.registerForm.password) {
          this.errors.confirmPassword = '两次输入的密码不一致'
        }
      } else if (field === 'email') {
        if (this.registerForm.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.registerForm.email)) {
          this.errors.email = '请输入有效的邮箱地址'
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
    async handleRegister() {
      this.errorMessage = ''
      this.validateField('username')
      this.validateField('password')
      this.validateField('confirmPassword')
      this.validateField('email')

      if (!this.isFormValid) return

      this.isLoading = true

      try {
        const response = await axios.post(
            `${API_CONFIG.BASE_URL}/api/user/register`,
            {
              username: this.registerForm.username,
              password: this.registerForm.password,
              email: this.registerForm.email
            },
            { timeout: API_CONFIG.TIMEOUT }
        )

        if (response.data.success) {
          alert('注册成功，请登录')
          this.$router.push('/login')
        } else {
          this.errorMessage = response.data.message || '注册失败，请稍后重试'
        }
      } catch (error) {
        console.error('注册错误:', error)
        if (error.response?.data?.message) {
          this.errorMessage = error.response.data.message
        } else {
          this.errorMessage = '网络连接失败，请稍后重试'
        }
      } finally {
        this.isLoading = false
      }
    }
  }
}
</script>

<style scoped>
/* 复用 UserLogin.vue 的样式 */
.login-page-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f3f4f6;
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

.brand-section {
  flex: 1;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%); /* Emerald-500 to 600 */
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
  color: #111827;
  margin-bottom: 0.5rem;
}

.form-header p {
  color: #6b7280;
  font-size: 0.95rem;
}

.input-group {
  margin-bottom: 1.5rem;
}

.input-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
  margin-bottom: 0.5rem;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  border: 1px solid #d1d5db;
  border-radius: 0.5rem;
  transition: all 0.2s;
  background: #fff;
}

.input-wrapper:focus-within {
  border-color: #10b981; /* Emerald-500 */
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.input-wrapper.has-error {
  border-color: #ef4444;
}

.input-wrapper.has-error:focus-within {
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.input-icon {
  padding-left: 0.75rem;
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

.submit-btn {
  width: 100%;
  padding: 0.75rem;
  background-color: #10b981; /* Emerald-500 */
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
  background-color: #059669; /* Emerald-600 */
  transform: translateY(-1px);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
}

.submit-btn:disabled {
  background-color: #a7f3d0;
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

.register-prompt {
  margin-top: 1.5rem;
  text-align: center;
  font-size: 0.875rem;
  color: #6b7280;
}

.register-prompt a {
  color: #10b981; /* Emerald-500 */
  text-decoration: none;
  font-weight: 600;
  margin-left: 0.25rem;
}

.register-prompt a:hover {
  text-decoration: underline;
}

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
