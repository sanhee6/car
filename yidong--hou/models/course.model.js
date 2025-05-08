const pool = require('./db.js');

/**
 * 课程数据模型
 * 处理与课程相关的数据库操作
 */
const Course = {
  /**
   * 获取所有课程（支持分页）
   * @param {number} page - 页码（默认1）
   * @param {number} limit - 每页显示数量（默认10）
   * @returns {Promise<Object>} 包含课程列表和分页信息的对象
   */
  getAll: async (page = 1, limit = 10) => {
    try {
      const offset = (page - 1) * limit;
      
      // 查询课程列表
      const [rows] = await pool.query(
        `SELECT id, name, teacher, classroom, weekday, 
        start_section, end_section, start_week, end_week, 
        semester_id, created_at, updated_at
        FROM courses 
        ORDER BY id DESC LIMIT ?, ?`,
        [offset, parseInt(limit)]
      );
      
      // 查询总数，用于分页
      const [countResult] = await pool.query('SELECT COUNT(*) as total FROM courses');
      const total = countResult[0].total;
      
      return {
        rows,
        pagination: {
          total,
          page,
          limit,
          totalPages: Math.ceil(total / limit)
        }
      };
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 根据ID获取单个课程
   * @param {number} id - 课程ID
   * @returns {Promise<Object|null>} 课程信息，如果不存在则返回null
   */
  getById: async (id) => {
    try {
      const [rows] = await pool.query(
        `SELECT id, name, teacher, classroom, weekday, 
        start_section, end_section, start_week, end_week, 
        semester_id, created_at, updated_at
        FROM courses WHERE id = ?`,
        [id]
      );
      return rows.length > 0 ? rows[0] : null;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 获取指定周次的课程
   * @param {number} week - 周次
   * @param {string} semesterId - 学期标识
   * @returns {Promise<Array>} 课程列表
   */
  getByWeek: async (week, semesterId) => {
    try {
      const [rows] = await pool.query(
        `SELECT id, name, teacher, classroom, weekday, 
        start_section, end_section, start_week, end_week, 
        semester_id, created_at, updated_at
        FROM courses 
        WHERE start_week <= ? AND end_week >= ? AND semester_id = ?`,
        [week, week, semesterId]
      );
      return rows;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 创建新课程
   * @param {Object} courseData - 课程数据
   * @returns {Promise<Object>} 创建结果
   */
  create: async (courseData) => {
    try {
      const [result] = await pool.query(
        `INSERT INTO courses (name, teacher, classroom, weekday, 
        start_section, end_section, start_week, end_week, semester_id) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`,
        [
          courseData.name,
          courseData.teacher,
          courseData.classroom,
          courseData.weekday,
          courseData.start_section,
          courseData.end_section,
          courseData.start_week,
          courseData.end_week,
          courseData.semester_id
        ]
      );
      return result;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 更新课程
   * @param {number} id - 课程ID
   * @param {Object} courseData - 更新数据
   * @returns {Promise<Object>} 更新结果
   */
  update: async (id, courseData) => {
    try {
      // 构建更新字段和参数
      let updateFields = [];
      let updateParams = [];
      
      // 处理可能的更新字段
      if (courseData.name) {
        updateFields.push('name = ?');
        updateParams.push(courseData.name);
      }
      
      if (courseData.teacher) {
        updateFields.push('teacher = ?');
        updateParams.push(courseData.teacher);
      }
      
      if (courseData.classroom) {
        updateFields.push('classroom = ?');
        updateParams.push(courseData.classroom);
      }
      
      if (courseData.weekday !== undefined) {
        updateFields.push('weekday = ?');
        updateParams.push(courseData.weekday);
      }
      
      if (courseData.start_section !== undefined) {
        updateFields.push('start_section = ?');
        updateParams.push(courseData.start_section);
      }
      
      if (courseData.end_section !== undefined) {
        updateFields.push('end_section = ?');
        updateParams.push(courseData.end_section);
      }
      
      if (courseData.start_week !== undefined) {
        updateFields.push('start_week = ?');
        updateParams.push(courseData.start_week);
      }
      
      if (courseData.end_week !== undefined) {
        updateFields.push('end_week = ?');
        updateParams.push(courseData.end_week);
      }
      
      if (courseData.semester_id) {
        updateFields.push('semester_id = ?');
        updateParams.push(courseData.semester_id);
      }
      
      // 如果没有更新字段，返回成功但不执行更新
      if (updateFields.length === 0) {
        return { affectedRows: 0, changedRows: 0 };
      }
      
      // 添加ID到参数数组末尾，用于WHERE条件
      updateParams.push(id);
      
      // 执行更新
      const [result] = await pool.query(
        `UPDATE courses SET ${updateFields.join(', ')} WHERE id = ?`,
        updateParams
      );
      
      return result;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 删除课程
   * @param {number} id - 课程ID
   * @returns {Promise<Object>} 删除结果
   */
  delete: async (id) => {
    try {
      const [result] = await pool.query('DELETE FROM courses WHERE id = ?', [id]);
      return result;
    } catch (error) {
      throw error;
    }
  }
};

module.exports = Course; 