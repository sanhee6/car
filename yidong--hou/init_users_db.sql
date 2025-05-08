-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE, -- 用户名必须唯一
    password VARCHAR(255) NOT NULL,   -- 登录时的密码
    is_admin BOOLEAN NOT NULL DEFAULT FALSE, -- 是否为管理员，默认为非管理员
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新时间
);

-- 插入测试数据
INSERT INTO users (username, password, is_admin) VALUES
('admin', '$2b$10$X5.ZKSEzP3DYH5/x9aUGSeopgb7V1qKXjNiYU1cgOfOWv/4cKCTYm', TRUE), -- 密码: admin123
('student', '$2b$10$DwGq5Ov5.MjK5dEzXm59V.YeEFTDGY0excSeMSc2p7DC6SYVIRcvy', FALSE); -- 密码: student123 