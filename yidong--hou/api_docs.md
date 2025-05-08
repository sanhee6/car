# 课程管理系统 API 文档

## 基础信息

- 基础URL: `http://localhost:3000/api`
- 所有请求和响应均使用 JSON 格式
- 标准响应格式:
  ```json
  {
    "status": "success" | "fail",
    "data": [...] | {...} | null,
    "message": "可选的消息",
    "pagination": {
      "total": 总记录数,
      "page": 当前页码,
      "limit": 每页记录数,
      "totalPages": 总页数
    }
  }
  ```

## 状态码

| 状态码 | 说明 |
| ------ | ---- |
| 200 | 请求成功 |
| 201 | 资源创建成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（未登录或令牌无效） |
| 403 | 禁止访问（权限不足） |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 1. 考试管理 API

### 1.1 获取考试列表

- **URL**: `/exams`
- **方法**: `GET`
- **参数**:
  - `page`: 页码（默认: 1）
  - `limit`: 每页记录数（默认: 10）
- **返回示例**:
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": 1,
        "course_id": 1,
        "course_name": "数据库原理",
        "title": "数据库原理期中考试",
        "exam_date": "2023-04-15T09:00:00",
        "duration": 120,
        "location": "A101",
        "description": "闭卷考试，请带好学生证",
        "created_at": "2023-03-01T08:00:00",
        "updated_at": "2023-03-01T08:00:00"
      }
    ],
    "pagination": {
      "total": 10,
      "page": 1,
      "limit": 10,
      "totalPages": 1
    }
  }
  ```

### 1.2 获取单个考试详情

- **URL**: `/exams/:id`
- **方法**: `GET`
- **参数**:
  - `id`: 考试ID
- **返回示例**:
  ```json
  {
    "status": "success",
    "data": {
      "id": 1,
      "course_id": 1,
      "course_name": "数据库原理",
      "title": "数据库原理期中考试",
      "exam_date": "2023-04-15T09:00:00",
      "duration": 120,
      "location": "A101",
      "description": "闭卷考试，请带好学生证",
      "created_at": "2023-03-01T08:00:00",
      "updated_at": "2023-03-01T08:00:00"
    }
  }
  ```

### 1.3 按课程获取考试列表

- **URL**: `/exams/course/:courseId`
- **方法**: `GET`
- **参数**:
  - `courseId`: 课程ID
- **返回示例**:
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": 1,
        "course_id": 1,
        "title": "数据库原理期中考试",
        "exam_date": "2023-04-15T09:00:00",
        "duration": 120,
        "location": "A101",
        "description": "闭卷考试，请带好学生证",
        "created_at": "2023-03-01T08:00:00",
        "updated_at": "2023-03-01T08:00:00"
      }
    ]
  }
  ```

### 1.4 搜索考试

- **URL**: `/exams/search`
- **方法**: `GET`
- **参数**:
  - `query`: 搜索关键词
