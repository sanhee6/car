const pool = require('./db.js');

/**
 * 考试数据模型
 * 处理与考试相关的数据库操作
 */
const Exam = {
  /**
   * 获取所有考试（支持分页）
   * @param {number} page - 页码（默认1）
   * @param {number} limit - 每页显示数量（默认10）
   * @returns {Promise<Object>} 包含考试列表和分页信息的对象
   */
  getAll: async (page = 1, limit = 10) => {
    try {
      const offset = (page - 1) * limit;
      
      // 查询考试列表，同时获取关联的课程名称
      const [rows] = await pool.query(
        `SELECT e.id, e.course_id, c.name AS course_name, e.title, 
        e.exam_date, e.duration, e.location, e.description, 
        e.created_at, e.updated_at
        FROM exams e
        LEFT JOIN courses c ON e.course_id = c.id
        ORDER BY e.exam_date DESC 
        LIMIT ?, ?`,
        [offset, parseInt(limit)]
      );
      
      // 查询总数，用于分页
      const [countResult] = await pool.query('SELECT COUNT(*) as total FROM exams');
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
   * 根据课程ID获取考试列表
   * @param {number} courseId - 课程ID
   * @returns {Promise<Array>} 考试列表
   */
  getByCourseId: async (courseId) => {
    try {
      const [rows] = await pool.query(
        `SELECT id, course_id, title, exam_date, duration, location, 
        description, created_at, updated_at
        FROM exams 
        WHERE course_id = ?
        ORDER BY exam_date DESC`,
        [courseId]
      );
      return rows;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 根据ID获取单个考试
   * @param {number} id - 考试ID
   * @returns {Promise<Object|null>} 考试信息，如果不存在则返回null
   */
  getById: async (id) => {
    try {
      const [rows] = await pool.query(
        `SELECT e.id, e.course_id, c.name AS course_name, e.title, 
        e.exam_date, e.duration, e.location, e.description, 
        e.created_at, e.updated_at
        FROM exams e
        LEFT JOIN courses c ON e.course_id = c.id
        WHERE e.id = ?`,
        [id]
      );
      return rows.length > 0 ? rows[0] : null;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 创建新考试
   * @param {Object} examData - 考试数据
   * @returns {Promise<Object>} 创建结果
   */
  create: async (examData) => {
    try {
      const [result] = await pool.query(
        `INSERT INTO exams (course_id, title, exam_date, duration, location, description) 
        VALUES (?, ?, ?, ?, ?, ?)`,
        [
          examData.course_id,
          examData.title,
          examData.exam_date,
          examData.duration,
          examData.location,
          examData.description || null
        ]
      );
      return result;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 更新考试
   * @param {number} id - 考试ID
   * @param {Object} examData - 更新数据
   * @returns {Promise<Object>} 更新结果
   */
  update: async (id, examData) => {
    try {
      // 构建更新字段和参数
      let updateFields = [];
      let updateParams = [];
      
      // 处理可能的更新字段
      if (examData.course_id !== undefined) {
        updateFields.push('course_id = ?');
        updateParams.push(examData.course_id);
      }
      
      if (examData.title !== undefined) {
        updateFields.push('title = ?');
        updateParams.push(examData.title);
      }
      
      if (examData.exam_date !== undefined) {
        updateFields.push('exam_date = ?');
        updateParams.push(examData.exam_date);
      }
      
      if (examData.duration !== undefined) {
        updateFields.push('duration = ?');
        updateParams.push(examData.duration);
      }
      
      if (examData.location !== undefined) {
        updateFields.push('location = ?');
        updateParams.push(examData.location);
      }
      
      if (examData.description !== undefined) {
        updateFields.push('description = ?');
        updateParams.push(examData.description);
      }
      
      // 如果没有更新字段，返回成功但不执行更新
      if (updateFields.length === 0) {
        return { affectedRows: 0, changedRows: 0 };
      }
      
      // 添加ID到参数数组末尾，用于WHERE条件
      updateParams.push(id);
      
      // 执行更新
      const [result] = await pool.query(
        `UPDATE exams SET ${updateFields.join(', ')} WHERE id = ?`,
        updateParams
      );
      
      return result;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 删除考试
   * @param {number} id - 考试ID
   * @returns {Promise<Object>} 删除结果
   */
  delete: async (id) => {
    try {
      const [result] = await pool.query('DELETE FROM exams WHERE id = ?', [id]);
      return result;
    } catch (error) {
      throw error;
    }
  },

  /**
   * 搜索考试
   * @param {string} query - 搜索关键词
   * @returns {Promise<Array>} 匹配的考试列表
   */
  search: async (query) => {
    try {
      const [rows] = await pool.query(
        `SELECT e.id, e.course_id, c.name AS course_name, e.title, 
        e.exam_date, e.duration, e.location, e.description, 
        e.created_at, e.updated_at
        FROM exams e
        LEFT JOIN courses c ON e.course_id = c.id
        WHERE e.title LIKE ? OR c.name LIKE ? OR e.location LIKE ? OR e.description LIKE ?
        ORDER BY e.exam_date DESC`,
        [`%${query}%`, `%${query}%`, `%${query}%`, `%${query}%`]
      );
      return rows;
    } catch (error) {
      throw error;
    }
  }
};

module.exports = Exam; 