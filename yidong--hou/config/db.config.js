const dotenv = require('dotenv');
dotenv.config();

// 导出数据库配置
module.exports = {
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '111111',
  database: process.env.DB_NAME || 'coursedb',
  port: process.env.DB_PORT || 3306,
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
}; 