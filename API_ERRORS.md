# API错误说明文档

## 当前存在的API错误

目前应用在调用后端API时遇到了以下错误：

### 1. 作业API错误

**错误信息**：
```
{"status":"fail","message":"获取作业列表失败","error":"Unknown column 'a.due_date' in 'field list'"}
```

**原因**：
服务器端SQL查询中使用了`a.due_date`字段，但数据库表中该字段已更名为`a.deadline`。

**客户端临时解决方案**：
- 已在`AssignmentDto.java`中正确使用`deadline`字段
- 在`AssignmentActivity.java`中添加了模拟数据支持，当API调用失败时显示模拟数据

**服务器端解决方案**：
需要修改服务器端查询，将`a.due_date`替换为`a.deadline`。可能的查询语句如下：

```sql
-- 修改前
SELECT a.id, a.course_id, c.name as course_name, a.title, a.description, a.due_date, a.max_score, a.created_at, a.updated_at
FROM assignments a
LEFT JOIN courses c ON a.course_id = c.id
WHERE ...

-- 修改后
SELECT a.id, a.course_id, c.name as course_name, a.title, a.description, a.deadline, a.max_score, a.created_at, a.updated_at
FROM assignments a
LEFT JOIN courses c ON a.course_id = c.id
WHERE ...
```

### 2. 成绩API错误

**错误信息**：
```
{"status":"fail","message":"获取学生成绩列表失败","error":"Unknown column 'g.feedback' in 'field list'"}
```

**原因**：
服务器端SQL查询中使用了`g.feedback`字段，但数据库表中该字段已被删除或从未添加。

**客户端临时解决方案**：
- 已在`GradeDto.java`中移除了feedback相关字段
- 在`GradeActivity.java`中添加了模拟数据支持，当API调用失败时显示模拟数据

**服务器端解决方案**：
需要修改服务器端查询，移除对`g.feedback`字段的引用。可能的查询语句如下：

```sql
-- 修改前
SELECT g.id, g.student_id, g.course_id, c.name as course_name, g.assignment_id, a.title as assignment_title, 
       g.exam_id, g.score, g.grade_type, g.submission_date, g.feedback, g.created_at, g.updated_at
FROM grades g
LEFT JOIN courses c ON g.course_id = c.id
LEFT JOIN assignments a ON g.assignment_id = a.id
WHERE g.student_id = ?

-- 修改后
SELECT g.id, g.student_id, g.course_id, c.name as course_name, g.assignment_id, a.title as assignment_title, 
       g.exam_id, g.score, g.grade_type, g.submission_date, g.created_at, g.updated_at
FROM grades g
LEFT JOIN courses c ON g.course_id = c.id
LEFT JOIN assignments a ON g.assignment_id = a.id
WHERE g.student_id = ?
```

## 测试状态

目前客户端已添加了临时解决方案，可以在API调用失败时显示模拟数据，确保用户体验。但要彻底解决问题，需要对服务器端进行上述修改。

## 后续步骤

1. 联系后端开发团队，提供错误信息和建议的SQL修改
2. 等待后端API修复
3. 修复后，测试API正常工作
4. 移除临时的模拟数据逻辑（可选） 