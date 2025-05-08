-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS coursedb;

-- 使用数据库
USE coursedb;

-- 创建课程表表
CREATE TABLE IF NOT EXISTS course_schedules (
  id INT AUTO_INCREMENT PRIMARY KEY,
  course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
  teacher_name VARCHAR(50) NOT NULL COMMENT '授课教师姓名',
  class_time VARCHAR(50) NOT NULL COMMENT '上课时间',
  classroom VARCHAR(50) NOT NULL COMMENT '教室',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='课程表信息';

-- 插入一些示例数据
INSERT INTO course_schedules (course_name, teacher_name, class_time, classroom) VALUES
('数据库原理', '张教授', '星期一 第1-2节', 'A101'),
('计算机网络', '李教授', '星期二 第3-4节', 'B203'),
('操作系统', '王教授', '星期三 第5-6节', 'C305'),
('软件工程', '赵教授', '星期四 第7-8节', 'D407'),
('人工智能', '刘教授', '星期五 第9-10节', 'E509'); 