// API配置文件
export const API_CONFIG = {
  // 后端服务地址
  BASE_URL: 'http://localhost:8081',
  
  // API端点
  ENDPOINTS: {
    CHAT: '/api/ai/chat'
  },
  
  // 超时设置（毫秒）
  TIMEOUT: 30000,
  
  // 重试次数
  MAX_RETRIES: 3
}

// 获取完整的API URL
export function getApiUrl(endpoint, params = {}) {
  const url = new URL(API_CONFIG.BASE_URL + endpoint)
  
  // 添加查询参数
  Object.keys(params).forEach(key => {
    if (params[key] !== null && params[key] !== undefined) {
      url.searchParams.append(key, params[key])
    }
  })
  
  return url.toString()
}
