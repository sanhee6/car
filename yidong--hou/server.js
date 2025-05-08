/**
 * 主服务器文件
 * 创建Express应用并配置中间件和路由
 */
const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const { testConnection } = require('./config/db');

// 导入中间件
const logger = require('./middleware/logger.js');
const { fieldMapperMiddleware, transformAssignmentFields, transformGradeFields } = require('./utils/field_mapper.js');

// 导入路由
const courseRoutes = require('./routes/course.routes.js');
const courseScheduleRoutes = require('./routes/course_schedule.routes.js');
const examRoutes = require('./routes/exam.routes.js');
const assignmentRoutes = require('./routes/assignment.routes.js');
const gradeRoutes = require('./routes/grade.routes.js');
const userRoutes = require('./routes/user.routes');
const authRoutes = require('./routes/auth.routes');

// 初始化环境变量
dotenv.config();

// 创建Express应用
const app = express();

// 测试数据库连接
testConnection();

// 中间件
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(logger);  // 添加日志中间件

// 简单的根路由
app.get('/', (req, res) => {
  res.json({ 
    message: "欢迎使用课程API",
    version: '1.0.0',
    endpoints: {
      auth: "/api/auth",
      users: '/api/users',
      courses: "/api/courses",
      course_schedules: "/api/course_schedules",
      exams: "/api/exams",
      assignments: "/api/assignments",
      grades: "/api/grades"
    }
  });
});

// 使用路由（添加特定路由的字段映射中间件）
app.use('/api/auth', authRoutes);
app.use('/api/users', userRoutes);
app.use('/api/courses', courseRoutes);
app.use('/api/course_schedules', courseScheduleRoutes);
app.use('/api/exams', examRoutes);
app.use('/api/assignments', fieldMapperMiddleware(transformAssignmentFields), assignmentRoutes);
app.use('/api/grades', fieldMapperMiddleware(transformGradeFields), gradeRoutes);

// 全局错误处理
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({
    status: 'fail',
    message: '服务器内部错误',
    error: process.env.NODE_ENV === 'development' ? err.message : undefined
  });
});

// 处理404错误
app.use((req, res) => {
  res.status(404).json({
    status: 'fail',
    message: `找不到路径: ${req.originalUrl}`
  });
});

// 启动服务器
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`服务器运行在端口 ${PORT}`);
  console.log(`认证API: http://localhost:${PORT}/api/auth`);
  console.log(`用户API: http://localhost:${PORT}/api/users`);
  console.log(`课程API: http://localhost:${PORT}/api/courses`);
  console.log(`课程表API: http://localhost:${PORT}/api/course_schedules`);
  console.log(`考试API: http://localhost:${PORT}/api/exams`);
  console.log(`作业API: http://localhost:${PORT}/api/assignments`);
  console.log(`成绩API: http://localhost:${PORT}/api/grades`);
}); 