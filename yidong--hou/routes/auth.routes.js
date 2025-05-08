/**
 * 认证路由
 * 处理用户登录注册等认证相关路由
 */
const express = require('express');
const router = express.Router();
const authController = require('../controllers/auth.controller');

/**
 * @route POST /api/auth/login
 * @description 用户登录
 * @body username: string - 用户名，必填
 * @body password: string - 密码，必填
 * @access Public
 */
router.post('/login', authController.login);

/**
 * @route POST /api/auth/register
 * @description 用户注册
 * @body username: string - 用户名，必填
 * @body password: string - 密码，必填
 * @access Public
 */
router.post('/register', authController.register);

module.exports = router; 