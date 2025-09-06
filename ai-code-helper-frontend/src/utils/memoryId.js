/**
 * 生成唯一的会话ID
 * @returns {number} 会话ID
 */
export function generateMemoryId() {
  // 使用时间戳 + 随机数生成唯一ID
  return Date.now() + Math.floor(Math.random() * 1000)
}

/**
 * 验证会话ID是否有效
 * @param {any} id 要验证的ID
 * @returns {boolean} 是否有效
 */
export function isValidMemoryId(id) {
  return typeof id === 'number' && id > 0 && Number.isInteger(id)
}
