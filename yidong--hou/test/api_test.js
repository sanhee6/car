const fetch = require('node-fetch');

/**
 * 课程表API测试脚本
 * 测试所有课程表相关的API接口
 */

// API基础URL
const BASE_URL = 'http://localhost:3000/api';

// 异步函数包装器，用于简化错误处理
const asyncWrapper = async (callback) => {
  try {
    await callback();
  } catch (error) {
    console.error('测试执行出错:', error);
  }
};

// 打印响应结果
const printResponse = (title, response) => {
  console.log(`\n--------- ${title} ---------`);
  console.log('状态码:', response.status);
  console.log('数据:', JSON.stringify(response.data, null, 2));
  console.log('---------------------------\n');
};

// 测试获取所有课程表
const testGetAllCourseSchedules = async () => {
  await asyncWrapper(async () => {
    console.log('测试: 获取所有课程表');
    
    const response = await fetch(`${BASE_URL}/course_schedules`);
    const data = await response.json();
    
    printResponse('GET /api/course_schedules', { 
      status: response.status,
      data
    });
  });
};

// 测试搜索课程表
const testSearchCourseSchedules = async (query) => {
  await asyncWrapper(async () => {
    console.log(`测试: 搜索课程表 (关键词: ${query})`);
    
    const response = await fetch(`${BASE_URL}/course_schedules/search?query=${encodeURIComponent(query)}`);
    const data = await response.json();
    
    printResponse(`GET /api/course_schedules/search?query=${query}`, { 
      status: response.status,
      data
    });
  });
};

// 测试创建课程表
const testCreateCourseSchedule = async () => {
  await asyncWrapper(async () => {
    console.log('测试: 创建课程表');
    
    const newCourse = {
      course_name: '测试课程',
      teacher_name: '测试教师',
      class_time: '星期3 第1-2节',
      classroom: 'A101',
      start_week: 1,
      end_week: 16,
      semester_id: '2023-2'
    };
    
    const response = await fetch(`${BASE_URL}/course_schedules`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(newCourse)
    });
    
    const data = await response.json();
    
    printResponse('POST /api/course_schedules', { 
      status: response.status,
      data
    });
    
    // 保存创建的课程ID，用于后续测试
    if (response.status === 201 && data.status === 'success') {
      return data.data.id;
    }
    return null;
  });
};

// 测试更新课程表
const testUpdateCourseSchedule = async (id) => {
  await asyncWrapper(async () => {
    console.log(`测试: 更新课程表 (ID: ${id})`);
    
    const updateData = {
      course_name: '更新后的课程',
      teacher_name: '更新后的教师',
      class_time: '星期5 第3-4节',
      classroom: 'B202'
    };
    
    const response = await fetch(`${BASE_URL}/course_schedules/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(updateData)
    });
    
    const data = await response.json();
    
    printResponse(`PUT /api/course_schedules/${id}`, { 
      status: response.status,
      data
    });
  });
};

// 测试删除课程表
const testDeleteCourseSchedule = async (id) => {
  await asyncWrapper(async () => {
    console.log(`测试: 删除课程表 (ID: ${id})`);
    
    const response = await fetch(`${BASE_URL}/course_schedules/${id}`, {
      method: 'DELETE'
    });
    
    const data = await response.json();
    
    printResponse(`DELETE /api/course_schedules/${id}`, { 
      status: response.status,
      data
    });
  });
};

// 运行所有测试
const runTests = async () => {
  console.log('开始测试课程表API...\n');
  
  // 测试获取所有课程表
  await testGetAllCourseSchedules();
  
  // 测试搜索课程表
  await testSearchCourseSchedules('数据库');
  
  // 测试创建课程表
  const newCourseId = await testCreateCourseSchedule();
  
  if (newCourseId) {
    // 测试更新课程表
    await testUpdateCourseSchedule(newCourseId);
    
    // 测试删除课程表
    await testDeleteCourseSchedule(newCourseId);
  }
  
  console.log('课程表API测试完成');
};

// 执行测试
runTests(); 