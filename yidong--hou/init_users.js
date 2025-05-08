/**
 * 初始化用户表脚本
 * 用于创建用户表并插入初始测试数据
 */
const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');
const bcrypt = require('bcryptjs');

/**
 * 创建数据库连接
 */
const createConnection = async () => {
    return await mysql.createConnection({
        host: 'localhost',
        port: 3306,
        user: 'root',
        password: '111111',
        database: 'coursedb',
        multipleStatements: true // 允许执行多条SQL语句
    });
};

/**
 * 读取SQL文件
 * @param {string} filePath - SQL文件路径
 * @returns {string} SQL文件内容
 */
const readSqlFile = (filePath) => {
    return fs.readFileSync(path.join(__dirname, filePath), 'utf8');
};

/**
 * 创建用户表并插入初始数据
 */
const initUsersTable = async () => {
    let connection;
    try {
        connection = await createConnection();
        console.log('数据库连接成功');
        
        // 创建用户表
        const createTableSQL = `
        CREATE TABLE IF NOT EXISTS users (
            id INT AUTO_INCREMENT PRIMARY KEY,
            username VARCHAR(255) NOT NULL UNIQUE,
            password VARCHAR(255) NOT NULL,
            is_admin BOOLEAN NOT NULL DEFAULT FALSE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        );`;
        
        await connection.query(createTableSQL);
        console.log('用户表创建成功');
        
        // 检查是否已有用户数据
        const [rows] = await connection.query('SELECT COUNT(*) as count FROM users');
        if (rows[0].count > 0) {
            console.log('用户表已有数据，跳过初始化');
            return;
        }
        
        // 插入测试用户数据
        const saltRounds = 10;
        const adminPassword = await bcrypt.hash('admin123', saltRounds);
        const studentPassword = await bcrypt.hash('student123', saltRounds);
        
        const insertUsersSQL = `
        INSERT INTO users (username, password, is_admin) VALUES
        ('admin', ?, TRUE),
        ('student', ?, FALSE);`;
        
        await connection.query(insertUsersSQL, [adminPassword, studentPassword]);
        console.log('初始用户数据插入成功');
        
    } catch (error) {
        console.error('初始化用户表失败:', error);
    } finally {
        if (connection) {
            await connection.end();
            console.log('数据库连接已关闭');
        }
    }
};

// 执行初始化
initUsersTable().then(() => {
    console.log('用户表初始化完成');
}).catch(err => {
    console.error('用户表初始化失败:', err);
}); 