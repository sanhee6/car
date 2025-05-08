/**
 * 用户数据模型
 * 处理与用户相关的数据库操作
 */
const { pool } = require('../config/db');
const bcrypt = require('bcryptjs');

/**
 * 用户模型
 * 包含用户数据的CRUD操作
 */
const User = {
    /**
     * 获取所有用户（支持分页）
     * @param {number} page - 页码（默认1）
     * @param {number} limit - 每页显示数量（默认10）
     * @returns {Promise<Object>} 包含用户列表和分页信息的对象
     */
    getAll: async (page = 1, limit = 10) => {
        try {
            // 计算偏移量
            const offset = (page - 1) * limit;
            
            // 查询用户列表，不返回密码字段
            const [rows] = await pool.query(
                `SELECT id, username, is_admin, created_at, updated_at
                FROM users
                ORDER BY id DESC
                LIMIT ?, ?`,
                [offset, parseInt(limit)]
            );
            
            // 查询总数，用于分页
            const [countResult] = await pool.query('SELECT COUNT(*) as total FROM users');
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
     * 根据ID获取单个用户
     * @param {number} id - 用户ID
     * @returns {Promise<Object|null>} 用户信息，如果不存在则返回null
     */
    getById: async (id) => {
        try {
            // 查询用户，不返回密码字段
            const [rows] = await pool.query(
                `SELECT id, username, is_admin, created_at, updated_at
                FROM users
                WHERE id = ?`,
                [id]
            );
            return rows.length > 0 ? rows[0] : null;
        } catch (error) {
            throw error;
        }
    },
    
    /**
     * 根据用户名获取用户（包含密码，用于验证）
     * @param {string} username - 用户名
     * @returns {Promise<Object|null>} 包含密码的用户信息，如果不存在则返回null
     */
    getByUsername: async (username) => {
        try {
            const [rows] = await pool.query(
                'SELECT * FROM users WHERE username = ?',
                [username]
            );
            return rows.length > 0 ? rows[0] : null;
        } catch (error) {
            throw error;
        }
    },
    
    /**
     * 创建新用户
     * @param {Object} userData - 用户数据（包含username、password、is_admin）
     * @returns {Promise<Object>} 创建结果
     */
    create: async (userData) => {
        try {
            // 对密码进行哈希加密
            const saltRounds = 10;
            const hashedPassword = await bcrypt.hash(userData.password, saltRounds);
            
            // 插入新用户
            const [result] = await pool.query(
                `INSERT INTO users (username, password, is_admin)
                VALUES (?, ?, ?)`,
                [userData.username, hashedPassword, userData.is_admin]
            );
            
            return result;
        } catch (error) {
            throw error;
        }
    },
    
    /**
     * 更新用户
     * @param {number} id - 用户ID
     * @param {Object} userData - 更新数据（可包含username、password、is_admin）
     * @returns {Promise<Object>} 更新结果
     */
    update: async (id, userData) => {
        try {
            // 构建更新字段和参数
            let updateFields = [];
            let updateParams = [];
            
            // 处理用户名
            if (userData.username !== undefined) {
                updateFields.push('username = ?');
                updateParams.push(userData.username);
            }
            
            // 处理密码（如果提供了）
            if (userData.password) {
                const saltRounds = 10;
                const hashedPassword = await bcrypt.hash(userData.password, saltRounds);
                updateFields.push('password = ?');
                updateParams.push(hashedPassword);
            }
            
            // 处理管理员状态
            if (userData.is_admin !== undefined) {
                updateFields.push('is_admin = ?');
                updateParams.push(userData.is_admin);
            }
            
            // 如果没有更新字段，返回空结果
            if (updateFields.length === 0) {
                return { affectedRows: 0, changedRows: 0 };
            }
            
            // 添加ID到参数数组末尾
            updateParams.push(id);
            
            // 执行更新
            const [result] = await pool.query(
                `UPDATE users SET ${updateFields.join(', ')} WHERE id = ?`,
                updateParams
            );
            
            return result;
        } catch (error) {
            throw error;
        }
    },
    
    /**
     * 删除用户
     * @param {number} id - 用户ID
     * @returns {Promise<Object>} 删除结果
     */
    delete: async (id) => {
        try {
            const [result] = await pool.query(
                'DELETE FROM users WHERE id = ?',
                [id]
            );
            return result;
        } catch (error) {
            throw error;
        }
    },
    
    /**
     * 验证用户密码
     * @param {string} plainPassword - 明文密码
     * @param {string} hashedPassword - 哈希密码
     * @returns {Promise<boolean>} 验证结果
     */
    validatePassword: async (plainPassword, hashedPassword) => {
        return await bcrypt.compare(plainPassword, hashedPassword);
    }
};

module.exports = User; 