const pool = require('./db.js');

/**
 * 作业数据模型
 * 处理与作业相关的数据库操作
 */
const Assignment = {
  /**
   * 获取所有作业（支持分页）
   * @param {number} page - 页码（默认1）
   * @param {number} limit - 每页显示数量（默认10）
   * @returns {Promise<Object>} 包含作业列表和分页信息的对象
   */
  getAll: async (page = 1, limit = 10) => {
    try {
      const offset = (page - 1) * limit;
      
      // 查询作业列表，同时获取关联的课程名称
      const [rows] = await pool.query(
        `SELECT a.id, a.course_id, c.name AS course_name, a.title, 
        a.description, a.deadline, a.total_score AS max_score, a.created_at, a.updated_at
        FROM assignments a
        LEFT JOIN courses c ON a.course_id = c.id
        ORDER BY a.deadline ASC 
        LIMIT ?, ?`,
        [offset, parseInt(limit)]
      );
      
      // 查询总数，用于分页
      const [countResult] = await pool.query('SELECT COUNT(*) as total FROM assignments');
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
   * 根据课程ID获取作业列表
   * @param {number} courseId - 课程ID
   * @returns {Promise<Array>} 作业列表
   */
  getByCourseId: async (courseId) => {
    try {
      const [rows] = await pool.query(
        `SELECT id, course_id, title, description, deadline, total_score AS max_score, 
        created_at, updated_at
        FROM assignments 
        WHERE course_id = ?
        ORDER BY deadline ASC`,
        [courseId]
      );
      return rows;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 根据ID获取单个作业
   * @param {number} id - 作业ID
   * @returns {Promise<Object|null>} 作业信息，如果不存在则返回null
   */
  getById: async (id) => {
    try {
      const [rows] = await pool.query(
        `SELECT a.id, a.course_id, c.name AS course_name, a.title, 
        a.description, a.deadline, a.total_score AS max_score, a.created_at, a.updated_at
        FROM assignments a
        LEFT JOIN courses c ON a.course_id = c.id
        WHERE a.id = ?`,
        [id]
      );
      return rows.length > 0 ? rows[0] : null;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 创建新作业
   * @param {Object} assignmentData - 作业数据
   * @returns {Promise<Object>} 创建结果
   */
  create: async (assignmentData) => {
    try {
      const [result] = await pool.query(
        `INSERT INTO assignments (course_id, title, description, deadline, total_score) 
        VALUES (?, ?, ?, ?, ?)`,
        [
          assignmentData.course_id,
          assignmentData.title,
          assignmentData.description || null,
          assignmentData.due_date,
          assignmentData.max_score
        ]
      );
      return result;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 更新作业
   * @param {number} id - 作业ID
   * @param {Object} assignmentData - 更新数据
   * @returns {Promise<Object>} 更新结果
   */
  update: async (id, assignmentData) => {
    try {
      // 构建更新字段和参数
      let updateFields = [];
      let updateParams = [];
      
      // 处理可能的更新字段
      if (assignmentData.course_id !== undefined) {
        updateFields.push('course_id = ?');
        updateParams.push(assignmentData.course_id);
      }
      
      if (assignmentData.title !== undefined) {
        updateFields.push('title = ?');
        updateParams.push(assignmentData.title);
      }
      
      if (assignmentData.description !== undefined) {
        updateFields.push('description = ?');
        updateParams.push(assignmentData.description);
      }
      
      if (assignmentData.due_date !== undefined) {
        updateFields.push('deadline = ?');
        updateParams.push(assignmentData.due_date);
      }
      
      if (assignmentData.max_score !== undefined) {
        updateFields.push('total_score = ?');
        updateParams.push(assignmentData.max_score);
      }
      
      // 如果没有更新字段，返回成功但不执行更新
      if (updateFields.length === 0) {
        return { affectedRows: 0, changedRows: 0 };
      }
      
      // 添加ID到参数数组末尾，用于WHERE条件
      updateParams.push(id);
      
      // 执行更新
      const [result] = await pool.query(
        `UPDATE assignments SET ${updateFields.join(', ')} WHERE id = ?`,
        updateParams
      );
      
      return result;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 删除作业
   * @param {number} id - 作业ID
   * @returns {Promise<Object>} 删除结果
   */
  delete: async (id) => {
    try {
      const [result] = await pool.query('DELETE FROM assignments WHERE id = ?', [id]);
      return result;
    } catch (error) {
      throw error;
    }
  },

  /**
   * 搜索作业
   * @param {string} query - 搜索关键词
   * @returns {Promise<Array>} 匹配的作业列表
   */
  search: async (query) => {
    try {
      const [rows] = await pool.query(
        `SELECT a.id, a.course_id, c.name AS course_name, a.title, 
        a.description, a.deadline, a.total_score AS max_score, a.created_at, a.updated_at
        FROM assignments a
        LEFT JOIN courses c ON a.course_id = c.id
        WHERE a.title LIKE ? OR c.name LIKE ? OR a.description LIKE ?
        ORDER BY a.deadline ASC`,
        [`%${query}%`, `%${query}%`, `%${query}%`]
      );
      return rows;
    } catch (error) {
      throw error;
    }
  }
};

module.exports = Assignment;