const pool = require('./db.js');

/**
 * 成绩数据模型
 * 处理与成绩相关的数据库操作
 */
const Grade = {
  /**
   * 获取所有成绩（支持分页）
   * @param {number} page - 页码（默认1）
   * @param {number} limit - 每页显示数量（默认10）
   * @returns {Promise<Object>} 包含成绩列表和分页信息的对象
   */
  getAll: async (page = 1, limit = 10) => {
    try {
      const offset = (page - 1) * limit;
      
      // 查询成绩列表，同时获取关联的课程、学生和作业信息
      const [rows] = await pool.query(
        `SELECT g.id, g.student_id, g.course_id, g.assignment_id, 
        c.name AS course_name, a.title AS assignment_title,
        g.score, COALESCE(g.created_at, NOW()) AS submission_date, g.created_at, g.updated_at
        FROM grades g
        LEFT JOIN courses c ON g.course_id = c.id
        LEFT JOIN assignments a ON g.assignment_id = a.id
        ORDER BY g.updated_at DESC 
        LIMIT ?, ?`,
        [offset, parseInt(limit)]
      );
      
      // 查询总数，用于分页
      const [countResult] = await pool.query('SELECT COUNT(*) as total FROM grades');
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
   * 根据课程ID获取成绩列表
   * @param {number} courseId - 课程ID
   * @returns {Promise<Array>} 成绩列表
   */
  getByCourseId: async (courseId) => {
    try {
      const [rows] = await pool.query(
        `SELECT g.id, g.student_id, g.course_id, g.assignment_id, 
        a.title AS assignment_title, g.score, 
        COALESCE(g.created_at, NOW()) AS submission_date, g.created_at, g.updated_at
        FROM grades g
        LEFT JOIN assignments a ON g.assignment_id = a.id
        WHERE g.course_id = ?
        ORDER BY g.updated_at DESC`,
        [courseId]
      );
      return rows;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 根据学生ID获取成绩列表
   * @param {number} studentId - 学生ID
   * @returns {Promise<Array>} 成绩列表
   */
  getByStudentId: async (studentId) => {
    try {
      const [rows] = await pool.query(
        `SELECT g.id, g.student_id, g.course_id, g.assignment_id, 
        c.name AS course_name, a.title AS assignment_title,
        g.score, COALESCE(g.created_at, NOW()) AS submission_date, 
        g.created_at, g.updated_at
        FROM grades g
        LEFT JOIN courses c ON g.course_id = c.id
        LEFT JOIN assignments a ON g.assignment_id = a.id
        WHERE g.student_id = ?
        ORDER BY g.updated_at DESC`,
        [studentId]
      );
      return rows;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 根据作业ID获取成绩列表
   * @param {number} assignmentId - 作业ID
   * @returns {Promise<Array>} 成绩列表
   */
  getByAssignmentId: async (assignmentId) => {
    try {
      const [rows] = await pool.query(
        `SELECT g.id, g.student_id, g.course_id, g.assignment_id, 
        g.score, COALESCE(g.created_at, NOW()) AS submission_date, 
        g.created_at, g.updated_at
        FROM grades g
        WHERE g.assignment_id = ?
        ORDER BY g.score DESC`,
        [assignmentId]
      );
      return rows;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 根据ID获取单个成绩
   * @param {number} id - 成绩ID
   * @returns {Promise<Object|null>} 成绩信息，如果不存在则返回null
   */
  getById: async (id) => {
    try {
      const [rows] = await pool.query(
        `SELECT g.id, g.student_id, g.course_id, g.assignment_id, 
        c.name AS course_name, a.title AS assignment_title,
        g.score, COALESCE(g.created_at, NOW()) AS submission_date, 
        g.created_at, g.updated_at
        FROM grades g
        LEFT JOIN courses c ON g.course_id = c.id
        LEFT JOIN assignments a ON g.assignment_id = a.id
        WHERE g.id = ?`,
        [id]
      );
      return rows.length > 0 ? rows[0] : null;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 创建新成绩
   * @param {Object} gradeData - 成绩数据
   * @returns {Promise<Object>} 创建结果
   */
  create: async (gradeData) => {
    try {
      const [result] = await pool.query(
        `INSERT INTO grades (student_id, course_id, assignment_id, score, comment, grade_type) 
        VALUES (?, ?, ?, ?, ?, ?)`,
        [
          gradeData.student_id,
          gradeData.course_id,
          gradeData.assignment_id,
          gradeData.score,
          gradeData.feedback || null,
          gradeData.grade_type || 'assignment'
        ]
      );
      return result;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 更新成绩
   * @param {number} id - 成绩ID
   * @param {Object} gradeData - 更新数据
   * @returns {Promise<Object>} 更新结果
   */
  update: async (id, gradeData) => {
    try {
      // 构建更新字段和参数
      let updateFields = [];
      let updateParams = [];
      
      // 处理可能的更新字段
      if (gradeData.student_id !== undefined) {
        updateFields.push('student_id = ?');
        updateParams.push(gradeData.student_id);
      }
      
      if (gradeData.course_id !== undefined) {
        updateFields.push('course_id = ?');
        updateParams.push(gradeData.course_id);
      }
      
      if (gradeData.assignment_id !== undefined) {
        updateFields.push('assignment_id = ?');
        updateParams.push(gradeData.assignment_id);
      }
      
      if (gradeData.score !== undefined) {
        updateFields.push('score = ?');
        updateParams.push(gradeData.score);
      }
      
      if (gradeData.feedback !== undefined) {
        updateFields.push('comment = ?');
        updateParams.push(gradeData.feedback);
      }
      
      if (gradeData.grade_type !== undefined) {
        updateFields.push('grade_type = ?');
        updateParams.push(gradeData.grade_type);
      }
      
      // 如果没有更新字段，返回成功但不执行更新
      if (updateFields.length === 0) {
        return { affectedRows: 0, changedRows: 0 };
      }
      
      // 添加ID到参数数组末尾，用于WHERE条件
      updateParams.push(id);
      
      // 执行更新
      const [result] = await pool.query(
        `UPDATE grades SET ${updateFields.join(', ')} WHERE id = ?`,
        updateParams
      );
      
      return result;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 删除成绩
   * @param {number} id - 成绩ID
   * @returns {Promise<Object>} 删除结果
   */
  delete: async (id) => {
    try {
      const [result] = await pool.query('DELETE FROM grades WHERE id = ?', [id]);
      return result;
    } catch (error) {
      throw error;
    }
  },

  /**
   * 搜索成绩
   * @param {string} query - 搜索关键词
   * @returns {Promise<Array>} 匹配的成绩列表
   */
  search: async (query) => {
    try {
      const [rows] = await pool.query(
        `SELECT g.id, g.student_id, g.course_id, g.assignment_id, 
        c.name AS course_name, a.title AS assignment_title,
        g.score, g.comment AS feedback, COALESCE(g.created_at, NOW()) AS submission_date, 
        g.created_at, g.updated_at
        FROM grades g
        LEFT JOIN courses c ON g.course_id = c.id
        LEFT JOIN assignments a ON g.assignment_id = a.id
        WHERE c.name LIKE ? OR a.title LIKE ? OR g.comment LIKE ?
        ORDER BY g.updated_at DESC`,
        [`%${query}%`, `%${query}%`, `%${query}%`]
      );
      return rows;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 获取课程统计信息
   * @param {number} courseId - 课程ID
   * @returns {Promise<Object>} 课程统计信息
   */
  getCourseStats: async (courseId) => {
    try {
      // 计算课程平均分
      const [avgResult] = await pool.query(
        `SELECT AVG(score) as average_score, MAX(score) as max_score, 
        MIN(score) as min_score, COUNT(*) as total_grades
        FROM grades
        WHERE course_id = ?`,
        [courseId]
      );
      
      // 获取课程作业列表及平均分
      const [assignmentsResult] = await pool.query(
        `SELECT a.id, a.title, AVG(g.score) as average_score, COUNT(g.id) as submissions
        FROM assignments a
        LEFT JOIN grades g ON a.id = g.assignment_id
        WHERE a.course_id = ?
        GROUP BY a.id
        ORDER BY a.deadline ASC`,
        [courseId]
      );
      
      return {
        stats: avgResult[0],
        assignments: assignmentsResult
      };
    } catch (error) {
      throw error;
    }
  }
};

module.exports = Grade; 