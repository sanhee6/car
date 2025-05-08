/**
 * 课程数据验证中间件
 * 用于验证创建和更新课程的请求数据
 */

/**
 * 验证创建课程的请求数据
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateCreateCourse = (req, res, next) => {
  const { 
    name, 
    teacher, 
    weekday, 
    start_section, 
    end_section, 
    start_week, 
    end_week, 
    semester_id 
  } = req.body;
  
  // 检查必填字段
  if (!name) {
    return res.status(400).json({
      status: 'fail',
      message: '课程名称不能为空'
    });
  }
  
  if (!teacher) {
    return res.status(400).json({
      status: 'fail',
      message: '教师姓名不能为空'
    });
  }
  
  // 验证周几是否为1-7之间的整数
  if (weekday === undefined || !Number.isInteger(Number(weekday)) || Number(weekday) < 1 || Number(weekday) > 7) {
    return res.status(400).json({
      status: 'fail',
      message: '周几必须是1到7之间的整数'
    });
  }
  
  // 验证开始节次
  if (start_section === undefined || !Number.isInteger(Number(start_section)) || Number(start_section) < 1) {
    return res.status(400).json({
      status: 'fail',
      message: '开始节次必须是大于0的整数'
    });
  }
  
  // 验证结束节次
  if (end_section === undefined || !Number.isInteger(Number(end_section)) || Number(end_section) < Number(start_section)) {
    return res.status(400).json({
      status: 'fail',
      message: '结束节次必须是大于等于开始节次的整数'
    });
  }
  
  // 验证开始周次
  if (start_week === undefined || !Number.isInteger(Number(start_week)) || Number(start_week) < 1) {
    return res.status(400).json({
      status: 'fail',
      message: '开始周次必须是大于0的整数'
    });
  }
  
  // 验证结束周次
  if (end_week === undefined || !Number.isInteger(Number(end_week)) || Number(end_week) < Number(start_week)) {
    return res.status(400).json({
      status: 'fail',
      message: '结束周次必须是大于等于开始周次的整数'
    });
  }
  
  // 验证学期标识
  if (!semester_id) {
    return res.status(400).json({
      status: 'fail',
      message: '学期标识不能为空'
    });
  }
  
  // 所有验证通过，继续下一步
  next();
};

/**
 * 验证更新课程的请求数据
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateUpdateCourse = (req, res, next) => {
  const { 
    weekday, 
    start_section, 
    end_section, 
    start_week, 
    end_week 
  } = req.body;
  
  // 至少需要有一个字段要更新
  if (Object.keys(req.body).length === 0) {
    return res.status(400).json({
      status: 'fail',
      message: '至少需要提供一个更新字段'
    });
  }
  
  // 验证课程ID
  const id = parseInt(req.params.id);
  if (isNaN(id) || id <= 0) {
    return res.status(400).json({
      status: 'fail',
      message: '无效的课程ID'
    });
  }
  
  // 验证周几
  if (weekday !== undefined && (!Number.isInteger(Number(weekday)) || Number(weekday) < 1 || Number(weekday) > 7)) {
    return res.status(400).json({
      status: 'fail',
      message: '周几必须是1到7之间的整数'
    });
  }
  
  // 验证开始节次
  if (start_section !== undefined && (!Number.isInteger(Number(start_section)) || Number(start_section) < 1)) {
    return res.status(400).json({
      status: 'fail',
      message: '开始节次必须是大于0的整数'
    });
  }
  
  // 验证结束节次（如果同时提供了开始节次和结束节次）
  if (start_section !== undefined && end_section !== undefined && Number(end_section) < Number(start_section)) {
    return res.status(400).json({
      status: 'fail',
      message: '结束节次必须是大于等于开始节次的整数'
    });
  }
  
  // 验证开始周次
  if (start_week !== undefined && (!Number.isInteger(Number(start_week)) || Number(start_week) < 1)) {
    return res.status(400).json({
      status: 'fail',
      message: '开始周次必须是大于0的整数'
    });
  }
  
  // 验证结束周次（如果同时提供了开始周次和结束周次）
  if (start_week !== undefined && end_week !== undefined && Number(end_week) < Number(start_week)) {
    return res.status(400).json({
      status: 'fail',
      message: '结束周次必须是大于等于开始周次的整数'
    });
  }
  
  // 所有验证通过，继续下一步
  next();
};

/**
 * 验证课程ID参数
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateCourseId = (req, res, next) => {
  const id = parseInt(req.params.id);
  if (isNaN(id) || id <= 0) {
    return res.status(400).json({
      status: 'fail',
      message: '无效的课程ID'
    });
  }
  
  // ID验证通过，继续下一步
  next();
}; 