/**
 * 日志中间件
 * 记录所有API请求的日志
 */
const fs = require('fs');
const path = require('path');
const moment = require('moment');

/**
 * 创建日志目录（如果不存在）
 */
const createLogDir = () => {
    const logDir = path.join(__dirname, '../logs');
    if (!fs.existsSync(logDir)) {
        fs.mkdirSync(logDir);
    }
    return logDir;
};

/**
 * 获取当前日期格式化字符串，用于日志文件名
 * @returns {string} 格式化的日期字符串（YYYY-MM-DD）
 */
const getFormattedDate = () => {
    return moment().format('YYYY-MM-DD');
};

/**
 * 写入日志到文件
 * @param {string} message - 日志消息
 */
const writeToLogFile = (message) => {
    try {
        const logDir = createLogDir();
        const logFile = path.join(logDir, `${getFormattedDate()}.log`);
        const logMessage = `${moment().format('YYYY-MM-DD HH:mm:ss')} - ${message}\n`;
        
        fs.appendFileSync(logFile, logMessage);
    } catch (err) {
        console.error('写入日志文件失败:', err);
    }
};

/**
 * 日志中间件函数
 * @param {Object} req - Express请求对象
 * @param {Object} res - Express响应对象
 * @param {Function} next - Express下一个中间件函数
 */
const logger = (req, res, next) => {
    // 记录请求开始时间
    const startTime = new Date();
    
    // 生成请求日志消息
    const reqLog = `${req.method} ${req.originalUrl} - IP: ${req.ip}`;
    console.log(reqLog);
    writeToLogFile(reqLog);
    
    // 捕获响应完成事件
    res.on('finish', () => {
        // 计算响应时间
        const responseTime = new Date() - startTime;
        
        // 生成响应日志消息
        const resLog = `${req.method} ${req.originalUrl} - 状态: ${res.statusCode} - 响应时间: ${responseTime}ms`;
        console.log(resLog);
        writeToLogFile(resLog);
    });
    
    next();
};

module.exports = logger; 