/**
 * 数据库配置文件
 * 用于连接MySQL数据库
 */
const mysql = require('mysql2/promise');

// 创建数据库连接池
const pool = mysql.createPool({
    host: 'localhost',
    port: 3306,
    user: 'root',
    password: '111111',
    database: 'coursedb',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

/**
 * 测试数据库连接
 * 在应用启动时调用，确保数据库连接正常
 */
const testConnection = async () => {
    try {
        const connection = await pool.getConnection();
        console.log('数据库连接成功');
        connection.release();
        return true;
    } catch (error) {
        console.error('数据库连接失败:', error);
        return false;
    }
};

module.exports = {
    pool,
    testConnection
}; 