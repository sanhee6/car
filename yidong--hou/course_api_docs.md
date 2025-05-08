# 课程管理 API 接口对接文档

以下是课程管理系统API接口的对接文档，帮助前端项目("D:\ruanjain\yidong222")顺利集成后端API。

## 服务器地址

所有请求应发送到后端服务器地址：`http://localhost:3000`

## 课程管理接口

### 1. 获取课程列表 (GET /api/courses)

**请求示例：**
```bash
curl -X GET "http://localhost:3000/api/courses?page=1&limit=10"
```

**参数说明：**
- `page`: 当前页码，默认为1
- `limit`: 每页显示数量，默认为10

**预期返回：**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "name": "数据库原理",
      "teacher": "张教授",
      "classroom": "A101",
      "weekday": 1,
      "start_section": 1,
      "end_section": 2,
      "start_week": 1,
      "end_week": 16,
      "semester_id": "2023-1",
      "created_at": "2025-05-02T15:27:33.000Z",
      "updated_at": "2025-05-02T15:27:33.000Z"
    },
    {
      "id": 2,
      "name": "计算机网络",
      "teacher": "李教授",
      "classroom": "B203",
      "weekday": 2,
      "start_section": 3,
      "end_section": 4,
      "start_week": 1,
      "end_week": 16,
      "semester_id": "2023-1",
      "created_at": "2025-05-02T15:27:33.000Z",
      "updated_at": "2025-05-02T15:27:33.000Z"
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

### 2. 获取课程详情 (GET /api/courses/:id)

**请求示例：**
```bash
curl -X GET "http://localhost:3000/api/courses/1"
```

**参数说明：**
- `id`: 课程ID（路径参数）

**预期返回：**
```json
{
  "status": "success",
  "data": {
    "id": 1,
    "name": "数据库原理",
    "teacher": "张教授",
    "classroom": "A101",
    "weekday": 1,
    "start_section": 1,
    "end_section": 2,
    "start_week": 1,
    "end_week": 16,
    "semester_id": "2023-1",
    "created_at": "2025-05-02T15:27:33.000Z",
    "updated_at": "2025-05-02T15:27:33.000Z"
  }
}
```

### 3. 添加课程 (POST /api/courses)

**请求示例：**
```bash
curl -X POST "http://localhost:3000/api/courses" \
     -H "Content-Type: application/json" \
     -d '{
         "name": "Web前端开发",
         "teacher": "周教授",
         "classroom": "H202",
         "weekday": 3,
         "start_section": 1,
         "end_section": 2,
         "start_week": 1,
         "end_week": 16,
         "semester_id": "2023-1"
     }'
```

**参数说明：**
- `name`: 课程名称，必填
- `teacher`: 授课教师姓名，必填
- `classroom`: 教室，可选
- `weekday`: 周几(1-7)，必填
- `start_section`: 开始节次，必填
- `end_section`: 结束节次，必填
- `start_week`: 开始周次，必填
- `end_week`: 结束周次，必填
- `semester_id`: 学期标识，必填

**预期返回：**
```json
{
  "status": "success",
  "message": "课程创建成功",
  "data": {
    "id": 6,
    "name": "Web前端开发",
    "teacher": "周教授",
    "classroom": "H202",
    "weekday": 3,
    "start_section": 1,
    "end_section": 2,
    "start_week": 1,
    "end_week": 16,
    "semester_id": "2023-1",
    "created_at": "2025-05-02T16:10:25.705Z",
    "updated_at": "2025-05-02T16:10:25.705Z"
  }
}
```

### 4. 更新课程 (PUT /api/courses/:id)

**请求示例：**
```bash
curl -X PUT "http://localhost:3000/api/courses/6" \
     -H "Content-Type: application/json" \
     -d '{
         "name": "高级Web前端开发",
         "classroom": "H305",
         "start_section": 3,
         "end_section": 4
     }'
```

**参数说明：**
- `id`: 课程ID（路径参数）
- `name`: 课程名称，可选
- `teacher`: 授课教师姓名，可选
- `classroom`: 教室，可选
- `weekday`: 周几(1-7)，可选
- `start_section`: 开始节次，可选
- `end_section`: 结束节次，可选
- `start_week`: 开始周次，可选
- `end_week`: 结束周次，可选
- `semester_id`: 学期标识，可选

**预期返回：**
```json
{
  "status": "success",
  "message": "课程更新成功",
  "data": {
    "id": 6,
    "name": "高级Web前端开发",
    "teacher": "周教授",
    "classroom": "H305",
    "weekday": 3,
    "start_section": 3,
    "end_section": 4,
    "start_week": 1,
    "end_week": 16,
    "semester_id": "2023-1",
    "created_at": "2025-05-02T16:10:25.000Z",
    "updated_at": "2025-05-02T16:15:15.000Z"
  }
}
```

### 5. 删除课程 (DELETE /api/courses/:id)

**请求示例：**
```bash
curl -X DELETE "http://localhost:3000/api/courses/6"
```

**参数说明：**
- `id`: 课程ID（路径参数）

**预期返回：**
```json
{
  "status": "success",
  "message": "课程删除成功"
}
```

## 错误处理

所有接口在发生错误时将返回以下格式的响应：

```json
{
  "status": "fail",
  "message": "错误消息",
  "error": "详细错误信息（仅在开发环境）"
}
```

常见错误码：
- `400`: 请求参数错误
- `404`: 资源不存在
- `500`: 服务器内部错误

## 前端集成示例

### JavaScript Fetch API 示例

```javascript
// 获取课程列表
async function getCourses(page = 1, limit = 10) {
  try {
    const response = await fetch(`http://localhost:3000/api/courses?page=${page}&limit=${limit}`);
    return await response.json();
  } catch (error) {
    console.error("获取课程列表失败:", error);
  }
}

