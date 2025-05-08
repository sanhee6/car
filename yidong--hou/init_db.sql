-- 创建数据库
CREATE DATABASE IF NOT EXISTS course_db;

-- 使用数据库
USE course_db;

-- 创建课程表
CREATE TABLE IF NOT EXISTS courses (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  teacher VARCHAR(50) NOT NULL,
  classroom VARCHAR(50),
  weekday INT NOT NULL,
  start_section INT NOT NULL,
  end_section INT NOT NULL,
  start_week INT NOT NULL,
  end_week INT NOT NULL,
  semester_id VARCHAR(20) NOT NULL
);

-- 插入一些示例数据
INSERT INTO courses (name, teacher, classroom, weekday, start_section, end_section, start_week, end_week, semester_id) VALUES
('数据库原理', '张教授', 'A101', 1, 1, 2, 1, 16, '2023-1'),
('计算机网络', '李教授', 'B203', 2, 3, 4, 1, 16, '2023-1'),
('操作系统', '王教授', 'C305', 3, 5, 6, 1, 16, '2023-1'),
('软件工程', '赵教授', 'D407', 4, 7, 8, 1, 16, '2023-1'),
('人工智能', '刘教授', 'E509', 5, 9, 10, 1, 16, '2023-1'); 