# 课程管理系统 API

一个基于Node.js、Express和MySQL的课程管理系统后端API。

## 功能特点

- RESTful API设计
- MySQL数据库存储
- 异步/等待语法处理异步操作
- 适当的错误处理和输入验证
- 日志记录功能

## 技术栈

- Node.js
- Express.js
- MySQL
- Cors (跨域支持)
- dotenv (环境变量管理)

## 项目结构

```
.
├── config/              # 配置文件
│   └── db.config.js     # 数据库配置
├── controllers/         # 控制器
│   └── course_schedule.controller.js  # 课程表控制器
├── middleware/          # 中间件
│   ├── logger.js        # 日志中间件
│   └── validator.js     # 数据验证中间件
├── models/              # 数据模型
│   ├── db.js            # 数据库连接
│   └── course_schedule.model.js  # 课程表模型
├── routes/              # 路由
│   └── course_schedule.routes.js  # 课程表路由
├── logs/                # 日志文件目录
├── .env                 # 环境变量
├── init_course_schedules_db.sql  # 数据库初始化SQL
├── init_db.js           # 数据库初始化脚本
├── server.js            # 应用入口
└── package.json         # 项目依赖
```

## 安装

1. 克隆项目

```bash
git clone <仓库地址>
cd <项目目录>
```

2. 安装依赖

```bash
npm install
```

3. 配置环境变量

创建`.env`文件，并设置以下变量:

```
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=111111
DB_NAME=coursedb
DB_PORT=3306
PORT=3000
NODE_ENV=development
```

4. 初始化数据库

```bash
node init_db.js
```

5. 启动服务器

```bash
npm start
```

开发模式:

```bash
npm run dev
```

## API 接口

### 课程表管理

#### 1. 获取课程表列表

- **URL:** `/api/course_schedules`
- **方法:** `GET`
- **查询参数:**
  - `page`: 当前页码，默认为 1
  - `limit`: 每页显示的数量，默认为 10
- **响应:**
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": 1,
        "course_name": "数据库原理",
        "teacher_name": "张教授",
        "class_time": "星期一 第1-2节",
        "classroom": "A101",
        "created_at": "2023-01-01T08:00:00.000Z",
        "updated_at": "2023-01-01T08:00:00.000Z"
      }
    ],
    "pagination": {
      "total": 1,
      "page": 1,
      "limit": 10,
      "totalPages": 1
    }
  }
  ```

#### 2. 添加课程表

- **URL:** `/api/course_schedules`
- **方法:** `POST`
- **请求体:**
  ```json
  {
    "course_name": "数据库原理",
    "teacher_name": "张教授",
    "class_time": "星期一 第1-2节",
    "classroom": "A101"
  }
  ```
- **响应:**
  ```json
  {
    "status": "success",
    "message": "课程表创建成功",
    "data": {
      "id": 1,
      "course_name": "数据库原理",
      "teacher_name": "张教授",
      "class_time": "星期一 第1-2节",
      "classroom": "A101",
      "created_at": "2023-01-01T08:00:00.000Z",
      "updated_at": "2023-01-01T08:00:00.000Z"
    }
  }
  ```

#### 3. 编辑课程表

- **URL:** `/api/course_schedules/:id`
- **方法:** `PUT`
- **请求体:**
  ```json
  {
    "course_name": "高级数据库原理",
    "teacher_name": "王教授",
    "class_time": "星期二 第3-4节",
    "classroom": "B203"
  }
  ```
- **响应:**
  ```json
  {
    "status": "success",
    "message": "课程表更新成功",
    "data": {
      "id": 1,
      "course_name": "高级数据库原理",
      "teacher_name": "王教授",
      "class_time": "星期二 第3-4节",
      "classroom": "B203",
      "created_at": "2023-01-01T08:00:00.000Z",
      "updated_at": "2023-01-01T09:00:00.000Z"
    }
  }
  ```

#### 4. 删除课程表

- **URL:** `/api/course_schedules/:id`
- **方法:** `DELETE`
- **响应:**
  ```json
  {
    "status": "success",
    "message": "课程表删除成功"
  }
  ```

## 错误处理

所有API请求在发生错误时将返回一个包含错误信息的JSON对象:

```json
{
  "status": "fail",
  "message": "错误消息",
  "error": "详细错误信息（仅在开发环境）"
}
```

## 开发与测试

本项目包含日志功能，所有API请求都会记录在`logs/`目录下的日志文件中。

## 许可证

MIT 

## API文档

本项目包含以下模块的API文档：

1. [课程表管理API文档](./course_api_docs.md) - 课程基本信息和课程表管理
2. [考试管理API文档](./exam_api_docs.md) - 课程考试信息管理
3. [作业管理API文档](./assignment_api_docs.md) - 课程作业信息管理  
4. [成绩管理API文档](./grade_api_docs.md) - 学生成绩信息管理

查看各文档了解详细的API使用方法。 