- **返回示例**:
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": 1,
        "course_id": 1,
        "course_name": "数据库原理",
        "title": "数据库原理期中考试",
        "exam_date": "2023-04-15T09:00:00",
        "duration": 120,
        "location": "A101",
        "description": "闭卷考试，请带好学生证",
        "created_at": "2023-03-01T08:00:00",
        "updated_at": "2023-03-01T08:00:00"
      }
    ]
  }
  ```

### 1.5 创建考试

- **URL**: `/exams`
- **方法**: `POST`
- **请求体**:
  ```json
  {
    "course_id": 1,
    "title": "数据库原理期末考试",
    "exam_date": "2023-06-20T14:00:00",
    "duration": 150,
    "location": "A102",
    "description": "闭卷考试，请带好学生证和2B铅笔"
  }
  ```
- **返回示例**:
  ```json
  {
    "status": "success",
    "message": "考试创建成功",
    "data": {
      "id": 5,
      "course_id": 1,
      "course_name": "数据库原理",
      "title": "数据库原理期末考试",
      "exam_date": "2023-06-20T14:00:00",
      "duration": 150,
      "location": "A102",
      "description": "闭卷考试，请带好学生证和2B铅笔",
      "created_at": "2023-05-01T10:30:00",
      "updated_at": "2023-05-01T10:30:00"
    }
  }
  ```

### 1.6 更新考试

- **URL**: `/exams/:id`
- **方法**: `PUT`
- **参数**:
  - `id`: 考试ID
- **请求体**:
  ```json
  {
    "title": "数据库原理期末考试（更新）",
    "location": "A103",
    "description": "闭卷考试，请带好学生证、2B铅笔和计算器"
  }
  ```
- **返回示例**:
  ```json
  {
    "status": "success",
    "message": "考试更新成功",
    "data": {
      "id": 5,
      "course_id": 1,
      "course_name": "数据库原理",
      "title": "数据库原理期末考试（更新）",
      "exam_date": "2023-06-20T14:00:00",
      "duration": 150,
      "location": "A103",
      "description": "闭卷考试，请带好学生证、2B铅笔和计算器",
      "created_at": "2023-05-01T10:30:00",
      "updated_at": "2023-05-01T10:35:00"
    }
  }
  ```

### 1.7 删除考试

- **URL**: `/exams/:id`
- **方法**: `DELETE`
- **参数**:
  - `id`: 考试ID
- **返回示例**:
  ```json
  {
    "status": "success",
    "message": "考试删除成功"
  }
  ```

## 2. 作业管理 API

### 2.1 获取作业列表

- **URL**: `/assignments`
- **方法**: `GET`
- **参数**:
  - `page`: 页码（默认: 1）
  - `limit`: 每页记录数（默认: 10）
- **返回示例**:
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": 1,
        "course_id": 1,
        "course_name": "数据库原理",
        "title": "SQL基础练习",
        "description": "SQL基本语句练习，完成教材P45-P50的习题",
        "due_date": "2023-03-10T23:59:59",
        "max_score": 100,
        "created_at": "2023-03-01T08:00:00",
        "updated_at": "2023-03-01T08:00:00"
      }
    ],
    "pagination": {
      "total": 5,
      "page": 1,
      "limit": 10,
      "totalPages": 1
    }
  }
  ```

### 2.2 获取单个作业详情

- **URL**: `/assignments/:id`
- **方法**: `GET`
- **参数**:
  - `id`: 作业ID
- **返回示例**:
  ```json
  {
    "status": "success",
    "data": {
      "id": 1,
      "course_id": 1,
      "course_name": "数据库原理",
      "title": "SQL基础练习",
      "description": "SQL基本语句练习，完成教材P45-P50的习题",
      "due_date": "2023-03-10T23:59:59",
      "max_score": 100,
      "created_at": "2023-03-01T08:00:00",
      "updated_at": "2023-03-01T08:00:00"
    }
  }
  ```

### 2.3 按课程获取作业列表

- **URL**: `/assignments/course/:courseId`
- **方法**: `GET`
- **参数**:
  - `courseId`: 课程ID
- **返回示例**:
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": 1,
        "course_id": 1,
        "title": "SQL基础练习",
        "description": "SQL基本语句练习，完成教材P45-P50的习题",
        "due_date": "2023-03-10T23:59:59",
        "max_score": 100,
        "created_at": "2023-03-01T08:00:00",
        "updated_at": "2023-03-01T08:00:00"
      }
    ]
  }
  ```

### 2.4 搜索作业

- **URL**: `/assignments/search`
- **方法**: `GET`
- **参数**:
  - `query`: 搜索关键词
- **返回示例**:
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": 1,
        "course_id": 1,
        "course_name": "数据库原理",
        "title": "SQL基础练习",
        "description": "SQL基本语句练习，完成教材P45-P50的习题",
        "due_date": "2023-03-10T23:59:59",
        "max_score": 100,
        "created_at": "2023-03-01T08:00:00",
        "updated_at": "2023-03-01T08:00:00"
      }
    ]
  }
  ```

### 2.5 创建作业

- **URL**: `/assignments`
- **方法**: `POST`
- **请求体**:
  ```json
  {
    "course_id": 1,
    "title": "数据库事务理解",
    "description": "分析事务ACID特性，并给出示例代码",
    "due_date": "2023-04-15T23:59:59",
    "max_score": 100
  }
  ```
- **返回示例**:
  ```json
  {
    "status": "success",
    "message": "作业创建成功",
    "data": {
      "id": 6,
      "course_id": 1,
      "course_name": "数据库原理",
      "title": "数据库事务理解",
      "description": "分析事务ACID特性，并给出示例代码",
      "due_date": "2023-04-15T23:59:59",
      "max_score": 100,
      "created_at": "2023-04-01T10:30:00",
      "updated_at": "2023-04-01T10:30:00"
    }
  }
  ```

### 2.6 更新作业

