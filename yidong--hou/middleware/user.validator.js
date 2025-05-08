/**
 * 用户数据验证中间件
 * 用于验证用户相关请求的数据
 */

/**
 * 验证用户ID是否为有效整数
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - 下一个中间件函数
 */
exports.validateUserId = (req, res, next) => {
    const id = req.params.id;
    
    // 检查ID是否为有效整数
    if (!id || isNaN(parseInt(id)) || parseInt(id) <= 0) {
        return res.status(400).json({
            status: 'fail',
            message: '无效的用户ID'
        });
    }
    
    next();
};

/**
 * 验证创建用户的请求数据
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - 下一个中间件函数
 */
exports.validateCreateUser = (req, res, next) => {
    const { username, password, is_admin } = req.body;
    
    // 检查必填字段
    if (!username) {
        return res.status(400).json({
            status: 'fail',
            message: '用户名不能为空'
        });
    }
    
    if (!password) {
        return res.status(400).json({
            status: 'fail',
            message: '密码不能为空'
        });
    }
    
    if (is_admin === undefined) {
        return res.status(400).json({
            status: 'fail',
            message: '管理员状态不能为空'
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
    
    next();
};

/**
 * 验证更新用户的请求数据
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - 下一个中间件函数
 */
exports.validateUpdateUser = (req, res, next) => {
    const { username, password } = req.body;
    
    // 至少需要一个要更新的字段
    if (!username && !password && req.body.is_admin === undefined) {
        return res.status(400).json({
            status: 'fail',
            message: '请提供至少一个要更新的字段'
        });
    }
    
    // 如果提供了用户名，验证长度
    if (username !== undefined && (username.length < 3 || username.length > 50)) {
        return res.status(400).json({
            status: 'fail',
            message: '用户名长度应在3-50个字符之间'
        });
    }
    
    // 如果提供了密码，验证长度
    if (password !== undefined && password.length < 6) {
        return res.status(400).json({
            status: 'fail',
            message: '密码长度不能少于6个字符'
        });
    }
    
    next();
}; 