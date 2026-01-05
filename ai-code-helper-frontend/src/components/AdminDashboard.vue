<template>
  <div class="admin-dashboard">
    <!-- 顶部导航栏 -->
    <nav class="dashboard-nav">
      <div class="nav-content">
        <div class="brand">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
            </svg>
          </div>
          <h1>管理后台</h1>
        </div>
        <div class="user-profile">
          <div class="avatar">
            <span>{{ adminUsername.charAt(0).toUpperCase() }}</span>
          </div>
          <span class="username">{{ adminUsername }}</span>
          <button @click="logout" class="logout-btn" title="退出登录">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
              <polyline points="16 17 21 12 16 7" />
              <line x1="21" y1="12" x2="9" y2="12" />
            </svg>
          </button>
        </div>
      </div>
    </nav>

    <main class="main-content">
      <!-- 顶部统计卡片区 -->
      <div class="stats-grid">
        <div class="stat-card primary">
          <div class="stat-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
            </svg>
          </div>
          <div class="stat-info">
            <span class="label">总消息数</span>
            <span class="value">{{ totalMessages }}</span>
          </div>
        </div>

        <div class="stat-card success">
          <div class="stat-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M17 21v-2a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v2" />
              <circle cx="9" cy="7" r="4" />
              <path d="M23 21v-2a2 2 0 0 0-2-2m-3-9a4 4 0 0 1 0 7.75" />
            </svg>
          </div>
          <div class="stat-info">
            <span class="label">活跃会话</span>
            <span class="value">{{ uniqueConversations }}</span>
          </div>
        </div>

        <div class="stat-card info">
          <div class="stat-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
          </div>
          <div class="stat-info">
            <span class="label">用户提问</span>
            <span class="value">{{ userMessages }}</span>
          </div>
        </div>

        <div class="stat-card warning">
          <div class="stat-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="2" y="3" width="20" height="14" rx="2" ry="2" />
              <line x1="8" y1="21" x2="16" y2="21" />
              <line x1="12" y1="17" x2="12" y2="21" />
            </svg>
          </div>
          <div class="stat-info">
            <span class="label">AI 回复</span>
            <span class="value">{{ aiMessages }}</span>
          </div>
        </div>
      </div>

      <!-- 主要操作区 -->
      <div class="content-card">
        <div class="card-header">
          <h2>消息管理列表</h2>
          <div class="search-bar">
            <div class="input-group">
              <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8" />
                <line x1="21" y1="21" x2="16.65" y2="16.65" />
              </svg>
              <input
                  v-model="searchKeyword"
                  type="text"
                  placeholder="搜索会话ID或内容..."
                  @keyup.enter="searchMessages"
              />
            </div>
            <button @click="searchMessages" class="btn btn-primary">
              搜索
            </button>
            <button @click="loadAllMessages" class="btn btn-outline" title="刷新列表">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="btn-icon">
                <path d="M23 4v6h-6" />
                <path d="M1 20v-6h6" />
                <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15" />
              </svg>
            </button>
          </div>
        </div>

        <!-- 表格区域 -->
        <div class="table-container">
          <div v-if="isLoading" class="state-box loading">
            <div class="spinner"></div>
            <p>正在加载数据...</p>
          </div>

          <div v-else-if="messages.length === 0" class="state-box empty">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1">
              <path d="M13 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z"></path>
              <polyline points="13 2 13 9 20 9"></polyline>
            </svg>
            <p>暂无相关数据</p>
          </div>

          <table v-else class="modern-table">
            <thead>
            <tr>
              <th width="80">ID</th>
              <th width="120">会话ID</th>
              <th width="100">角色</th>
              <th>消息内容</th>
              <th width="180">时间</th>
              <th width="160">操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="message in messages" :key="message.id">
              <td class="text-secondary">#{{ message.id }}</td>
              <td class="font-mono">{{ message.conversationId }}</td>
              <td>
                  <span :class="['badge', message.messageType.toLowerCase()]">
                    {{ message.messageType === 'USER' ? '用户' : 'AI 助手' }}
                  </span>
              </td>
              <td>
                <div class="content-cell" :title="message.messageContent">
                  {{ message.messageContent }}
                </div>
              </td>
              <td class="text-secondary text-sm">
                {{ formatDate(message.createdAt) }}
              </td>
              <td>
                <div class="action-buttons">
                  <button @click="deleteMessage(message.id)" class="action-btn delete" title="删除单条">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="3 6 5 6 21 6"></polyline>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                    </svg>
                  </button>
                  <button @click="deleteConversation(message.conversationId)" class="action-btn warning" title="清空会话">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                      <line x1="9" y1="9" x2="15" y2="15"></line>
                      <line x1="15" y1="9" x2="9" y2="15"></line>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import axios from 'axios'