- **URL**: `/assignments/:id`
- **方法**: `PUT`
- **参数**:
  - `id`: 作业ID
- **请求体**:
  ```json
  {
    "title": "数据库事务分析",
    "description": "分析事务ACID特性，并给出MySQL示例代码",
    "due_date": "2023-04-20T23:59:59"
  }
  ```
- **返回示例**:
  ```json
  {
    "status": "success",
    "message": "作业更新成功",
    "data": {
      "id": 6,
      "course_id": 1,
      "course_name": "数据库原理",
      "title": "数据库事务分析",
      "description": "分析事务ACID特性，并给出MySQL示例代码",
      "due_date": "2023-04-20T23:59:59",
      "max_score": 100,
      "created_at": "2023-04-01T10:30:00",
      "updated_at": "2023-04-01T10:35:00"
    }
  }
  ```

### 2.7 删除作业

- **URL**: `/assignments/:id`
- **方法**: `DELETE`
- **参数**:
  - `id`: 作业ID
- **返回示例**:
  ```json
  {
    "status": "success",
    "message": "作业删除成功"
  }
  ```

## 3. 成绩管理 API

### 3.1 获取成绩列表

- **URL**: `/grades`
- **方法**: `GET`
- **参数**:
  - `page`: 页码（默认: 1）
  - `limit`: 每页记录数（默认: 10）
- **返回示例**:
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": 1,
        "student_id": "2023001",
        "course_id": 1,
        "assignment_id": 1,
        "course_name": "数据库原理",
        "assignment_title": "SQL基础练习",
        "score": 85,
        "feedback": "基础语句掌握良好，但高级查询有欠缺",
        "submission_date": "2023-03-09T10:30:00",
        "created_at": "2023-03-09T10:30:00",
        "updated_at": "2023-03-09T10:30:00"
      }
    ],
    "pagination": {
      "total": 7,
      "page": 1,
      "limit": 10,
      "totalPages": 1
    }
  }
  ```

### 3.2 获取单个成绩详情

- **URL**: `/grades/:id`
- **方法**: `GET`
- **参数**:
  - `id`: 成绩ID
- **返回示例**:
  ```json
  {
    "status": "success",
    "data": {
      "id": 1,
      "student_id": "2023001",
      "course_id": 1,
      "assignment_id": 1,
      "course_name": "数据库原理",
      "assignment_title": "SQL基础练习",
      "score": 85,
      "feedback": "基础语句掌握良好，但高级查询有欠缺",
      "submission_date": "2023-03-09T10:30:00",
      "created_at": "2023-03-09T10:30:00",
      "updated_at": "2023-03-09T10:30:00"
    }
  }
  ```

### 3.3 按课程获取成绩列表

- **URL**: `/grades/course/:courseId`
- **方法**: `GET`
- **参数**:
  - `courseId`: 课程ID
- **返回示例**:
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": 1,
        "student_id": "2023001",
        "course_id": 1,
        "assignment_id": 1,
        "assignment_title": "SQL基础练习",
        "score": 85,
        "feedback": "基础语句掌握良好，但高级查询有欠缺",
        "submission_date": "2023-03-09T10:30:00",
        "created_at": "2023-03-09T10:30:00",
        "updated_at": "2023-03-09T10:30:00"
      }
    ]
  }
  ```

### 3.4 按学生获取成绩列表

- **URL**: `/grades/student/:studentId`
- **方法**: `GET`
- **参数**:
  - `studentId`: 学生ID
- **返回示例**:
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": 1,
        "student_id": "2023001",
        "course_id": 1,
        "assignment_id": 1,
        "course_name": "数据库原理",
        "assignment_title": "SQL基础练习",
        "score": 85,
        "feedback": "基础语句掌握良好，但高级查询有欠缺",
        "submission_date": "2023-03-09T10:30:00",
        "created_at": "2023-03-09T10:30:00",
        "updated_at": "2023-03-09T10:30:00"
      }
    ]
  }
  ```

### 3.5 按作业获取成绩列表

- **URL**: `/grades/assignment/:assignmentId`
- **方法**: `GET`
- **参数**:
  - `assignmentId`: 作业ID
- **返回示例**:
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": 1,
        "student_id": "2023001",
        "course_id": 1,
        "assignment_id": 1,
        "score": 85,
        "feedback": "基础语句掌握良好，但高级查询有欠缺",
        "submission_date": "2023-03-09T10:30:00",
        "created_at": "2023-03-09T10:30:00",
        "updated_at": "2023-03-09T10:30:00"
      }
    ]
  }
  ```

