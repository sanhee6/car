const Grade = require('../models/grade.model.js');
const { transformGradeFields } = require('../utils/field_mapper.js');

/**
 * 成绩控制器
 * 处理成绩相关的业务逻辑
 */

/**
 * 获取成绩列表（支持分页）
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.getGrades = async (req, res) => {
  try {
    // 解析分页参数
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;
    
    // 获取成绩列表
    const result = await Grade.getAll(page, limit);
    
    // 转换字段名称，确保API一致性
    const transformedRows = transformGradeFields(result.rows);
    
    res.json({
      status: 'success',
      data: transformedRows,
      pagination: result.pagination
    });
  } catch (err) {
    console.error('获取成绩列表失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '获取成绩列表失败',
      error: err.message
    });
  }
};

/**
 * 根据课程ID获取成绩列表
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.getGradesByCourseId = async (req, res) => {
  try {
    const courseId = parseInt(req.params.courseId);
    
    // 验证课程ID
    if (isNaN(courseId) || courseId <= 0) {
      return res.status(400).json({
        status: 'fail',
        message: '无效的课程ID'
      });
    }
    
    // 获取该课程的所有成绩
    const grades = await Grade.getByCourseId(courseId);
    
    // 转换字段名称，确保API一致性
    const transformedGrades = transformGradeFields(grades);
    
    res.json({
      status: 'success',
      data: transformedGrades
    });
  } catch (err) {
    console.error('获取课程成绩列表失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '获取课程成绩列表失败',
      error: err.message
    });
  }
};

/**
 * 根据学生ID获取成绩列表
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.getGradesByStudentId = async (req, res) => {
  try {
    const studentId = req.params.studentId;
    
    // 获取该学生的所有成绩
    const grades = await Grade.getByStudentId(studentId);
    
    // 转换字段名称，确保API一致性
    const transformedGrades = transformGradeFields(grades);
    
    res.json({
      status: 'success',
      data: transformedGrades
    });
  } catch (err) {
    console.error('获取学生成绩列表失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '获取学生成绩列表失败',
      error: err.message
    });
  }
};

/**
 * 根据作业ID获取成绩列表
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.getGradesByAssignmentId = async (req, res) => {
  try {
    const assignmentId = parseInt(req.params.assignmentId);
    
    // 验证作业ID
    if (isNaN(assignmentId) || assignmentId <= 0) {
      return res.status(400).json({
        status: 'fail',
        message: '无效的作业ID'
      });
    }
    
    // 获取该作业的所有成绩
    const grades = await Grade.getByAssignmentId(assignmentId);
    
    // 转换字段名称，确保API一致性
    const transformedGrades = transformGradeFields(grades);
    
    res.json({
      status: 'success',
      data: transformedGrades
    });
  } catch (err) {
    console.error('获取作业成绩列表失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '获取作业成绩列表失败',
      error: err.message
    });
  }
};

/**
 * 获取单个成绩详情
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.getGradeById = async (req, res) => {
  try {
    const id = parseInt(req.params.id);
    
    // 获取成绩详情
    const grade = await Grade.getById(id);
    
    if (!grade) {
      return res.status(404).json({
        status: 'fail',
        message: '未找到成绩'
      });
    }
    
    // 转换字段名称，确保API一致性
    const transformedGrade = transformGradeFields(grade);
    
    res.json({
      status: 'success',
      data: transformedGrade
    });
  } catch (err) {
    console.error('获取成绩详情失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '获取成绩详情失败',
      error: err.message
    });
  }
};

/**
 * 获取课程统计信息
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.getCourseStats = async (req, res) => {
  try {
    const courseId = parseInt(req.params.courseId);
    
    // 验证课程ID
    if (isNaN(courseId) || courseId <= 0) {
      return res.status(400).json({
        status: 'fail',
        message: '无效的课程ID'
      });
    }
    
    // 获取课程统计信息
    const stats = await Grade.getCourseStats(courseId);
    
    // 转换字段名称，确保API一致性
    const transformedStats = {
      ...stats,
      assignments: transformGradeFields(stats.assignments)
    };
    
    res.json({
      status: 'success',
      data: transformedStats
    });
  } catch (err) {
    console.error('获取课程统计信息失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '获取课程统计信息失败',
      error: err.message
    });
  }
};

/**
 * 搜索成绩
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.searchGrades = async (req, res) => {
  try {
    const query = req.query.query || '';
    
    // 如果搜索关键词为空，返回空结果
    if (!query.trim()) {
      return res.json({
        status: 'success',
        data: [],
        message: '搜索关键词不能为空'
      });
    }
    
    // 搜索成绩
    const grades = await Grade.search(query);
    
    // 转换字段名称，确保API一致性
    const transformedGrades = transformGradeFields(grades);
    
    res.json({
      status: 'success',
      data: transformedGrades
    });
  } catch (err) {
    console.error('搜索成绩失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '搜索成绩失败',
      error: err.message
    });
  }
};

/**
 * 创建新成绩
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.createGrade = async (req, res) => {
  try {
    // 从请求体获取数据
    const { student_id, course_id, assignment_id, score, feedback, submission_date } = req.body;
    
    // 创建成绩（注意：映射API字段名到数据库字段名）
    const result = await Grade.create({
      student_id,
      course_id,
      assignment_id,
      score,
      comment: feedback,  // 映射feedback到comment
      submission_date
    });
    
    // 获取创建后的成绩详情
    const newGrade = await Grade.getById(result.insertId);
    
    // 转换字段名称，确保API一致性
    const transformedGrade = transformGradeFields(newGrade);
    
    res.status(201).json({
      status: 'success',
      message: '成绩创建成功',
      data: transformedGrade
    });
  } catch (err) {
    console.error('创建成绩失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '创建成绩失败',
      error: err.message
    });
  }
};

/**
 * 更新成绩
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.updateGrade = async (req, res) => {
  try {
    const id = parseInt(req.params.id);
    
    // 检查成绩是否存在
    const existingGrade = await Grade.getById(id);
    if (!existingGrade) {
      return res.status(404).json({
        status: 'fail',
        message: '未找到成绩'
      });
    }
    
    // 从请求体获取更新数据
    const { student_id, course_id, assignment_id, score, feedback, submission_date } = req.body;
    
    // 更新成绩（注意：映射API字段名到数据库字段名）
    const result = await Grade.update(id, {
      student_id,
      course_id,
      assignment_id,
      score,
      comment: feedback,  // 映射feedback到comment
      submission_date
    });
    
    // 获取更新后的成绩
    const updatedGrade = await Grade.getById(id);
    
    // 转换字段名称，确保API一致性
    const transformedGrade = transformGradeFields(updatedGrade);
    
    res.json({
      status: 'success',
      message: '成绩更新成功',
      data: transformedGrade
    });
  } catch (err) {
    console.error('更新成绩失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '更新成绩失败',
      error: err.message
    });
  }
};

/**
 * 删除成绩
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.deleteGrade = async (req, res) => {
  try {
    const id = parseInt(req.params.id);
    
    // 检查成绩是否存在
    const existingGrade = await Grade.getById(id);
    if (!existingGrade) {
      return res.status(404).json({
        status: 'fail',
        message: '未找到成绩'
      });
    }
    
    // 删除成绩
    await Grade.delete(id);
    
    res.json({
      status: 'success',
      message: '成绩删除成功'
    });
  } catch (err) {
    console.error('删除成绩失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '删除成绩失败',
      error: err.message
    });
  }
}; 