/**
 * 字段映射工具
 * 确保API返回的字段名一致
 */

/**
 * 转换作业数据字段
 * 确保使用API规范的字段名
 * @param {Object|Array} data - 要转换的数据
 * @returns {Object|Array} 转换后的数据
 */
const transformAssignmentFields = (data) => {
  if (!data) return data;
  
  // 处理数组
  if (Array.isArray(data)) {
    return data.map(item => transformAssignmentFields(item));
  }
  
  // 处理单个对象
  const result = { ...data };
  
  // 确保使用API规范的字段名 - 修改为客户端需要的字段名
  if (result.due_date !== undefined && result.deadline === undefined) {
    result.deadline = result.due_date;
    delete result.due_date;
    console.log('字段映射: due_date → deadline');
  }
  
  if (result.max_score !== undefined && result.total_score === undefined) {
    result.total_score = result.max_score;
    delete result.max_score;
    console.log('字段映射: max_score → total_score');
  }
  
  return result;
};

/**
 * 转换成绩数据字段
 * 确保使用API规范的字段名
 * @param {Object|Array} data - 要转换的数据
 * @returns {Object|Array} 转换后的数据
 */
const transformGradeFields = (data) => {
  if (!data) return data;
  
  // 处理数组
  if (Array.isArray(data)) {
    return data.map(item => transformGradeFields(item));
  }
  
  // 处理单个对象
  const result = { ...data };
  
  // 移除feedback字段，客户端不再使用此字段
  if (result.feedback !== undefined) {
    delete result.feedback;
    console.log('移除feedback字段');
  }
  
  return result;
};

/**
 * 中间件：自动转换所有API响应中的字段名
 * @param {Function} transformer - 要使用的转换函数
 * @returns {Function} Express中间件
 */
const fieldMapperMiddleware = (transformer) => {
  return (req, res, next) => {
    // 保存原始的res.json方法
    const originalJson = res.json;
    
    // 覆盖res.json方法，在发送响应前应用转换
    res.json = function(data) {
      console.log('应用API响应字段映射');
      
      // 如果响应包含data字段，只转换data部分
      if (data && data.data) {
        data.data = transformer(data.data);
      } else {
        // 否则转换整个响应
        data = transformer(data);
      }
      
      // 调用原始json方法发送转换后的数据
      return originalJson.call(this, data);
    };
    
    next();
  };
};

module.exports = {
  transformAssignmentFields,
  transformGradeFields,
  fieldMapperMiddleware
}; 