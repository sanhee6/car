/**
 * 课程表数据验证中间件
 * 用于验证创建和更新课程表的请求数据
 */

/**
 * 验证创建课程表的请求数据
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateCreateCourseSchedule = (req, res, next) => {
  const { course_name, teacher_name, class_time, classroom } = req.body;
  
  // 检查所有必填字段
  if (!course_name) {
    return res.status(400).json({
      status: 'fail',
      message: '课程名称不能为空'
    });
  }
  
  if (!teacher_name) {
    return res.status(400).json({
      status: 'fail',
      message: '教师姓名不能为空'
    });
  }
  
  if (!class_time) {
    return res.status(400).json({
      status: 'fail',
      message: '上课时间不能为空'
    });
  }
  
  if (!classroom) {
    return res.status(400).json({
      status: 'fail',
      message: '教室不能为空'
    });
  }
  
  // 所有验证通过，继续下一步
  next();
};

/**
 * 验证更新课程表的请求数据
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateUpdateCourseSchedule = (req, res, next) => {
  // 至少需要有一个字段要更新
  if (Object.keys(req.body).length === 0) {
    return res.status(400).json({
      status: 'fail',
      message: '至少需要提供一个更新字段'
    });
  }
  
  // 验证ID参数是否为数字
  const id = parseInt(req.params.id);
  if (isNaN(id) || id <= 0) {
    return res.status(400).json({
      status: 'fail',
      message: '无效的课程表ID'
    });
  }
  
  // 所有验证通过，继续下一步
  next();
};

/**
 * 验证课程表ID参数
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateCourseScheduleId = (req, res, next) => {
  const id = parseInt(req.params.id);
  if (isNaN(id) || id <= 0) {
    return res.status(400).json({
      status: 'fail',
      message: '无效的课程表ID'
    });
  }
  
  // ID验证通过，继续下一步
  next();
}; 