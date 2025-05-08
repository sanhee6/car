/**
 * 考试数据验证中间件
 * 用于验证创建和更新考试的请求数据
 */

/**
 * 验证创建考试的请求数据
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateCreateExam = (req, res, next) => {
  const { course_id, title, exam_date, duration, location } = req.body;
  
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
      message: '考试标题不能为空'
    });
  }
  
  if (!exam_date) {
    return res.status(400).json({
      status: 'fail',
      message: '考试日期不能为空'
    });
  }
  
  if (!duration) {
    return res.status(400).json({
      status: 'fail',
      message: '考试时长不能为空'
    });
  }
  
  if (!location) {
    return res.status(400).json({
      status: 'fail',
      message: '考试地点不能为空'
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
  
  // 验证考试日期是否为有效日期
  try {
    const date = new Date(exam_date);
    if (isNaN(date.getTime())) {
      throw new Error('无效日期');
    }
  } catch (err) {
    return res.status(400).json({
      status: 'fail',
      message: '无效的考试日期格式'
    });
  }
  
  // 验证考试时长是否为正数
  const examDuration = parseInt(duration);
  if (isNaN(examDuration) || examDuration <= 0) {
    return res.status(400).json({
      status: 'fail',
      message: '考试时长必须为正数'
    });
  }
  
  // 所有验证通过，继续下一步
  next();
};

/**
 * 验证更新考试的请求数据
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateUpdateExam = (req, res, next) => {
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
      message: '无效的考试ID'
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
  
  // 如果提供了exam_date，验证其是否为有效日期
  if (req.body.exam_date !== undefined) {
    try {
      const date = new Date(req.body.exam_date);
      if (isNaN(date.getTime())) {
        throw new Error('无效日期');
      }
    } catch (err) {
      return res.status(400).json({
        status: 'fail',
        message: '无效的考试日期格式'
      });
    }
  }
  
  // 如果提供了duration，验证其是否为正数
  if (req.body.duration !== undefined) {
    const examDuration = parseInt(req.body.duration);
    if (isNaN(examDuration) || examDuration <= 0) {
      return res.status(400).json({
        status: 'fail',
        message: '考试时长必须为正数'
      });
    }
  }
  
  // 所有验证通过，继续下一步
  next();
};

/**
 * 验证考试ID参数
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateExamId = (req, res, next) => {
  const id = parseInt(req.params.id);
  if (isNaN(id) || id <= 0) {
    return res.status(400).json({
      status: 'fail',
      message: '无效的考试ID'
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