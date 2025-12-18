<template>
  <Transition name="modal-fade">
    <div v-if="show" class="modal-overlay" @click="handleOverlayClick">
      <div class="modal-container" @click.stop>
        <!-- 顶部标题栏 -->
        <div class="modal-header">
          <div class="header-content">
            <h2>欢迎回来</h2>
            <p>请选择您的登录身份以继续</p>
          </div>
          <button class="close-btn" @click="closeModal" aria-label="关闭">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="6" y1="6" x2="18" y2="18" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>

        <div class="modal-body">
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

        <div class="modal-footer">
          <button class="skip-link" @click="skipLogin">
            暂不登录，以访客身份继续
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script>
export default {
  name: 'LoginModal',
  props: {
    show: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    closeModal() {
      this.$emit('close')
    },
    handleOverlayClick() {
      // 点击遮罩层关闭
      this.closeModal()
    },
    goToUserLogin() {
      this.$router.push('/login')
      this.closeModal()
    },
    goToAdminLogin() {
      this.$router.push('/admin/login')
      this.closeModal()
    },
    skipLogin() {
      this.closeModal()
    }
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(17, 24, 39, 0.7); /* 深色遮罩 */
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-container {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 520px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 头部样式 */
.modal-header {
  padding: 1.5rem 1.5rem 0.5rem;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-content h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #111827;
}

.header-content p {
  margin: 0.5rem 0 0;
  color: #6b7280;
  font-size: 0.95rem;
}

.close-btn {
  background: transparent;
  border: none;
  color: #9ca3af;
  padding: 0.5rem;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  background: #f3f4f6;
  color: #4b5563;
}

.close-btn svg {
  width: 24px;
  height: 24px;
}

/* 主体内容 */
.modal-body {
  padding: 1.5rem;
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
  padding: 1.25rem;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: white;
}

.option-card:hover {
  border-color: #6366f1; /* Indigo-500 */
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
  margin-right: 1rem;
  flex-shrink: 0;
}

.option-card.user .icon-wrapper {
  background: #e0e7ff; /* Indigo-100 */
  color: #4f46e5; /* Indigo-600 */
}

.option-card.admin .icon-wrapper {
  background: #fae8ff; /* Purple-100 */
  color: #9333ea; /* Purple-600 */
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
  font-size: 0.85rem;
  color: #6b7280;
}

.arrow-icon {
  color: #d1d5db;
  transition: transform 0.2s;
}

.option-card:hover .arrow-icon {
  color: #6366f1;
  transform: translateX(4px);
}

.arrow-icon svg {
  width: 20px;
  height: 20px;
}

/* 底部样式 */
.modal-footer {
  padding: 1rem 1.5rem 1.5rem;
  text-align: center;
  border-top: 1px solid #f3f4f6;
  background: #f9fafb;
}

.skip-link {
  background: none;
  border: none;
  color: #6b7280;
  font-size: 0.875rem;
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  transition: color 0.2s;
}

.skip-link:hover {
  color: #111827;
  text-decoration: underline;
}

/* Vue Transition 动画 */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .modal-container {
  animation: modal-slide-in 0.3s ease-out;
}

.modal-fade-leave-active .modal-container {
  animation: modal-slide-out 0.2s ease-in;
}

@keyframes modal-slide-in {
  from {
    transform: scale(0.95) translateY(10px);
    opacity: 0;
  }
  to {
    transform: scale(1) translateY(0);
    opacity: 1;
  }
}

@keyframes modal-slide-out {
  from {
    transform: scale(1) translateY(0);
    opacity: 1;
  }
  to {
    transform: scale(0.95) translateY(10px);
    opacity: 0;
  }
}

/* 响应式设计 */
@media (max-width: 640px) {
  .modal-container {
    width: 95%;
    position: absolute;
    bottom: 0;
    border-radius: 20px 20px 0 0;
    max-width: none;
  }

  .modal-header {
    padding: 1.25rem;
  }

  .modal-body {
    padding: 1.25rem;
  }

  .option-card {
    padding: 1rem;
  }

  @keyframes modal-slide-in {
    from { transform: translateY(100%); }
    to { transform: translateY(0); }
  }

  @keyframes modal-slide-out {
    from { transform: translateY(0); }
    to { transform: translateY(100%); }
  }
}
</style>