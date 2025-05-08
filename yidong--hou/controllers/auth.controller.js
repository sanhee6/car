/**
 * 认证控制器
 * 处理用户登录和注册相关逻辑
 */
const User = require('../models/user.model');
const jwt = require('jsonwebtoken');

// JWT密钥（生产环境应从环境变量获取）
const JWT_SECRET = 'your_jwt_secret';

/**
 * 用户登录
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.login = async (req, res) => {
    try {
        const { username, password } = req.body;
        
        // 验证请求体
        if (!username || !password) {
            return res.status(400).json({
                status: 'fail',
                message: '用户名和密码不能为空'
            });
        }
        
        // 查找用户
        const user = await User.getByUsername(username);
        
        // 用户不存在
        if (!user) {
            return res.status(401).json({
                status: 'fail',
                message: '用户名或密码错误'
            });
        }
        
        // 验证密码
        const isPasswordValid = await User.validatePassword(password, user.password);
        
        if (!isPasswordValid) {
            return res.status(401).json({
                status: 'fail',
                message: '用户名或密码错误'
            });
        }
        
        // 创建JWT令牌
        const token = jwt.sign(
            { 
                id: user.id, 
                username: user.username, 
                is_admin: user.is_admin 
            },
            JWT_SECRET,
            { expiresIn: '24h' }
        );
        
        // 返回用户信息和令牌
        res.json({
            status: 'success',
            message: '登录成功',
            data: {
                id: user.id,
                username: user.username,
                is_admin: user.is_admin,
                token
            }
        });
    } catch (err) {
        console.error('登录失败:', err);
        res.status(500).json({
            status: 'fail',
            message: '登录失败',
            error: err.message
        });
    }
};

/**
 * 用户注册
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 */
exports.register = async (req, res) => {
    try {
        const { username, password } = req.body;
        
        // 验证请求体
        if (!username || !password) {
            return res.status(400).json({
                status: 'fail',
                message: '用户名和密码不能为空'
            });
        }
        
        // 验证用户名长度
        if (username.length < 3 || username.length > 50) {
            return res.status(400).json({
                status: 'fail',
                message: '用户名长度应在3-50个字符之间'
            });
        }
        
        // 验证密码长度
        if (password.length < 6) {
            return res.status(400).json({
                status: 'fail',
                message: '密码长度不能少于6个字符'
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
        
        // 创建用户（默认非管理员）
        const result = await User.create({
            username,
            password,
            is_admin: false
        });
        
        // 获取创建的用户（不含密码）
        const newUser = await User.getById(result.insertId);
        
        // 创建JWT令牌
        const token = jwt.sign(
            { 
                id: newUser.id, 
                username: newUser.username, 
                is_admin: newUser.is_admin 
            },
            JWT_SECRET,
            { expiresIn: '24h' }
        );
        
        res.status(201).json({
            status: 'success',
            message: '注册成功',
            data: {
                id: newUser.id,
                username: newUser.username,
                is_admin: newUser.is_admin,
                token
            }
        });
    } catch (err) {
        console.error('注册失败:', err);
        res.status(500).json({
            status: 'fail',
            message: '注册失败',
            error: err.message
        });
    }
}; 