import { API_CONFIG } from '../config/api'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      messages: [],
      searchKeyword: '',
      isLoading: false,
      adminUsername: ''
    }
  },
  computed: {
    totalMessages() {
      return this.messages.length
    },
    uniqueConversations() {
      const conversations = new Set(this.messages.map(m => m.conversationId))
      return conversations.size
    },
    userMessages() {
      return this.messages.filter(m => m.messageType === 'USER').length
    },
    aiMessages() {
      return this.messages.filter(m => m.messageType === 'AI').length
    }
  },
  mounted() {
    this.adminUsername = sessionStorage.getItem('adminUsername') || '管理员'
    this.loadAllMessages()
  },
  methods: {
    async loadAllMessages() {
      this.isLoading = true
      try {
        const response = await axios.get(
            `${API_CONFIG.BASE_URL}/api/admin/messages`,
            {
              headers: {
                'Authorization': sessionStorage.getItem('adminToken')
              }
            }
        )
        this.messages = response.data
      } catch (error) {
        console.error('加载数据失败:', error)
        if (error.response?.status === 401) {
          alert('登录已过期，请重新登录')
          this.logout()
        } else {
          // 这里可以使用更友好的 Toast 提示，暂时保持 alert
          // alert('加载数据失败')
        }
      } finally {
        this.isLoading = false
      }
    },

    async searchMessages() {
      if (!this.searchKeyword.trim()) {
        this.loadAllMessages()
        return
      }

      this.isLoading = true
      try {
        const response = await axios.get(
            `${API_CONFIG.BASE_URL}/api/admin/messages/search`,
            {
              params: { keyword: this.searchKeyword },
              headers: {
                'Authorization': sessionStorage.getItem('adminToken')
              }
            }
        )
        this.messages = response.data
      } catch (error) {
        console.error('搜索失败:', error)
        alert('搜索失败')
      } finally {
        this.isLoading = false
      }
    },

    async deleteMessage(id) {
      if (!confirm('确定要删除这条消息吗？')) {
        return
      }

      try {
        await axios.delete(
            `${API_CONFIG.BASE_URL}/api/admin/messages/${id}`,
            {
              headers: {
                'Authorization': sessionStorage.getItem('adminToken')
              }
            }
        )
        // 乐观更新：直接在前端移除，减少一次请求
        this.messages = this.messages.filter(m => m.id !== id)
      } catch (error) {
        console.error('删除失败:', error)
        alert('删除失败')
      }
    },

    async deleteConversation(conversationId) {
      if (!confirm(`确定要删除会话 ${conversationId} 的所有消息吗？`)) {
        return
      }

      try {
        await axios.delete(
            `${API_CONFIG.BASE_URL}/api/admin/conversations/${conversationId}`,
            {
              headers: {
                'Authorization': sessionStorage.getItem('adminToken')
              }
            }
        )
        // 乐观更新
        this.messages = this.messages.filter(m => m.conversationId !== conversationId)
      } catch (error) {
        console.error('删除失败:', error)
        alert('删除失败')
      }
    },

    formatDate(dateString) {
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },

    logout() {
      sessionStorage.removeItem('adminToken')
      sessionStorage.removeItem('adminUsername')
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
/* 全局变量定义 */
:root {
  --primary-color: #6366f1; /* Indigo */
  --primary-hover: #4f46e5;
  --bg-color: #f3f4f6;
  --card-bg: #ffffff;
  --text-main: #1f2937;
  --text-secondary: #6b7280;
  --border-color: #e5e7eb;
  --danger-color: #ef4444;
  --warning-color: #f59e0b;
  --success-color: #10b981;
}

/* 基础布局 */
.admin-dashboard {
  min-height: 100vh;
  background-color: #f8fafc; /* 更清爽的灰白背景 */
  color: #1e293b;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

/* 导航栏 */
.dashboard-nav {
  background: #ffffff;
  height: 64px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 50;
}

.nav-content {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  padding: 0 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.logo-icon svg {
  width: 20px;
  height: 20px;
}

.brand h1 {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 12px;
  background: #f1f5f9;
  border-radius: 20px;
}

.avatar {
  width: 32px;
  height: 32px;
  background: #6366f1;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.username {
  font-size: 0.9rem;
  font-weight: 500;
}

.logout-btn {
  background: none;
  border: none;
  color: #64748b;
  cursor: pointer;
  padding: 4px;
  border-radius: 50%;
  display: flex;
  transition: all 0.2s;
}

.logout-btn:hover {
  color: #ef4444;
  background: #fee2e2;
}

.logout-btn svg {
  width: 18px;
  height: 18px;
}

/* 主内容区 */
.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

/* 统计卡片 Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  padding: 1.5rem;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
  border: 1px solid rgba(0,0,0,0.02);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon svg {
  width: 28px;
  height: 28px;
}

/* 统计卡片颜色变体 */
.stat-card.primary .stat-icon { background: #e0e7ff; color: #6366f1; }
.stat-card.success .stat-icon { background: #dcfce7; color: #10b981; }
.stat-card.info .stat-icon { background: #e0f2fe; color: #0ea5e9; }
.stat-card.warning .stat-icon { background: #fef3c7; color: #f59e0b; }

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-info .label {
  color: #64748b;
  font-size: 0.875rem;
  margin-bottom: 4px;
}

.stat-info .value {
  color: #0f172a;
  font-size: 1.75rem;
  font-weight: 700;
  line-height: 1;
}

/* 内容卡片 (表格容器) */
.content-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.card-header {
  padding: 1.5rem;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.card-header h2 {
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0;
  color: #0f172a;
}

/* 搜索栏 */
.search-bar {
  display: flex;
  gap: 12px;
  align-items: center;
}

.input-group {
  position: relative;
  width: 300px;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: #94a3b8;
}

.input-group input {
  width: 100%;
  padding: 10px 12px 10px 40px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.9rem;
  transition: all 0.2s;
  background: #f8fafc;
}

.input-group input:focus {
  outline: none;
  border-color: #6366f1;
  background: white;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

/* 按钮样式 */
.btn {
  padding: 0.6rem 1.2rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn-primary {
  background: #6366f1;
  color: white;
}

.btn-primary:hover {
  background: #4f46e5;
  transform: translateY(-1px);
}

.btn-outline {
  background: white;
  border: 1px solid #e2e8f0;
  color: #64748b;
  padding: 0.6rem;
}

.btn-outline:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
  color: #0f172a;
}

.btn-icon {
  width: 20px;
  height: 20px;
}

/* 表格样式 */
.table-container {
  overflow-x: auto;
}

.modern-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

.modern-table th {
  background: #f8fafc;
  padding: 1rem 1.5rem;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  color: #64748b;
  letter-spacing: 0.05em;
  border-bottom: 1px solid #e2e8f0;
}

.modern-table td {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #f1f5f9;
  color: #334155;
  font-size: 0.9rem;
  vertical-align: middle;
}

.modern-table tr:hover {
  background: #f8fafc;
}

.font-mono {
  font-family: 'Monaco', 'Consolas', monospace;
  font-size: 0.85rem;
  color: #64748b;
}

.text-secondary {
  color: #94a3b8;
}

.text-sm {
  font-size: 0.85rem;
}

/* 徽章样式 */
.badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  display: inline-block;
}

.badge.user {
  background: #eff6ff;
  color: #3b82f6;
  border: 1px solid #dbeafe;
}

.badge.ai {
  background: #f5f3ff;
  color: #8b5cf6;
  border: 1px solid #ede9fe;
}

/* 消息内容截断 */
.content-cell {
  max-width: 400px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #1e293b;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  background: transparent;
}

.action-btn svg {
  width: 16px;
  height: 16px;
}

.action-btn.delete {
  color: #ef4444;
  background: #fef2f2;
}

.action-btn.delete:hover {
  background: #fee2e2;
}

.action-btn.warning {
  color: #f59e0b;
  background: #fffbeb;
}

.action-btn.warning:hover {
  background: #fef3c7;
}

/* 状态提示 */
.state-box {
  padding: 4rem;
  text-align: center;
  color: #94a3b8;
}

.state-box svg {
  width: 48px;
  height: 48px;
  margin-bottom: 1rem;
  color: #cbd5e1;
}

.spinner {
  width: 30px;
  height: 30px;
  border: 3px solid #e2e8f0;
  border-top-color: #6366f1;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 响应式适配 */
@media (max-width: 768px) {
  .nav-content {
    padding: 0 1rem;
  }

  .card-header {
    flex-direction: column;
    align-items: stretch;
  }

  .input-group {
    width: 100%;
  }

  .search-bar {
    flex-direction: column;
    width: 100%;
  }

  .btn {
    width: 100%;
  }

  .content-cell {
    max-width: 200px;
  }
}
</style>