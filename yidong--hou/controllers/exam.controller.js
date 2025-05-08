const Exam = require('../models/exam.model.js');

/**
 * 考试控制器
 * 处理考试相关的业务逻辑
 */

/**
 * 获取考试列表（支持分页）
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.getExams = async (req, res) => {
  try {
    // 解析分页参数
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;
    
    // 获取考试列表
    const result = await Exam.getAll(page, limit);
    
    res.json({
      status: 'success',
      data: result.rows,
      pagination: result.pagination
    });
  } catch (err) {
    console.error('获取考试列表失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '获取考试列表失败',
      error: err.message
    });
  }
};

/**
 * 根据课程ID获取考试列表
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.getExamsByCourseId = async (req, res) => {
  try {
    const courseId = parseInt(req.params.courseId);
    
    // 验证课程ID
    if (isNaN(courseId) || courseId <= 0) {
      return res.status(400).json({
        status: 'fail',
        message: '无效的课程ID'
      });
    }
    
    // 获取该课程的所有考试
    const exams = await Exam.getByCourseId(courseId);
    
    res.json({
      status: 'success',
      data: exams
    });
  } catch (err) {
    console.error('获取课程考试列表失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '获取课程考试列表失败',
      error: err.message
    });
  }
};

/**
 * 获取单个考试详情
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.getExamById = async (req, res) => {
  try {
    const id = parseInt(req.params.id);
    
    // 获取考试详情
    const exam = await Exam.getById(id);
    
    if (!exam) {
      return res.status(404).json({
        status: 'fail',
        message: '未找到考试'
      });
    }
    
    res.json({
      status: 'success',
      data: exam
    });
  } catch (err) {
    console.error('获取考试详情失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '获取考试详情失败',
      error: err.message
    });
  }
};

/**
 * 搜索考试
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.searchExams = async (req, res) => {
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
    
    // 搜索考试
    const exams = await Exam.search(query);
    
    res.json({
      status: 'success',
      data: exams
    });
  } catch (err) {
    console.error('搜索考试失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '搜索考试失败',
      error: err.message
    });
  }
};

/**
 * 创建新考试
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.createExam = async (req, res) => {
  try {
    // 从请求体获取数据
    const { course_id, title, exam_date, duration, location, description } = req.body;
    
    // 创建考试
    const result = await Exam.create({
      course_id,
      title,
      exam_date,
      duration,
      location,
      description
    });
    
    // 获取创建后的考试详情
    const newExam = await Exam.getById(result.insertId);
    
    res.status(201).json({
      status: 'success',
      message: '考试创建成功',
      data: newExam
    });
  } catch (err) {
    console.error('创建考试失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '创建考试失败',
      error: err.message
    });
  }
};

/**
 * 更新考试
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.updateExam = async (req, res) => {
  try {
    const id = parseInt(req.params.id);
    
    // 检查考试是否存在
    const existingExam = await Exam.getById(id);
    if (!existingExam) {
      return res.status(404).json({
        status: 'fail',
        message: '未找到考试'
      });
    }
    
    // 从请求体获取更新数据
    const { course_id, title, exam_date, duration, location, description } = req.body;
    
    // 更新考试
    const result = await Exam.update(id, {
      course_id,
      title,
      exam_date,
      duration,
      location,
      description
    });
    
    // 获取更新后的考试
    const updatedExam = await Exam.getById(id);
    
    res.json({
      status: 'success',
      message: '考试更新成功',
      data: updatedExam
    });
  } catch (err) {
    console.error('更新考试失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '更新考试失败',
      error: err.message
    });
  }
};

/**
 * 删除考试
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.deleteExam = async (req, res) => {
  try {
    const id = parseInt(req.params.id);
    
    // 检查考试是否存在
    const existingExam = await Exam.getById(id);
    if (!existingExam) {
      return res.status(404).json({
        status: 'fail',
        message: '未找到考试'
      });
    }
    
    // 删除考试
    await Exam.delete(id);
    
    res.json({
      status: 'success',
      message: '考试删除成功'
    });
  } catch (err) {
    console.error('删除考试失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '删除考试失败',
      error: err.message
    });
  }
}; 