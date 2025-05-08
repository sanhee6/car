/**
 * 认证中间件
 * 用于验证和处理JWT令牌
 */
const jwt = require('jsonwebtoken');

// JWT密钥（应与auth.controller.js中相同）
const JWT_SECRET = 'your_jwt_secret';

/**
 * 验证JWT令牌
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express下一个中间件函数
 */
exports.verifyToken = (req, res, next) => {
    // 从请求头获取令牌
    const authHeader = req.headers.authorization;
    const token = authHeader && authHeader.split(' ')[1]; // Bearer TOKEN格式
    
    if (!token) {
        return res.status(401).json({
            status: 'fail',
            message: '无访问令牌，请先登录'
        });
    }
    
    try {
        // 验证令牌
        const decoded = jwt.verify(token, JWT_SECRET);
        req.user = decoded; // 将用户信息存储在请求对象中
        next();
    } catch (err) {
        return res.status(401).json({
            status: 'fail',
            message: '令牌无效或已过期',
            error: err.message
        });
    }
};

/**
 * 验证是否为管理员
 * 必须在verifyToken之后使用
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express下一个中间件函数
 */
exports.isAdmin = (req, res, next) => {
    if (!req.user) {
        return res.status(401).json({
            status: 'fail',
            message: '未授权，请先登录'
        });
    }
    
    if (!req.user.is_admin) {
        return res.status(403).json({
            status: 'fail',
            message: '禁止访问，需要管理员权限'
        });
    }
    
    next();
}; 