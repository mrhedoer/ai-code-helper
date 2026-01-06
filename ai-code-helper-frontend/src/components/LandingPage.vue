<template>
  <div class="landing-container">
    <div class="content-card">
      <!-- 顶部标题栏 -->
      <div class="card-header">
        <div class="header-content">
          <div class="brand-logo">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h2>欢迎使用 AI 编程助手</h2>
          <p>请选择您的登录身份以继续</p>
        </div>
      </div>

      <div class="card-body">
        <div class="login-options-grid">
          <!-- 用户登录选项 -->
          <div class="option-card user" @click="goToUserLogin">
            <div class="icon-wrapper">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
            </div>
            <div class="card-content">
              <h3>用户登录</h3>
              <p>访问个人中心、查看历史记录</p>
            </div>
            <div class="arrow-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </div>
          </div>

          <!-- 管理员登录选项 -->
          <div class="option-card admin" @click="goToAdminLogin">
            <div class="icon-wrapper">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
            </div>
            <div class="card-content">
              <h3>管理员登录</h3>
              <p>进入后台管理系统、系统配置</p>
            </div>
            <div class="arrow-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LandingPage',
  methods: {
    goToUserLogin() {
      this.$router.push('/login')
    },
    goToAdminLogin() {
      this.$router.push('/admin/login')
    }
  }
}
</script>

<style scoped>
.landing-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f3f4f6;
  padding: 1.5rem;
}

.content-card {
  background: white;
  border-radius: 16px;
  width: 100%;
  max-width: 520px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 头部样式 */
.card-header {
  padding: 2.5rem 2rem 1rem;
  text-align: center;
}

.brand-logo {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #4f46e5 0%, #4338ca 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem;
  color: white;
}

.brand-logo svg {
  width: 32px;
  height: 32px;
}

.header-content h2 {
  margin: 0;
  font-size: 1.75rem;
  font-weight: 700;
  color: #111827;
}

.header-content p {
  margin: 0.75rem 0 0;
  color: #6b7280;
  font-size: 1rem;
}

/* 主体内容 */
.card-body {
  padding: 2rem;
}

.login-options-grid {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

/* 选项卡片 */
.option-card {
  display: flex;
  align-items: center;
  padding: 1.5rem;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: white;
}

.option-card:hover {
  border-color: #4f46e5;
  background: #f5f7ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 1.25rem;
  flex-shrink: 0;
}

.option-card.user .icon-wrapper {
  background: #e0e7ff;
  color: #4f46e5;
}

.option-card.admin .icon-wrapper {
  background: #fae8ff;
  color: #9333ea;
}

.icon-wrapper svg {
  width: 24px;
  height: 24px;
}

.card-content {
  flex: 1;
}

.card-content h3 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #1f2937;
}

.card-content p {
  margin: 0.25rem 0 0;
  font-size: 0.875rem;
  color: #6b7280;
}

.arrow-icon {
  color: #d1d5db;
  transition: transform 0.2s;
}

.option-card:hover .arrow-icon {
  color: #4f46e5;
  transform: translateX(4px);
}

.arrow-icon svg {
  width: 20px;
  height: 20px;
}

/* 响应式设计 */
@media (max-width: 640px) {
  .card-header {
    padding: 2rem 1.5rem 1rem;
  }

  .card-body {
    padding: 1.5rem;
  }

  .option-card {
    padding: 1.25rem;
  }
}
</style>
