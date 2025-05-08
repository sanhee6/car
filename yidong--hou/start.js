/**
 * 课表管理系统启动脚本
 * 用于初始化数据库并启动服务器
 */
const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');
const mysql = require('mysql2/promise');
const dbConfig = require('./config/db.config');

// 创建logs目录
const logDir = path.join(__dirname, 'logs');
if (!fs.existsSync(logDir)) {
  console.log('创建日志目录...');
  fs.mkdirSync(logDir, { recursive: true });
}

// 初始化数据库函数
async function initDatabase() {
  console.log('正在初始化数据库...');
  
  // 创建没有数据库名称的连接配置
  const connectionConfig = {
    host: dbConfig.host,
    user: dbConfig.user,
    password: dbConfig.password,
    port: dbConfig.port
  };
  
  try {
    // 尝试连接MySQL服务器
    const connection = await mysql.createConnection(connectionConfig);
    console.log('MySQL连接成功！');
    
    // 读取SQL初始化脚本
    const sql = fs.readFileSync('./course_db.sql', 'utf8');
    
    // 按语句分割并执行
    const statements = sql.split(';')
      .map(statement => statement.trim())
      .filter(statement => statement.length > 0);
    
    for (const statement of statements) {
      await connection.query(statement);
      console.log('已执行: ' + statement.substring(0, 50) + '...');
    }
    
    console.log('数据库初始化完成！');
    await connection.end();
    
    return true;
  } catch (error) {
    console.error('数据库初始化失败:', error.message);
    return false;
  }
}

// 启动服务器函数
async function startServer() {
  console.log('正在启动服务器...');
  
  try {
    // 导入服务器模块
    require('./server');
  } catch (error) {
    console.error('服务器启动失败:', error.message);
    process.exit(1);
  }
}

// 主函数
async function main() {
  console.log('======= 课表管理系统启动 =======');
  
  // 初始化数据库
  const dbInitialized = await initDatabase();
  
  if (dbInitialized) {
    // 启动服务器
    await startServer();
  } else {
    console.error('由于数据库初始化失败，无法启动服务器');
    process.exit(1);
  }
}

// 执行主函数
main().catch(err => {
  console.error('启动过程中出现错误:', err);
  process.exit(1);
}); 