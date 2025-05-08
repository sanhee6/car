/**
 * 作业数据验证中间件
 * 用于验证创建和更新作业的请求数据
 */

/**
 * 验证创建作业的请求数据
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateCreateAssignment = (req, res, next) => {
  const { course_id, title, due_date, max_score } = req.body;
  
  // 检查所有必填字段
  if (!course_id) {
    return res.status(400).json({
      status: 'fail',
      message: '课程ID不能为空'
    });
  }
  
  if (!title) {
    return res.status(400).json({
      status: 'fail',
      message: '作业标题不能为空'
    });
  }
  
  if (!due_date) {
    return res.status(400).json({
      status: 'fail',
      message: '截止日期不能为空'
    });
  }
  
  if (max_score === undefined) {
    return res.status(400).json({
      status: 'fail',
      message: '最高分值不能为空'
    });
  }
  
  // 验证课程ID是否为数字
  const courseId = parseInt(course_id);
  if (isNaN(courseId) || courseId <= 0) {
    return res.status(400).json({
      status: 'fail',
      message: '无效的课程ID'
    });
  }
  
  // 验证截止日期是否为有效日期
  try {
    const date = new Date(due_date);
    if (isNaN(date.getTime())) {
      throw new Error('无效日期');
    }
  } catch (err) {
    return res.status(400).json({
      status: 'fail',
      message: '无效的截止日期格式'
    });
  }
  
  // 验证最高分值是否为正数
  const maxScoreValue = parseFloat(max_score);
  if (isNaN(maxScoreValue) || maxScoreValue <= 0) {
    return res.status(400).json({
      status: 'fail',
      message: '最高分值必须为正数'
    });
  }
  
  // 所有验证通过，继续下一步
  next();
};

/**
 * 验证更新作业的请求数据
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateUpdateAssignment = (req, res, next) => {
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
      message: '无效的作业ID'
    });
  }
  
  // 如果提供了course_id，验证其是否为有效数字
  if (req.body.course_id !== undefined) {
    const courseId = parseInt(req.body.course_id);
    if (isNaN(courseId) || courseId <= 0) {
      return res.status(400).json({
        status: 'fail',
        message: '无效的课程ID'
      });
    }
  }
  
  // 如果提供了due_date，验证其是否为有效日期
  if (req.body.due_date !== undefined) {
    try {
      const date = new Date(req.body.due_date);
      if (isNaN(date.getTime())) {
        throw new Error('无效日期');
      }
    } catch (err) {
      return res.status(400).json({
        status: 'fail',
        message: '无效的截止日期格式'
      });
    }
  }
  
  // 如果提供了max_score，验证其是否为正数
  if (req.body.max_score !== undefined) {
    const maxScoreValue = parseFloat(req.body.max_score);
    if (isNaN(maxScoreValue) || maxScoreValue <= 0) {
      return res.status(400).json({
        status: 'fail',
        message: '最高分值必须为正数'
      });
    }
  }
  
  // 所有验证通过，继续下一步
  next();
};

/**
 * 验证作业ID参数
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateAssignmentId = (req, res, next) => {
  const id = parseInt(req.params.id);
  if (isNaN(id) || id <= 0) {
    return res.status(400).json({
      status: 'fail',
      message: '无效的作业ID'
    });
  }
  
  // ID验证通过，继续下一步
  next();
};

/**
 * 验证课程ID参数
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateCourseId = (req, res, next) => {
  const courseId = parseInt(req.params.courseId);
  if (isNaN(courseId) || courseId <= 0) {
    return res.status(400).json({
      status: 'fail',
      message: '无效的课程ID'
    });
  }
  
  // ID验证通过，继续下一步
  next();
}; 