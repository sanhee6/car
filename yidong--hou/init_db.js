const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');
const dbConfig = require('./config/db.config.js');

/**
 * 初始化数据库
 * 读取SQL文件并执行创建表和插入样本数据
 */
async function initDatabase() {
  let connection;
  try {
    // 创建连接，但不指定数据库（因为可能还不存在）
    connection = await mysql.createConnection({
      host: dbConfig.host,
      user: dbConfig.user,
      password: dbConfig.password,
      port: dbConfig.port
    });
    
    console.log('数据库连接成功');
    
    // 读取课程表SQL文件
    const courseSchedulesSqlPath = path.join(__dirname, 'init_course_schedules_db.sql');
    const courseSchedulesSql = fs.readFileSync(courseSchedulesSqlPath, 'utf8');
    
    // 将SQL文件按语句分割
    const courseSchedulesStatements = courseSchedulesSql.split(';').filter(statement => statement.trim() !== '');
    
    // 依次执行每条SQL语句
    for (const statement of courseSchedulesStatements) {
      await connection.query(statement);
      console.log(`执行SQL: ${statement.substring(0, 80)}...`);
    }
    
    // 读取课程SQL文件
    const coursesSqlPath = path.join(__dirname, 'init_courses_db.sql');
    const coursesSql = fs.readFileSync(coursesSqlPath, 'utf8');
    
    // 将SQL文件按语句分割
    const coursesStatements = coursesSql.split(';').filter(statement => statement.trim() !== '');
    
    // 依次执行每条SQL语句
    for (const statement of coursesStatements) {
      await connection.query(statement);
      console.log(`执行SQL: ${statement.substring(0, 80)}...`);
    }
    
    // 读取附加表SQL文件（考试、作业、成绩表）
    const additionalTablesSqlPath = path.join(__dirname, 'init_additional_tables.sql');
    const additionalTablesSql = fs.readFileSync(additionalTablesSqlPath, 'utf8');
    
    // 将SQL文件按语句分割
    const additionalTablesStatements = additionalTablesSql.split(';').filter(statement => statement.trim() !== '');
    
    // 依次执行每条SQL语句
    for (const statement of additionalTablesStatements) {
      await connection.query(statement);
      console.log(`执行SQL: ${statement.substring(0, 80)}...`);
    }
    
    console.log('数据库初始化完成!');
  } catch (err) {
    console.error('数据库初始化失败:', err);
  } finally {
    if (connection) {
      await connection.end();
      console.log('数据库连接关闭');
    }
  }
}

// 执行初始化
initDatabase(); 