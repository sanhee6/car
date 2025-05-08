-- 使用数据库
USE coursedb;

-- 创建课程表
CREATE TABLE IF NOT EXISTS courses (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL COMMENT '课程名称',
  teacher VARCHAR(50) NOT NULL COMMENT '教师姓名',
  classroom VARCHAR(50) COMMENT '教室',
  weekday INT NOT NULL COMMENT '周几（1-7）',
  start_section INT NOT NULL COMMENT '开始节次',
  end_section INT NOT NULL COMMENT '结束节次',
  start_week INT NOT NULL COMMENT '开始周次',
  end_week INT NOT NULL COMMENT '结束周次',
  semester_id VARCHAR(20) NOT NULL COMMENT '学期标识',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='课程信息';

-- 插入一些示例数据
INSERT INTO courses (name, teacher, classroom, weekday, start_section, end_section, start_week, end_week, semester_id) VALUES
('数据库原理', '张教授', 'A101', 1, 1, 2, 1, 16, '2023-1'),
('计算机网络', '李教授', 'B203', 2, 3, 4, 1, 16, '2023-1'),
('操作系统', '王教授', 'C305', 3, 5, 6, 1, 16, '2023-1'),
('软件工程', '赵教授', 'D407', 4, 7, 8, 1, 16, '2023-1'),
('人工智能', '刘教授', 'E509', 5, 9, 10, 1, 16, '2023-1'); 