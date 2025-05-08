const pool = require('./db.js');

/**
 * 课程表模型
 * 处理与课程表相关的数据库操作
 */
const CourseSchedule = {
  /**
   * 获取所有课程表（支持分页）
   * @param {number} page - 页码（默认1）
   * @param {number} limit - 每页显示数量（默认10）
   * @returns {Promise<Object>} 包含课程表列表和分页信息的对象
   */
  getAll: async (page = 1, limit = 10) => {
    try {
      const offset = (page - 1) * limit;
      
      // 查询课程表列表
      const [rows] = await pool.query(
        `SELECT id, course_name, teacher_name, class_time, classroom, 
        created_at, updated_at
        FROM course_schedules 
        ORDER BY id DESC LIMIT ?, ?`,
        [offset, parseInt(limit)]
      );
      
      // 查询总数，用于分页
      const [countResult] = await pool.query('SELECT COUNT(*) as total FROM course_schedules');
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
   * 搜索课程表
   * @param {string} query - 搜索关键词
   * @returns {Promise<Array>} 匹配的课程表列表
   */
  search: async (query) => {
    try {
      // 使用LIKE进行模糊查询匹配课程名称、教师姓名或教室
      const [rows] = await pool.query(
        `SELECT id, course_name, teacher_name, class_time, classroom, 
        created_at, updated_at
        FROM course_schedules 
        WHERE course_name LIKE ? OR teacher_name LIKE ? OR classroom LIKE ?
        ORDER BY id DESC`,
        [`%${query}%`, `%${query}%`, `%${query}%`]
      );
      
      return rows;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 根据ID获取单个课程表
   * @param {number} id - 课程表ID
   * @returns {Promise<Object|null>} 课程表信息，如果不存在则返回null
   */
  getById: async (id) => {
    try {
      const [rows] = await pool.query(
        `SELECT id, course_name, teacher_name, class_time, classroom, 
        created_at, updated_at
        FROM course_schedules WHERE id = ?`,
        [id]
      );
      return rows.length > 0 ? rows[0] : null;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 创建新课程表
   * @param {Object} data - 课程表数据
   * @returns {Promise<Object>} 创建结果
   */
  create: async (data) => {
    try {
      // 插入新课程表
      const [result] = await pool.query(
        `INSERT INTO course_schedules (course_name, teacher_name, class_time, classroom)
        VALUES (?, ?, ?, ?)`,
        [
          data.course_name,
          data.teacher_name,
          data.class_time,
          data.classroom
        ]
      );
      
      // 返回创建的课程表信息
      const newCourseSchedule = {
        id: result.insertId,
        ...data,
        created_at: new Date(),
        updated_at: new Date()
      };
      
      return newCourseSchedule;
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 更新课程表
   * @param {number} id - 课程表ID
   * @param {Object} data - 更新数据
   * @returns {Promise<Object>} 更新结果
   */
  update: async (id, data) => {
    try {
      // 构建更新语句和参数
      let updateFields = [];
      let updateParams = [];
      
      // 处理可能的更新字段
      if (data.course_name) {
        updateFields.push('course_name = ?');
        updateParams.push(data.course_name);
      }
      
      if (data.teacher_name) {
        updateFields.push('teacher_name = ?');
        updateParams.push(data.teacher_name);
      }
      
      if (data.class_time) {
        updateFields.push('class_time = ?');
        updateParams.push(data.class_time);
      }
      
      if (data.classroom) {
        updateFields.push('classroom = ?');
        updateParams.push(data.classroom);
      }
      
      // 如果没有更新字段，抛出错误
      if (updateFields.length === 0) {
        throw new Error('没有提供要更新的字段');
      }
      
      // 添加ID到参数数组末尾，用于WHERE条件
      updateParams.push(id);
      
      // 执行更新操作
      const [result] = await pool.query(
        `UPDATE course_schedules SET ${updateFields.join(', ')} WHERE id = ?`,
        updateParams
      );
      
      return {
        affectedRows: result.affectedRows,
        changedRows: result.changedRows,
        id
      };
    } catch (error) {
      throw error;
    }
  },
  
  /**
   * 删除课程表
   * @param {number} id - 课程表ID
   * @returns {Promise<Object>} 删除结果
   */
  delete: async (id) => {
    try {
      const [result] = await pool.query('DELETE FROM course_schedules WHERE id = ?', [id]);
      return {
        affectedRows: result.affectedRows,
        id
      };
    } catch (error) {
      throw error;
    }
  }
};

module.exports = CourseSchedule; 