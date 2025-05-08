# 考试模块API文档

## 基础信息

- 基础路径: `/api/exams`
- 数据格式: JSON
- 认证方式: 无（可根据需要添加）

## API端点

### 1. 获取所有考试

获取系统中的所有考试信息，支持分页。

**请求方法**: `GET`

**URL**: `/api/exams`

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
        "title": "数据库原理期中考试",
        "exam_date": "2023-04-15T09:00:00.000Z",
        "duration": 120,
        "location": "A101",
        "description": "闭卷考试，请带好学生证",
        "created_at": "2023-03-01T08:00:00.000Z",
        "updated_at": "2023-03-01T08:00:00.000Z"
      },
      // 更多考试记录...
    ],
    "pagination": {
      "total": 4,
      "page": 1,
      "limit": 10,
      "totalPages": 1
    }
  },
  "message": "获取考试列表成功"
}
```

### 2. 获取指定考试

根据ID获取单个考试的详细信息。

**请求方法**: `GET`

**URL**: `/api/exams/:id`

**路径参数**:

| 参数名 | 类型   | 必填 | 描述    |
|--------|--------|------|---------|
| id     | Number | 是   | 考试ID  |

**响应示例**:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "course_id": 1,
    "course_name": "数据库原理",
    "title": "数据库原理期中考试",
    "exam_date": "2023-04-15T09:00:00.000Z",
    "duration": 120,
    "location": "A101",
    "description": "闭卷考试，请带好学生证",
    "created_at": "2023-03-01T08:00:00.000Z",
    "updated_at": "2023-03-01T08:00:00.000Z"
  },
  "message": "获取考试详情成功"
}
```

### 3. 按课程获取考试

获取指定课程的所有考试列表。

**请求方法**: `GET`

**URL**: `/api/exams/course/:courseId`

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
      "title": "数据库原理期中考试",
      "exam_date": "2023-04-15T09:00:00.000Z",
      "duration": 120,
      "location": "A101",
      "description": "闭卷考试，请带好学生证",
      "created_at": "2023-03-01T08:00:00.000Z",
      "updated_at": "2023-03-01T08:00:00.000Z"
    },
    {
      "id": 2,
      "course_id": 1,
      "title": "数据库原理期末考试",
      "exam_date": "2023-06-20T14:00:00.000Z",
      "duration": 150,
      "location": "A102",
      "description": "闭卷考试，请带好学生证和2B铅笔",
      "created_at": "2023-03-01T08:00:00.000Z",
      "updated_at": "2023-03-01T08:00:00.000Z"
    }
  ],
  "message": "获取课程考试列表成功"
}
```

### 4. 创建考试

创建一个新的考试记录。

**请求方法**: `POST`

**URL**: `/api/exams`

**请求体**:

| 字段名      | 类型   | 必填 | 描述                     |
|-------------|--------|------|--------------------------|
| course_id   | Number | 是   | 关联的课程ID             |
| title       | String | 是   | 考试标题                 |
| exam_date   | String | 是   | 考试日期时间（ISO格式）  |
| duration    | Number | 是   | 考试时长（分钟）         |
| location    | String | 是   | 考试地点                 |
| description | String | 否   | 考试说明                 |

**请求示例**:

```json
{
  "course_id": 1,
  "title": "数据库原理复习考试",
  "exam_date": "2023-05-10T10:00:00.000Z",
  "duration": 90,
  "location": "A103",
  "description": "开卷考试，可带教材"
}
```

**响应示例**:

```json
{
  "success": true,
  "data": {
    "id": 5,
    "course_id": 1,
    "title": "数据库原理复习考试",
    "exam_date": "2023-05-10T10:00:00.000Z",
    "duration": 90,
    "location": "A103",
    "description": "开卷考试，可带教材",
    "created_at": "2023-04-01T08:00:00.000Z",
    "updated_at": "2023-04-01T08:00:00.000Z"
  },
  "message": "创建考试成功"
}
```

### 5. 更新考试

更新现有的考试信息。

**请求方法**: `PUT`

**URL**: `/api/exams/:id`

**路径参数**:

| 参数名 | 类型   | 必填 | 描述   |
|--------|--------|------|--------|
| id     | Number | 是   | 考试ID |

**请求体**:

| 字段名      | 类型   | 必填 | 描述                    |
|-------------|--------|------|-------------------------|
| course_id   | Number | 否   | 关联的课程ID            |
| title       | String | 否   | 考试标题                |
| exam_date   | String | 否   | 考试日期时间（ISO格式） |
| duration    | Number | 否   | 考试时长（分钟）        |
| location    | String | 否   | 考试地点                |
| description | String | 否   | 考试说明                |

**请求示例**:

```json
{
  "title": "数据库原理期中考试（调整）",
  "exam_date": "2023-04-20T09:00:00.000Z",
  "location": "A105"
}
```

**响应示例**:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "course_id": 1,
    "title": "数据库原理期中考试（调整）",
    "exam_date": "2023-04-20T09:00:00.000Z",
    "duration": 120,
    "location": "A105",
    "description": "闭卷考试，请带好学生证",
    "created_at": "2023-03-01T08:00:00.000Z",
    "updated_at": "2023-04-01T08:00:00.000Z"
  },
  "message": "更新考试成功"
}
```

### 6. 删除考试

删除指定的考试记录。

**请求方法**: `DELETE`

**URL**: `/api/exams/:id`

**路径参数**:

| 参数名 | 类型   | 必填 | 描述   |
|--------|--------|------|--------|
| id     | Number | 是   | 考试ID |

**响应示例**:

```json
{
  "success": true,
  "message": "删除考试成功"
}
```

### 7. 搜索考试

根据关键词搜索考试。

**请求方法**: `GET`

**URL**: `/api/exams/search`

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
      "title": "数据库原理期中考试",
      "exam_date": "2023-04-15T09:00:00.000Z",
      "duration": 120,
      "location": "A101",
      "description": "闭卷考试，请带好学生证",
      "created_at": "2023-03-01T08:00:00.000Z",
      "updated_at": "2023-03-01T08:00:00.000Z"
    },
    // 更多匹配的考试...
  ],
  "message": "搜索考试成功"
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

| 错误代码            | 描述                           | HTTP状态码 |
|---------------------|--------------------------------|------------|
| INVALID_INPUT       | 输入数据无效                   | 400        |
| EXAM_NOT_FOUND      | 考试不存在                     | 404        |
| SERVER_ERROR        | 服务器内部错误                 | 500        | 