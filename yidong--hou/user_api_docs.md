# 课表用户管理系统 API 文档

## 基本信息

- 基础URL: `http://localhost:3000/api`
- 返回格式: JSON
- 认证方式: JWT (JSON Web Token)

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

## 1. 用户认证 API

### 1.1 用户登录

- **URL:** `/auth/login`
- **方法:** `POST`
- **权限:** 公开
- **描述:** 用户登录并获取访问令牌

**请求参数:**

| 参数名 | 类型 | 必填 | 描述 |
| ------ | ---- | ---- | ---- |
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

**请求示例:**

```json
{
  "username": "admin",
  "password": "admin123"
}
```

**成功响应:**

```json
{
  "status": "success",
  "message": "登录成功",
  "data": {
    "id": 1,
    "username": "admin",
    "is_admin": true,
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**错误响应:**

```json
{
  "status": "fail",
  "message": "用户名或密码错误"
}
```

### 1.2 用户注册

- **URL:** `/auth/register`
- **方法:** `POST`
- **权限:** 公开
- **描述:** 注册新用户账号

**请求参数:**

| 参数名 | 类型 | 必填 | 描述 |
| ------ | ---- | ---- | ---- |
| username | string | 是 | 用户名（3-50个字符） |
| password | string | 是 | 密码（至少6个字符） |

**请求示例:**

```json
{
  "username": "newuser",
  "password": "password123"
}
```

**成功响应:**

```json
{
  "status": "success",
  "message": "注册成功",
  "data": {
    "id": 3,
    "username": "newuser",
    "is_admin": false,
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**错误响应:**

```json
{
  "status": "fail",
  "message": "用户名已存在"
}
```

## 2. 用户管理 API

### 2.1 获取用户列表

- **URL:** `/users`
- **方法:** `GET`
- **权限:** 仅管理员
- **描述:** 获取所有用户的分页列表

**请求头:**

```
Authorization: Bearer <token>
```

**查询参数:**

| 参数名 | 类型 | 必填 | 描述 |
| ------ | ---- | ---- | ---- |
| page | integer | 否 | 当前页码，默认为1 |
| limit | integer | 否 | 每页显示数量，默认为10 |

**成功响应:**

```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "username": "admin",
      "is_admin": true,
      "created_at": "2023-06-01T08:30:00.000Z",
      "updated_at": "2023-06-01T08:30:00.000Z"
    },
    {
      "id": 2,
      "username": "student",
      "is_admin": false,
      "created_at": "2023-06-01T09:15:00.000Z",
      "updated_at": "2023-06-01T09:15:00.000Z"
    }
  ],
  "pagination": {
    "total": 2,
    "page": 1,
    "limit": 10,
    "totalPages": 1
  }
}
```

**错误响应:**

```json
{
  "status": "fail",
  "message": "禁止访问，需要管理员权限"
}
```

### 2.2 获取单个用户

- **URL:** `/users/:id`
- **方法:** `GET`
- **权限:** 管理员或用户本人
- **描述:** 获取指定ID的用户信息

**请求头:**

```
Authorization: Bearer <token>
```

**路径参数:**

| 参数名 | 类型 | 必填 | 描述 |
| ------ | ---- | ---- | ---- |
| id | integer | 是 | 用户ID |

**成功响应:**

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "is_admin": true,
    "created_at": "2023-06-01T08:30:00.000Z",
    "updated_at": "2023-06-01T08:30:00.000Z"
  }
}
```

**错误响应:**

```json
{
  "status": "fail",
  "message": "用户不存在"
}
```

### 2.3 创建用户

- **URL:** `/users`
- **方法:** `POST`
- **权限:** 仅管理员
- **描述:** 创建新用户（管理员创建）

**请求头:**

```
Authorization: Bearer <token>
```

**请求参数:**

| 参数名 | 类型 | 必填 | 描述 |
| ------ | ---- | ---- | ---- |
| username | string | 是 | 用户名（3-50个字符） |
| password | string | 是 | 密码（至少6个字符） |
| is_admin | boolean | 是 | 是否为管理员 |

**请求示例:**

```json
{
  "username": "newuser",
  "password": "password123",
  "is_admin": false
}
```

**成功响应:**

```json
{
  "status": "success",
  "message": "用户创建成功",
  "data": {
    "id": 3,
    "username": "newuser",
    "is_admin": false,
    "created_at": "2023-06-15T10:20:00.000Z",
    "updated_at": "2023-06-15T10:20:00.000Z"
  }
}
```

**错误响应:**

```json
{
  "status": "fail",
  "message": "用户名已存在"
}
```

### 2.4 更新用户

- **URL:** `/users/:id`
- **方法:** `PUT`
- **权限:** 管理员或用户本人（用户本人不能修改is_admin字段）
- **描述:** 更新指定ID的用户信息

**请求头:**

```
Authorization: Bearer <token>
```

**路径参数:**

| 参数名 | 类型 | 必填 | 描述 |
| ------ | ---- | ---- | ---- |
| id | integer | 是 | 用户ID |

**请求参数:**

| 参数名 | 类型 | 必填 | 描述 |
| ------ | ---- | ---- | ---- |
| username | string | 否 | 用户名（3-50个字符） |
| password | string | 否 | 密码（至少6个字符） |
| is_admin | boolean | 否 | 是否为管理员（仅管理员可修改） |

**请求示例:**

```json
{
  "username": "updateduser",
  "password": "newpassword"
}
```

**成功响应:**

```json
{
  "status": "success",
  "message": "用户更新成功",
  "data": {
    "id": 3,
    "username": "updateduser",
    "is_admin": false,
    "created_at": "2023-06-15T10:20:00.000Z",
    "updated_at": "2023-06-15T11:30:00.000Z"
  }
}
```

**错误响应:**

```json
{
  "status": "fail",
  "message": "没有权限修改管理员状态"
}
```

### 2.5 删除用户

- **URL:** `/users/:id`
- **方法:** `DELETE`
- **权限:** 仅管理员
- **描述:** 删除指定ID的用户

**请求头:**

```
Authorization: Bearer <token>
```

**路径参数:**

| 参数名 | 类型 | 必填 | 描述 |
| ------ | ---- | ---- | ---- |
| id | integer | 是 | 用户ID |

**成功响应:**

```json
{
  "status": "success",
  "message": "用户删除成功"
}
```

**错误响应:**

```json
{
  "status": "fail",
  "message": "禁止访问，需要管理员权限"
}
```

## 前端API使用示例

### 登录示例

```javascript
async function login(username, password) {
  try {
    const response = await fetch('http://localhost:3000/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username, password })
    });
    
    const data = await response.json();
    
    if (data.status === 'success') {
      // 保存令牌到本地存储
      localStorage.setItem('token', data.data.token);
      localStorage.setItem('user', JSON.stringify(data.data));
      
      // 根据用户类型跳转
      if (data.data.is_admin) {
        // 管理员跳转到管理页面
        window.location.href = '/admin/dashboard';
      } else {
        // 普通用户跳转到课程页面
        window.location.href = '/courses';
      }
    } else {
      alert(data.message);
    }
  } catch (error) {
    console.error('登录失败:', error);
    alert('登录失败，请稍后重试');
  }
}
```

### 注册示例

```javascript
async function register(username, password) {
  try {
    const response = await fetch('http://localhost:3000/api/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username, password })
    });
    
    const data = await response.json();
    
    if (data.status === 'success') {
      // 保存令牌到本地存储
      localStorage.setItem('token', data.data.token);
      localStorage.setItem('user', JSON.stringify(data.data));
      
      // 注册成功跳转到课程页面
      window.location.href = '/courses';
    } else {
      alert(data.message);
    }
  } catch (error) {
    console.error('注册失败:', error);
    alert('注册失败，请稍后重试');
  }
}
```

### 管理员获取用户列表示例

```javascript
async function getUsersList(page = 1, limit = 10) {
  try {
    const token = localStorage.getItem('token');
    
    if (!token) {
      window.location.href = '/login';
      return;
    }
    
    const response = await fetch(`http://localhost:3000/api/users?page=${page}&limit=${limit}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    const data = await response.json();
    
    if (data.status === 'success') {
      // 渲染用户列表
      renderUsersList(data.data, data.pagination);
    } else if (response.status === 401) {
      // 令牌无效，重新登录
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    } else if (response.status === 403) {
      // 权限不足
      alert('没有权限访问此页面');
      window.location.href = '/courses';
    } else {
      alert(data.message);
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
    alert('获取用户列表失败，请稍后重试');
  }
}
```

## 注意事项

1. 所有需要认证的API请求必须在请求头中包含有效的JWT令牌
2. 令牌格式：`Authorization: Bearer <token>`
3. 管理员账户默认为：username=admin, password=admin123
4. 普通用户默认为：username=student, password=student123
5. 用户注册时自动创建为普通用户（非管理员）
6. 只有管理员用户可以创建其他管理员用户
7. 返回格式统一为 { status: 'success'/'fail', ... } 