// 获取课程详情
async function getCourseById(id) {
  try {
    const response = await fetch(`http://localhost:3000/api/courses/${id}`);
    return await response.json();
  } catch (error) {
    console.error("获取课程详情失败:", error);
  }
}

// 添加课程
async function createCourse(courseData) {
  try {
    const response = await fetch("http://localhost:3000/api/courses", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(courseData)
    });
    return await response.json();
  } catch (error) {
    console.error("创建课程失败:", error);
  }
}

// 更新课程
async function updateCourse(id, updateData) {
  try {
    const response = await fetch(`http://localhost:3000/api/courses/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(updateData)
    });
    return await response.json();
  } catch (error) {
    console.error("更新课程失败:", error);
  }
}

// 删除课程
async function deleteCourse(id) {
  try {
    const response = await fetch(`http://localhost:3000/api/courses/${id}`, {
      method: "DELETE"
    });
    return await response.json();
  } catch (error) {
    console.error("删除课程失败:", error);
  }
}
```

### 使用示例

```javascript
// 获取课程列表
getCourses().then(data => {
  console.log("课程列表:", data);
});

// 获取课程详情
getCourseById(1).then(data => {
  console.log("课程详情:", data);
});

// 创建课程
const newCourse = {
  name: "人机交互",
  teacher: "林教授",
  classroom: "F305",
  weekday: 5,
  start_section: 5,
  end_section: 6,
  start_week: 1,
  end_week: 16,
  semester_id: "2023-1"
};
createCourse(newCourse).then(data => {
  console.log("创建成功:", data);
});

// 更新课程
updateCourse(1, { classroom: "新教室", start_section: 3, end_section: 4 }).then(data => {
  console.log("更新成功:", data);
});

// 删除课程
deleteCourse(1).then(data => {
  console.log("删除成功:", data);
});
```

## 注意事项

1. 确保后端服务器正在运行，可以通过访问 `http://localhost:3000/` 测试服务器是否在线。
2. 所有日期时间字段以ISO 8601格式返回，例如: `2025-05-02T15:27:33.000Z`
3. 所有请求需要正确设置 `Content-Type: application/json` 头部（对于POST和PUT请求）。
4. 前端应妥善处理API可能返回的各种错误情况。
5. 数值字段（如 `weekday`, `start_section` 等）在创建和更新时需要确保为整数，否则将返回验证错误。 