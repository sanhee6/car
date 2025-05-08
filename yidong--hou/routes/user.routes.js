/**
 * 用户路由
 * 处理用户相关的API路由
 */
const express = require('express');
const router = express.Router();
const userController = require('../controllers/user.controller');
const userValidator = require('../middleware/user.validator');
const { verifyToken, isAdmin } = require('../middleware/auth.middleware');

/**
 * @route GET /api/users
 * @description 获取所有用户（支持分页）
 * @query page: int - 当前页码，默认为 1
 * @query limit: int - 每页显示的数量，默认为 10 
 * @access 仅管理员
 */
router.get('/', verifyToken, isAdmin, userController.getUsers);

/**
 * @route GET /api/users/:id
 * @description 获取指定 ID 的用户信息
 * @param id: string - 用户 ID
 * @access 已登录用户（只能访问自己）或管理员
 */
router.get('/:id', verifyToken, (req, res, next) => {
    // 如果是管理员或者请求的是自己的信息，则允许访问
    if (req.user.is_admin || req.user.id === parseInt(req.params.id)) {
        next();
    } else {
        res.status(403).json({
            status: 'fail',
            message: '只能访问自己的用户信息'
        });
    }
}, userValidator.validateUserId, userController.getUserById);

/**
 * @route POST /api/users
 * @description 创建新用户（管理员创建）
 * @body username: string - 用户名，必填
 * @body password: string - 密码，必填
 * @body is_admin: boolean - 是否为管理员，必填
 * @access 仅管理员
 */
router.post('/', verifyToken, isAdmin, userValidator.validateCreateUser, userController.createUser);

/**
 * @route PUT /api/users/:id
 * @description 更新指定 ID 的用户信息
 * @param id: string - 用户 ID
 * @body username: string - 用户名，可选
 * @body password: string - 密码，可选
 * @body is_admin: boolean - 是否为管理员，可选
 * @access 已登录用户（只能更新自己的部分信息）或管理员
 */
router.put('/:id', verifyToken, (req, res, next) => {
    // 如果不是管理员且修改的不是自己的信息
    if (!req.user.is_admin && req.user.id !== parseInt(req.params.id)) {
        return res.status(403).json({
            status: 'fail',
            message: '只能修改自己的用户信息'
        });
    }
    
    // 如果不是管理员，不能修改是否为管理员的字段
    if (!req.user.is_admin && req.body.is_admin !== undefined) {
        return res.status(403).json({
            status: 'fail',
            message: '没有权限修改管理员状态'
        });
    }
    
    next();
}, userValidator.validateUserId, userValidator.validateUpdateUser, userController.updateUser);

/**
 * @route DELETE /api/users/:id
 * @description 删除指定 ID 的用户
 * @param id: string - 用户 ID
 * @access 仅管理员
 */
router.delete('/:id', verifyToken, isAdmin, userValidator.validateUserId, userController.deleteUser);

module.exports = router; 