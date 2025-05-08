/**
 * 成绩数据验证中间件
 * 用于验证创建和更新成绩的请求数据
 */

/**
 * 验证创建成绩的请求数据
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateCreateGrade = (req, res, next) => {
  const { student_id, course_id, assignment_id, score } = req.body;
  
  // 检查所有必填字段
  if (!student_id) {
    return res.status(400).json({
      status: 'fail',
      message: '学生ID不能为空'
    });
  }
  
  if (!course_id) {
    return res.status(400).json({
      status: 'fail',
      message: '课程ID不能为空'
    });
  }
  
  if (!assignment_id) {
    return res.status(400).json({
      status: 'fail',
      message: '作业ID不能为空'
    });
  }
  
  if (score === undefined) {
    return res.status(400).json({
      status: 'fail',
      message: '成绩分数不能为空'
    });
  }
  
  // 验证学生ID是否为数字
  const studentId = parseInt(student_id);
  if (isNaN(studentId) || studentId <= 0) {
    return res.status(400).json({
      status: 'fail',
      message: '无效的学生ID'
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
  
  // 验证作业ID是否为数字
  const assignmentId = parseInt(assignment_id);
  if (isNaN(assignmentId) || assignmentId <= 0) {
    return res.status(400).json({
      status: 'fail',
      message: '无效的作业ID'
    });
  }
  
  // 验证分数是否为非负数值
  const scoreValue = parseFloat(score);
  if (isNaN(scoreValue) || scoreValue < 0) {
    return res.status(400).json({
      status: 'fail',
      message: '成绩分数必须为非负数'
    });
  }
  
  // 验证提交日期格式
  if (req.body.submission_date) {
    try {
      const date = new Date(req.body.submission_date);
      if (isNaN(date.getTime())) {
        throw new Error('无效日期');
      }
    } catch (err) {
      return res.status(400).json({
        status: 'fail',
        message: '无效的提交日期格式'
      });
    }
  }
  
  // 所有验证通过，继续下一步
  next();
};

/**
 * 验证更新成绩的请求数据
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateUpdateGrade = (req, res, next) => {
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
      message: '无效的成绩ID'
    });
  }
  
  // 如果提供了student_id，验证其是否为有效数字
  if (req.body.student_id !== undefined) {
    const studentId = parseInt(req.body.student_id);
    if (isNaN(studentId) || studentId <= 0) {
      return res.status(400).json({
        status: 'fail',
        message: '无效的学生ID'
      });
    }
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
  
  // 如果提供了assignment_id，验证其是否为有效数字
  if (req.body.assignment_id !== undefined) {
    const assignmentId = parseInt(req.body.assignment_id);
    if (isNaN(assignmentId) || assignmentId <= 0) {
      return res.status(400).json({
        status: 'fail',
        message: '无效的作业ID'
      });
    }
  }
  
  // 如果提供了score，验证其是否为非负数值
  if (req.body.score !== undefined) {
    const scoreValue = parseFloat(req.body.score);
    if (isNaN(scoreValue) || scoreValue < 0) {
      return res.status(400).json({
        status: 'fail',
        message: '成绩分数必须为非负数'
      });
    }
  }
  
  // 如果提供了submission_date，验证其是否为有效日期
  if (req.body.submission_date !== undefined) {
    try {
      const date = new Date(req.body.submission_date);
      if (isNaN(date.getTime())) {
        throw new Error('无效日期');
      }
    } catch (err) {
      return res.status(400).json({
        status: 'fail',
        message: '无效的提交日期格式'
      });
    }
  }
  
  // 所有验证通过，继续下一步
  next();
};

/**
 * 验证成绩ID参数
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateGradeId = (req, res, next) => {
  const id = parseInt(req.params.id);
  if (isNaN(id) || id <= 0) {
    return res.status(400).json({
      status: 'fail',
      message: '无效的成绩ID'
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

/**
 * 验证学生ID参数
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateStudentId = (req, res, next) => {
  const studentId = parseInt(req.params.studentId);
  if (isNaN(studentId) || studentId <= 0) {
    return res.status(400).json({
      status: 'fail',
      message: '无效的学生ID'
    });
  }
  
  // ID验证通过，继续下一步
  next();
};

/**
 * 验证作业ID参数
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express中间件下一步函数
 */
exports.validateAssignmentId = (req, res, next) => {
  const assignmentId = parseInt(req.params.assignmentId);
  if (isNaN(assignmentId) || assignmentId <= 0) {
    return res.status(400).json({
      status: 'fail',
      message: '无效的作业ID'
    });
  }
  
  // ID验证通过，继续下一步
  next();
}; 