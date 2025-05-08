# 作业模块API文档

## 基础信息

- 基础路径: `/api/assignments`
- 数据格式: JSON
- 认证方式: 无（可根据需要添加）

## API端点

### 1. 获取所有作业

获取系统中的所有作业信息，支持分页。

**请求方法**: `GET`

**URL**: `/api/assignments`

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
        "course_id": 1,
        "course_name": "数据库原理",
        "title": "SQL基础练习",
        "description": "SQL基本语句练习，完成教材P45-P50的习题",
        "due_date": "2023-03-10T23:59:59.000Z",
        "max_score": 100,
        "created_at": "2023-03-01T08:00:00.000Z",
        "updated_at": "2023-03-01T08:00:00.000Z"
      },
      // 更多作业记录...
    ],
    "pagination": {
      "total": 5,
      "page": 1,
      "limit": 10,
      "totalPages": 1
    }
  },
  "message": "获取作业列表成功"
}
```

### 2. 获取指定作业

根据ID获取单个作业的详细信息。

**请求方法**: `GET`

**URL**: `/api/assignments/:id`

**路径参数**:

| 参数名 | 类型   | 必填 | 描述    |
|--------|--------|------|---------|
| id     | Number | 是   | 作业ID  |

**响应示例**:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "course_id": 1,
    "course_name": "数据库原理",
    "title": "SQL基础练习",
    "description": "SQL基本语句练习，完成教材P45-P50的习题",
    "due_date": "2023-03-10T23:59:59.000Z",
    "max_score": 100,
    "created_at": "2023-03-01T08:00:00.000Z",
    "updated_at": "2023-03-01T08:00:00.000Z"
  },
  "message": "获取作业详情成功"
}
```

### 3. 按课程获取作业

获取指定课程的所有作业列表。

**请求方法**: `GET`

**URL**: `/api/assignments/course/:courseId`

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
      "course_id": 1,
      "title": "SQL基础练习",
      "description": "SQL基本语句练习，完成教材P45-P50的习题",
      "due_date": "2023-03-10T23:59:59.000Z",
      "max_score": 100,
      "created_at": "2023-03-01T08:00:00.000Z",
      "updated_at": "2023-03-01T08:00:00.000Z"
    },
    {
      "id": 2,
      "course_id": 1,
      "title": "E-R图设计",
      "description": "设计一个图书管理系统的E-R图",
      "due_date": "2023-04-05T23:59:59.000Z",
      "max_score": 100,
      "created_at": "2023-03-01T08:00:00.000Z",
      "updated_at": "2023-03-01T08:00:00.000Z"
    },
    // 更多该课程的作业...
  ],
  "message": "获取课程作业列表成功"
}
```

### 4. 创建作业

创建一个新的作业记录。

**请求方法**: `POST`

**URL**: `/api/assignments`

**请求体**:

| 字段名      | 类型   | 必填 | 描述                     |
|-------------|--------|------|--------------------------|
| course_id   | Number | 是   | 关联的课程ID             |
| title       | String | 是   | 作业标题                 |
| description | String | 否   | 作业描述                 |
| due_date    | String | 是   | 截止日期（ISO格式）      |
| max_score   | Number | 是   | 总分值                   |

**请求示例**:

```json
{
  "course_id": 1,
  "title": "数据库索引设计",
  "description": "为图书管理系统设计合适的索引结构",
  "due_date": "2023-05-15T23:59:59.000Z",
  "max_score": 100
}
```

**响应示例**:

```json
{
  "success": true,
  "data": {
    "id": 6,
    "course_id": 1,
    "title": "数据库索引设计",
    "description": "为图书管理系统设计合适的索引结构",
    "due_date": "2023-05-15T23:59:59.000Z",
    "max_score": 100,
    "created_at": "2023-04-01T08:00:00.000Z",
    "updated_at": "2023-04-01T08:00:00.000Z"
  },
  "message": "创建作业成功"
}
```

### 5. 更新作业

更新现有的作业信息。

**请求方法**: `PUT`

**URL**: `/api/assignments/:id`

**路径参数**:

| 参数名 | 类型   | 必填 | 描述   |
|--------|--------|------|--------|
| id     | Number | 是   | 作业ID |

**请求体**:

| 字段名      | 类型   | 必填 | 描述                    |
|-------------|--------|------|-------------------------|
| course_id   | Number | 否   | 关联的课程ID            |
| title       | String | 否   | 作业标题                |
| description | String | 否   | 作业描述                |
| due_date    | String | 否   | 截止日期（ISO格式）     |
| max_score   | Number | 否   | 总分值                  |

**请求示例**:

```json
{
  "title": "SQL基础练习（修订版）",
  "description": "SQL基本语句练习，完成教材P45-P60的习题",
  "due_date": "2023-03-15T23:59:59.000Z"
}
```

**响应示例**:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "course_id": 1,
    "title": "SQL基础练习（修订版）",
    "description": "SQL基本语句练习，完成教材P45-P60的习题",
    "due_date": "2023-03-15T23:59:59.000Z",
    "max_score": 100,
    "created_at": "2023-03-01T08:00:00.000Z",
    "updated_at": "2023-04-01T08:00:00.000Z"
  },
  "message": "更新作业成功"
}
```

### 6. 删除作业

删除指定的作业记录。

**请求方法**: `DELETE`

**URL**: `/api/assignments/:id`

**路径参数**:

| 参数名 | 类型   | 必填 | 描述   |
|--------|--------|------|--------|
| id     | Number | 是   | 作业ID |

**响应示例**:

```json
{
  "success": true,
  "message": "删除作业成功"
}
```

### 7. 搜索作业

根据关键词搜索作业。

**请求方法**: `GET`

**URL**: `/api/assignments/search`

**查询参数**:

| 参数名 | 类型   | 必填 | 描述     |
|--------|--------|------|----------|
| query  | String | 是   | 搜索关键词 |

**响应示例**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "course_id": 1,
      "course_name": "数据库原理",
      "title": "SQL基础练习",
      "description": "SQL基本语句练习，完成教材P45-P50的习题",
      "due_date": "2023-03-10T23:59:59.000Z",
      "max_score": 100,
      "created_at": "2023-03-01T08:00:00.000Z",
      "updated_at": "2023-03-01T08:00:00.000Z"
    },
    // 更多匹配的作业...
  ],
  "message": "搜索作业成功"
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

| 错误代码             | 描述                           | HTTP状态码 |
|----------------------|--------------------------------|------------|
| INVALID_INPUT        | 输入数据无效                   | 400        |
| ASSIGNMENT_NOT_FOUND | 作业不存在                     | 404        |
| SERVER_ERROR         | 服务器内部错误                 | 500        | 