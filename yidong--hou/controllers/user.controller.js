/**
 * 用户控制器
 * 处理用户相关的业务逻辑
 */
const User = require('../models/user.model');

/**
 * 获取用户列表
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.getUsers = async (req, res) => {
    try {
        // 解析分页参数
        const page = parseInt(req.query.page) || 1;
        const limit = parseInt(req.query.limit) || 10;
        
        // 获取用户列表
        const result = await User.getAll(page, limit);
        
        res.json({
            status: 'success',
            data: result.rows,
            pagination: result.pagination
        });
    } catch (err) {
        console.error('获取用户列表失败:', err);
        res.status(500).json({
            status: 'fail',
            message: '获取用户列表失败',
            error: err.message
        });
    }
};

/**
 * 获取单个用户
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.getUserById = async (req, res) => {
    try {
        const id = parseInt(req.params.id);
        
        // 获取用户
        const user = await User.getById(id);
        
        if (!user) {
            return res.status(404).json({
                status: 'fail',
                message: '用户不存在'
            });
        }
        
        res.json({
            status: 'success',
            data: user
        });
    } catch (err) {
        console.error('获取用户失败:', err);
        res.status(500).json({
            status: 'fail',
            message: '获取用户失败',
            error: err.message
        });
    }
};

/**
 * 创建用户
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.createUser = async (req, res) => {
    try {
        const { username, password, is_admin } = req.body;
        
        // 验证必填字段
        if (!username || !password || is_admin === undefined) {
            return res.status(400).json({
                status: 'fail',
                message: '用户名、密码和管理员状态不能为空'
            });
        }
        
        // 检查用户名是否已存在
        const existingUser = await User.getByUsername(username);
        if (existingUser) {
            return res.status(400).json({
                status: 'fail',
                message: '用户名已存在'
            });
        }
        
        // 创建用户
        const result = await User.create({
            username,
            password,
            is_admin: is_admin === true || is_admin === 'true'
        });
        
        // 获取创建的用户（不含密码）
        const newUser = await User.getById(result.insertId);
        
        res.status(201).json({
            status: 'success',
            message: '用户创建成功',
            data: newUser
        });
    } catch (err) {
        console.error('创建用户失败:', err);
        res.status(500).json({
            status: 'fail',
            message: '创建用户失败',
            error: err.message
        });
    }
};

/**
 * 更新用户
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.updateUser = async (req, res) => {
    try {
        const id = parseInt(req.params.id);
        const { username, password, is_admin } = req.body;
        
        // 检查用户是否存在
        const existingUser = await User.getById(id);
        if (!existingUser) {
            return res.status(404).json({
                status: 'fail',
                message: '用户不存在'
            });
        }
        
        // 如果更新用户名，检查新用户名是否已存在（排除当前用户）
        if (username && username !== existingUser.username) {
            const userWithSameName = await User.getByUsername(username);
            if (userWithSameName) {
                return res.status(400).json({
                    status: 'fail',
                    message: '用户名已存在'
                });
            }
        }
        
        // 更新用户
        const updateData = {};
        if (username !== undefined) updateData.username = username;
        if (password !== undefined) updateData.password = password;
        if (is_admin !== undefined) updateData.is_admin = is_admin === true || is_admin === 'true';
        
        await User.update(id, updateData);
        
        // 获取更新后的用户（不含密码）
        const updatedUser = await User.getById(id);
        
        res.json({
            status: 'success',
            message: '用户更新成功',
            data: updatedUser
        });
    } catch (err) {
        console.error('更新用户失败:', err);
        res.status(500).json({
            status: 'fail',
            message: '更新用户失败',
            error: err.message
        });
    }
};

/**
 * 删除用户
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.deleteUser = async (req, res) => {
    try {
        const id = parseInt(req.params.id);
        
        // 检查用户是否存在
        const existingUser = await User.getById(id);
        if (!existingUser) {
            return res.status(404).json({
                status: 'fail',
                message: '用户不存在'
            });
        }
        
        // 删除用户
        await User.delete(id);
        
        res.json({
            status: 'success',
            message: '用户删除成功'
        });
    } catch (err) {
        console.error('删除用户失败:', err);
        res.status(500).json({
            status: 'fail',
            message: '删除用户失败',
            error: err.message
        });
    }
}; 