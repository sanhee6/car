-- 使用数据库
USE coursedb;

-- 创建考试表
CREATE TABLE IF NOT EXISTS exams (
  id INT AUTO_INCREMENT PRIMARY KEY,
  course_id INT NOT NULL COMMENT '关联的课程ID',
  title VARCHAR(100) NOT NULL COMMENT '考试标题',
  exam_date DATETIME NOT NULL COMMENT '考试日期时间',
  duration INT NOT NULL COMMENT '考试时长（分钟）',
  location VARCHAR(100) NOT NULL COMMENT '考试地点',
  description TEXT COMMENT '考试说明',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) COMMENT='考试信息表';

-- 创建作业表
CREATE TABLE IF NOT EXISTS assignments (
  id INT AUTO_INCREMENT PRIMARY KEY,
  course_id INT NOT NULL COMMENT '关联的课程ID',
  title VARCHAR(100) NOT NULL COMMENT '作业标题',
  description TEXT COMMENT '作业描述',
  due_date DATETIME NOT NULL COMMENT '截止日期',
  max_score INT NOT NULL DEFAULT 100 COMMENT '总分值',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) COMMENT='作业信息表';

-- 创建成绩表
CREATE TABLE IF NOT EXISTS grades (
  id INT AUTO_INCREMENT PRIMARY KEY,
  student_id VARCHAR(20) NOT NULL COMMENT '学生ID',
  course_id INT NOT NULL COMMENT '课程ID',
  assignment_id INT COMMENT '作业ID，可为空表示非作业成绩',
  exam_id INT COMMENT '考试ID，可为空表示非考试成绩',
  score DECIMAL(5,2) NOT NULL COMMENT '分数',
  feedback TEXT COMMENT '反馈评语',
  submission_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交日期',
  grade_type ENUM('assignment', 'exam', 'final', 'other') NOT NULL DEFAULT 'other' COMMENT '成绩类型',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
  FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE SET NULL,
  FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE SET NULL
) COMMENT='成绩信息表';

-- 插入示例数据：考试
INSERT INTO exams (course_id, title, exam_date, duration, location, description) VALUES
(1, '数据库原理期中考试', '2023-04-15 09:00:00', 120, 'A101', '闭卷考试，请带好学生证'),
(1, '数据库原理期末考试', '2023-06-20 14:00:00', 150, 'A102', '闭卷考试，请带好学生证和2B铅笔'),
(2, '计算机网络期中考试', '2023-04-16 09:00:00', 120, 'B203', '开卷考试，可带教材'),
(2, '计算机网络期末考试', '2023-06-22 14:00:00', 150, 'B204', '闭卷考试，只允许带计算器');

-- 插入示例数据：作业
INSERT INTO assignments (course_id, title, description, due_date, max_score) VALUES
(1, 'SQL基础练习', 'SQL基本语句练习，完成教材P45-P50的习题', '2023-03-10 23:59:59', 100),
(1, 'E-R图设计', '设计一个图书管理系统的E-R图', '2023-04-05 23:59:59', 100),
(1, '数据库系统实现', '基于MySQL实现一个简单的图书管理系统', '2023-05-20 23:59:59', 100),
(2, 'TCP/IP协议分析', '分析并总结TCP/IP协议的特点', '2023-03-15 23:59:59', 100),
(2, '网络拓扑设计', '设计一个小型企业网络拓扑结构', '2023-04-10 23:59:59', 100);

-- 插入示例数据：成绩
INSERT INTO grades (student_id, course_id, assignment_id, exam_id, score, feedback, submission_date, grade_type) VALUES
('2023001', 1, 1, NULL, 85, '基础语句掌握良好，但高级查询有欠缺', '2023-03-09 10:30:00', 'assignment'),
('2023001', 1, NULL, 1, 78, '对数据完整性部分理解不足', '2023-04-15 11:30:00', 'exam'),
('2023001', 2, 4, NULL, 92, '分析透彻，有深度', '2023-03-14 21:45:00', 'assignment'),
('2023002', 1, 1, NULL, 76, '部分查询语句错误', '2023-03-10 09:15:00', 'assignment'),
('2023002', 1, NULL, 1, 85, '规范化理论掌握较好', '2023-04-15 11:30:00', 'exam'),
('2023003', 2, 4, NULL, 88, '内容完整，但缺少一些细节分析', '2023-03-15 08:00:00', 'assignment'),
('2023003', 2, NULL, 3, 91, '协议理解透彻', '2023-04-16 11:45:00', 'exam'); 