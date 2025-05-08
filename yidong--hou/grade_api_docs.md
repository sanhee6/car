# 成绩模块API文档

## 基础信息

- 基础路径: `/api/grades`
- 数据格式: JSON
- 认证方式: 无（可根据需要添加）

## API端点

### 1. 获取所有成绩

获取系统中的所有成绩信息，支持分页。

**请求方法**: `GET`

**URL**: `/api/grades`

**查询参数**:

| 参数名 | 类型   | 必填 | 描述                |
|--------|--------|------|---------------------|
| page   | Number | 否   | 页码（默认值: 1）    |
| limit  | Number | 否   | 每页数量（默认值: 10）|

**响应示例**:

```json
{
  "success": true,
  "data": {
    "rows": [
      {
        "id": 1,
        "student_id": "2023001",
        "course_id": 1,
        "assignment_id": 1,
        "course_name": "数据库原理",
        "assignment_title": "SQL基础练习",
        "score": 85,
        "feedback": "基础语句掌握良好，但高级查询有欠缺",
        "submission_date": "2023-03-09T10:30:00.000Z",
        "created_at": "2023-03-09T10:30:00.000Z",
        "updated_at": "2023-03-09T10:30:00.000Z"
      },
      // 更多成绩记录...
    ],
    "pagination": {
      "total": 7,
      "page": 1,
      "limit": 10,
      "totalPages": 1
    }
  },
  "message": "获取成绩列表成功"
}
```

### 2. 获取指定成绩

根据ID获取单个成绩的详细信息。

**请求方法**: `GET`

**URL**: `/api/grades/:id`

**路径参数**:

| 参数名 | 类型   | 必填 | 描述    |
|--------|--------|------|---------|
| id     | Number | 是   | 成绩ID  |

**响应示例**:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "student_id": "2023001",
    "course_id": 1,
    "assignment_id": 1,
    "course_name": "数据库原理",
    "assignment_title": "SQL基础练习",
    "score": 85,
    "feedback": "基础语句掌握良好，但高级查询有欠缺",
    "submission_date": "2023-03-09T10:30:00.000Z",
    "created_at": "2023-03-09T10:30:00.000Z",
    "updated_at": "2023-03-09T10:30:00.000Z"
  },
  "message": "获取成绩详情成功"
}
```

### 3. 按课程获取成绩

获取指定课程的所有成绩列表。

**请求方法**: `GET`

**URL**: `/api/grades/course/:courseId`

**路径参数**:

| 参数名    | 类型   | 必填 | 描述   |
|-----------|--------|------|--------|
| courseId  | Number | 是   | 课程ID |

**响应示例**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "student_id": "2023001",
      "course_id": 1,
      "assignment_id": 1,
      "assignment_title": "SQL基础练习",
      "score": 85,
      "feedback": "基础语句掌握良好，但高级查询有欠缺",
      "submission_date": "2023-03-09T10:30:00.000Z",
      "created_at": "2023-03-09T10:30:00.000Z",
      "updated_at": "2023-03-09T10:30:00.000Z"
    },
    // 更多该课程的成绩...
  ],
  "message": "获取课程成绩列表成功"
}
```

### 4. 按学生获取成绩

获取指定学生的所有成绩列表。

**请求方法**: `GET`

**URL**: `/api/grades/student/:studentId`

**路径参数**:

| 参数名     | 类型   | 必填 | 描述    |
|------------|--------|------|---------|
| studentId  | String | 是   | 学生ID  |

**响应示例**:

```json
{
  "success": true,
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
      "submission_date": "2023-03-09T10:30:00.000Z",
      "created_at": "2023-03-09T10:30:00.000Z",
      "updated_at": "2023-03-09T10:30:00.000Z"
    },
    // 更多该学生的成绩...
  ],
  "message": "获取学生成绩列表成功"
}
```

### 5. 按作业获取成绩

获取指定作业的所有成绩列表。

**请求方法**: `GET`

**URL**: `/api/grades/assignment/:assignmentId`

**路径参数**:

| 参数名        | 类型   | 必填 | 描述    |
|---------------|--------|------|---------|
| assignmentId  | Number | 是   | 作业ID  |

