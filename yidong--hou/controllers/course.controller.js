const Course = require('../models/course.model.js');

/**
 * 课程控制器
 * 处理课程相关的业务逻辑
 */

/**
 * 获取课程列表（支持分页）
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.getCourses = async (req, res) => {
  try {
    // 解析分页参数
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;
    
    // 获取课程列表
    const result = await Course.getAll(page, limit);
    
    res.json({
      status: 'success',
      data: result.rows,
      pagination: result.pagination
    });
  } catch (err) {
    console.error('获取课程列表失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '获取课程列表失败',
      error: err.message
    });
  }
};

/**
 * 根据ID获取单个课程
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.getCourseById = async (req, res) => {
  try {
    const id = parseInt(req.params.id);
    
    // 获取课程
    const course = await Course.getById(id);
    
    if (!course) {
      return res.status(404).json({
        status: 'fail',
        message: '未找到课程'
      });
    }
    
    res.json({
      status: 'success',
      data: course
    });
  } catch (err) {
    console.error('获取课程失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '获取课程失败',
      error: err.message
    });
  }
};

/**
 * 创建新课程
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.createCourse = async (req, res) => {
  try {
    // 从请求体获取数据
    const { 
      name, 
      teacher, 
      classroom, 
      weekday, 
      start_section, 
      end_section, 
      start_week, 
      end_week, 
      semester_id 
    } = req.body;
    
    // 创建课程
    const result = await Course.create({
      name,
      teacher,
      classroom,
      weekday,
      start_section,
      end_section,
      start_week,
      end_week,
      semester_id
    });
    
    // 获取创建的课程
    const newCourse = await Course.getById(result.insertId);
    
    res.status(201).json({
      status: 'success',
      message: '课程创建成功',
      data: newCourse
    });
  } catch (err) {
    console.error('创建课程失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '创建课程失败',
      error: err.message
    });
  }
};

/**
 * 更新课程
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.updateCourse = async (req, res) => {
  try {
    const id = parseInt(req.params.id);
    
    // 检查课程是否存在
    const existingCourse = await Course.getById(id);
    if (!existingCourse) {
      return res.status(404).json({
        status: 'fail',
        message: '未找到课程'
      });
    }
    
    // 从请求体获取更新数据
    const { 
      name, 
      teacher, 
      classroom, 
      weekday, 
      start_section, 
      end_section, 
      start_week, 
      end_week, 
      semester_id 
    } = req.body;
    
    // 更新课程
    await Course.update(id, {
      name,
      teacher,
      classroom,
      weekday,
      start_section,
      end_section,
      start_week,
      end_week,
      semester_id
    });
    
    // 获取更新后的课程
    const updatedCourse = await Course.getById(id);
    
    res.json({
      status: 'success',
      message: '课程更新成功',
      data: updatedCourse
    });
  } catch (err) {
    console.error('更新课程失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '更新课程失败',
      error: err.message
    });
  }
};

/**
 * 删除课程
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.deleteCourse = async (req, res) => {
  try {
    const id = parseInt(req.params.id);
    
    // 检查课程是否存在
    const existingCourse = await Course.getById(id);
    if (!existingCourse) {
      return res.status(404).json({
        status: 'fail',
        message: '未找到课程'
      });
    }
    
    // 删除课程
    await Course.delete(id);
    
    res.json({
      status: 'success',
      message: '课程删除成功'
    });
  } catch (err) {
    console.error('删除课程失败:', err);
    res.status(500).json({
      status: 'fail',
      message: '删除课程失败',
      error: err.message
    });
  }
}; 