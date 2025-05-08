const CourseSchedule = require('../models/course_schedule.model.js');

/**
 * 课程表控制器
 * 处理课程表相关的业务逻辑
 */

/**
 * 获取课程表列表（支持分页）
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.getCourseSchedules = async (req, res) => {
  try {
    // 解析分页参数
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;
    
    // 获取课程表列表
    const result = await CourseSchedule.getAll(page, limit);
    
    res.json({
      status: 'success',
      data: result.rows,
      pagination: result.pagination
    });
  } catch (err) {
    console.error('获取课程表列表失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '获取课程表列表失败',
      error: err.message
    });
  }
};

/**
 * 搜索课程表
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.searchCourseSchedules = async (req, res) => {
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
    
    // 搜索课程表
    const courses = await CourseSchedule.search(query);
    
    res.json({
      status: 'success',
      data: courses
    });
  } catch (err) {
    console.error('搜索课程表失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '搜索课程表失败',
      error: err.message
    });
  }
};

/**
 * 创建新课程表
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.createCourseSchedule = async (req, res) => {
  try {
    // 从请求体获取数据
    const { course_name, teacher_name, class_time, classroom } = req.body;
    
    // 创建课程表
    const result = await CourseSchedule.create({
      course_name,
      teacher_name,
      class_time,
      classroom
    });
    
    res.status(201).json({
      status: 'success',
      message: '课程表创建成功',
      data: result
    });
  } catch (err) {
    console.error('创建课程表失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '创建课程表失败',
      error: err.message
    });
  }
};

/**
 * 更新课程表
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.updateCourseSchedule = async (req, res) => {
  try {
    const id = parseInt(req.params.id);
    
    // 检查课程表是否存在
    const existingCourseSchedule = await CourseSchedule.getById(id);
    if (!existingCourseSchedule) {
      return res.status(404).json({
        status: 'fail',
        message: '未找到课程表'
      });
    }
    
    // 从请求体获取更新数据
    const { course_name, teacher_name, class_time, classroom } = req.body;
    
    // 更新课程表
    const result = await CourseSchedule.update(id, {
      course_name,
      teacher_name,
      class_time,
      classroom
    });
    
    // 获取更新后的课程表
    const updatedCourseSchedule = await CourseSchedule.getById(id);
    
    res.json({
      status: 'success',
      message: '课程表更新成功',
      data: updatedCourseSchedule
    });
  } catch (err) {
    console.error('更新课程表失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '更新课程表失败',
      error: err.message
    });
  }
};

/**
 * 删除课程表
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.deleteCourseSchedule = async (req, res) => {
  try {
    const id = parseInt(req.params.id);
    
    // 检查课程表是否存在
    const existingCourseSchedule = await CourseSchedule.getById(id);
    if (!existingCourseSchedule) {
      return res.status(404).json({
        status: 'fail',
        message: '未找到课程表'
      });
    }
    
    // 删除课程表
    await CourseSchedule.delete(id);
    
    res.json({
      status: 'success',
      message: '课程表删除成功'
    });
  } catch (err) {
    console.error('删除课程表失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '删除课程表失败',
      error: err.message
    });
  }
}; 