**响应示例**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "student_id": "2023001",
      "course_id": 1,
      "assignment_id": 1,
      "score": 85,
      "feedback": "基础语句掌握良好，但高级查询有欠缺",
      "submission_date": "2023-03-09T10:30:00.000Z",
      "created_at": "2023-03-09T10:30:00.000Z",
      "updated_at": "2023-03-09T10:30:00.000Z"
    },
    // 更多该作业的成绩...
  ],
  "message": "获取作业成绩列表成功"
}
```

### 6. 创建成绩

创建一个新的成绩记录。

**请求方法**: `POST`

**URL**: `/api/grades`

**请求体**:

| 字段名        | 类型   | 必填 | 描述                       |
|---------------|--------|------|----------------------------|
| student_id    | String | 是   | 学生ID                     |
| course_id     | Number | 是   | 课程ID                     |
| assignment_id | Number | 否   | 作业ID（可为null）         |
| exam_id       | Number | 否   | 考试ID（可为null）         |
| score         | Number | 是   | 分数                       |
| feedback      | String | 否   | 反馈评语                   |
| grade_type    | String | 否   | 成绩类型（默认：assignment）|

**请求示例**:

```json
{
  "student_id": "2023004",
  "course_id": 1,
  "assignment_id": 1,
  "score": 92,
  "feedback": "作业完成很好，逻辑清晰",
  "grade_type": "assignment"
}
```

**响应示例**:

```json
{
  "success": true,
  "data": {
    "id": 8,
    "student_id": "2023004",
    "course_id": 1,
    "assignment_id": 1,
    "score": 92,
    "feedback": "作业完成很好，逻辑清晰",
    "grade_type": "assignment",
    "created_at": "2023-04-01T08:00:00.000Z",
    "updated_at": "2023-04-01T08:00:00.000Z"
  },
  "message": "创建成绩成功"
}
```

### 7. 更新成绩

更新现有的成绩信息。

**请求方法**: `PUT`

**URL**: `/api/grades/:id`

**路径参数**:

| 参数名 | 类型   | 必填 | 描述   |
|--------|--------|------|--------|
| id     | Number | 是   | 成绩ID |

**请求体**:

| 字段名        | 类型   | 必填 | 描述                      |
|---------------|--------|------|---------------------------|
| student_id    | String | 否   | 学生ID                    |
| course_id     | Number | 否   | 课程ID                    |
| assignment_id | Number | 否   | 作业ID                    |
| exam_id       | Number | 否   | 考试ID                    |
| score         | Number | 否   | 分数                      |
| feedback      | String | 否   | 反馈评语                  |
| grade_type    | String | 否   | 成绩类型                  |

**请求示例**:

```json
{
  "score": 88,
  "feedback": "基础语句掌握良好，高级查询有进步"
}
```

**响应示例**:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "student_id": "2023001",
    "course_id": 1,
    "assignment_id": 1,
    "score": 88,
    "feedback": "基础语句掌握良好，高级查询有进步",
    "grade_type": "assignment",
    "created_at": "2023-03-09T10:30:00.000Z",
    "updated_at": "2023-04-01T08:00:00.000Z"
  },
  "message": "更新成绩成功"
}
```

### 8. 删除成绩

删除指定的成绩记录。

**请求方法**: `DELETE`

**URL**: `/api/grades/:id`

**路径参数**:

| 参数名 | 类型   | 必填 | 描述   |
|--------|--------|------|--------|
| id     | Number | 是   | 成绩ID |

**响应示例**:

```json
{
  "success": true,
  "message": "删除成绩成功"
}
```

### 9. 搜索成绩

根据关键词搜索成绩。

**请求方法**: `GET`

**URL**: `/api/grades/search`

**查询参数**:

| 参数名 | 类型   | 必填 | 描述       |
|--------|--------|------|------------|
| query  | String | 是   | 搜索关键词 |

**响应示例**:

```json
{
  "success": true,
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
      "submission_date": "2023-03-09T10:30:00.000Z",
      "created_at": "2023-03-09T10:30:00.000Z",
      "updated_at": "2023-03-09T10:30:00.000Z"
    },
    // 更多匹配的成绩...
  ],
  "message": "搜索成绩成功"
}
```

### 10. 获取课程统计信息

获取指定课程的成绩统计信息。

**请求方法**: `GET`

**URL**: `/api/grades/stats/course/:courseId`

**路径参数**:

| 参数名    | 类型   | 必填 | 描述   |
|-----------|--------|------|--------|
| courseId  | Number | 是   | 课程ID |

**响应示例**:

```json
{
  "success": true,
  "data": {
    "stats": {
      "average_score": 83.8,
      "max_score": 92,
      "min_score": 76,
      "total_grades": 5
    },
    "assignments": [
      {
        "id": 1,
        "title": "SQL基础练习",
        "average_score": 85.5,
        "submissions": 3
      },
      {
        "id": 2,
        "title": "E-R图设计",
        "average_score": 82.0,
        "submissions": 2
      }
      // 更多作业统计...
    ]
  },
  "message": "获取课程统计信息成功"
}
```

## 错误响应

当请求出现错误时，会返回以下格式的响应：

```json
{
  "success": false,
  "message": "错误信息",
  "error": {
    "code": "ERROR_CODE",
    "details": "详细错误信息"
  }
}
```

**常见错误代码**:

| 错误代码          | 描述                           | HTTP状态码 |
|-------------------|--------------------------------|------------|
| INVALID_INPUT     | 输入数据无效                   | 400        |
| GRADE_NOT_FOUND   | 成绩不存在                     | 404        |
| SERVER_ERROR      | 服务器内部错误                 | 500        | 