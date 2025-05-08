-- 使用数据库
USE course_db;

-- 创建API操作日志表
CREATE TABLE IF NOT EXISTS api_logs (
  id INT AUTO_INCREMENT PRIMARY KEY,
  method VARCHAR(10) NOT NULL,
  endpoint VARCHAR(100) NOT NULL,
  params TEXT,
  body TEXT,
  status_code INT NOT NULL,
  response TEXT,
  ip_address VARCHAR(45),
  user_agent TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
); 