### 3.6 获取课程统计信息

- **URL**: `/grades/course/:courseId/stats`
- **方法**: `GET`
- **参数**:
  - `courseId`: 课程ID
- **返回示例**:
  ```json
  {
    "status": "success",
    "data": {
      "stats": {
        "average_score": 85.5,
        "max_score": 92,
        "min_score": 76,
        "total_grades": 4
      },
      "assignments": [
        {
          "id": 1,
          "title": "SQL基础练习",
          "average_score": 80.5,
          "submissions": 2
        },
        {
          "id": 2,
          "title": "E-R图设计",
          "average_score": 90.5,
          "submissions": 2
        }
      ]
    }
  }
  ```

### 3.7 搜索成绩

- **URL**: `/grades/search`
- **方法**: `GET`
- **参数**:
  - `query`: 搜索关键词
- **返回示例**:
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": 1,
        "student_id": "2023001",
        "course_id": 1,
        "assignment_id": 1,
        "course_name": "数据库原理",
        "assignment_title": "SQL基础练习",
        "score": 85,
        "feedback": "基础语句掌握良好，但高级查询有欠缺",
        "submission_date": "2023-03-09T10:30:00",
        "created_at": "2023-03-09T10:30:00",
        "updated_at": "2023-03-09T10:30:00"
      }
    ]
  }
  ```

### 3.8 创建成绩

- **URL**: `/grades`
- **方法**: `POST`
- **请求体**:
  ```json
  {
    "student_id": "2023004",
    "course_id": 1,
    "assignment_id": 1,
    "score": 93,
    "feedback": "SQL查询能力很强，理解深入",
    "grade_type": "assignment"
  }
  ```
- **返回示例**:
  ```json
  {
    "status": "success",
    "message": "成绩创建成功",
    "data": {
      "id": 8,
      "student_id": "2023004",
      "course_id": 1,
      "assignment_id": 1,
      "course_name": "数据库原理",
      "assignment_title": "SQL基础练习",
      "score": 93,
      "feedback": "SQL查询能力很强，理解深入",
      "submission_date": "2023-05-01T10:30:00",
      "created_at": "2023-05-01T10:30:00",
      "updated_at": "2023-05-01T10:30:00"
    }
  }
  ```

### 3.9 更新成绩

- **URL**: `/grades/:id`
- **方法**: `PUT`
- **参数**:
  - `id`: 成绩ID
- **请求体**:
  ```json
  {
    "score": 95,
    "feedback": "SQL查询能力出色，提供了优秀的查询优化思路"
  }
  ```
- **返回示例**:
  ```json
  {
    "status": "success",
    "message": "成绩更新成功",
    "data": {
      "id": 8,
      "student_id": "2023004",
      "course_id": 1,
      "assignment_id": 1,
      "course_name": "数据库原理",
      "assignment_title": "SQL基础练习",
      "score": 95,
      "feedback": "SQL查询能力出色，提供了优秀的查询优化思路",
      "submission_date": "2023-05-01T10:30:00",
      "created_at": "2023-05-01T10:30:00",
      "updated_at": "2023-05-01T10:35:00"
    }
  }
  ```

### 3.10 删除成绩

- **URL**: `/grades/:id`
- **方法**: `DELETE`
- **参数**:
  - `id`: 成绩ID
- **返回示例**:
  ```json
  {
    "status": "success",
    "message": "成绩删除成功"
  }
  ```

## 前端调用示例 (JavaScript)

```javascript
// 获取考试列表示例
async function getExams(page = 1, limit = 10) {
  try {
    const response = await fetch(`http://localhost:3000/api/exams?page=${page}&limit=${limit}`);
    const data = await response.json();
    
    if (data.status === 'success') {
      return data.data;
    } else {
      console.error('获取考试列表失败:', data.message);
      return [];
    }
  } catch (error) {
    console.error('API请求失败:', error);
    return [];
  }
}

// 创建考试示例
async function createExam(examData) {
  try {
    const response = await fetch('http://localhost:3000/api/exams', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(examData)
    });
    
    const data = await response.json();
    
    if (data.status === 'success') {
      console.log('考试创建成功:', data.data);
      return data.data;
    } else {
      console.error('创建考试失败:', data.message);
      return null;
    }
  } catch (error) {
    console.error('API请求失败:', error);
    return null;
  }
}
``` 