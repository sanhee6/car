const fetch = require('node-fetch');

// API基础URL
const BASE_URL = 'http://localhost:3000/api';

// 测试获取课程表列表
async function testGetCourseSchedules() {
  try {
    console.log('测试: 获取课程表列表');
    const response = await fetch(`${BASE_URL}/course_schedules`);
    const data = await response.json();
    console.log('状态码:', response.status);
    console.log('响应数据:', JSON.stringify(data, null, 2));
    return data;
  } catch (error) {
    console.error('测试失败:', error);
  }
}

// 测试创建课程表
async function testCreateCourseSchedule() {
  try {
    console.log('\n测试: 创建课程表');
    const newCourse = {
      course_name: "测试课程",
      teacher_name: "测试教授",
      class_time: "星期四 第5-6节",
      classroom: "测试教室"
    };
    
    const response = await fetch(`${BASE_URL}/course_schedules`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(newCourse)
    });
    
    const data = await response.json();
    console.log('状态码:', response.status);
    console.log('响应数据:', JSON.stringify(data, null, 2));
    return data.data;
  } catch (error) {
    console.error('测试失败:', error);
  }
}

// 测试更新课程表
async function testUpdateCourseSchedule(id) {
  try {
    console.log(`\n测试: 更新ID为${id}的课程表`);
    const updateData = {
      course_name: "更新后的课程",
      teacher_name: "更新后的教授"
    };
    
    const response = await fetch(`${BASE_URL}/course_schedules/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(updateData)
    });
    
    const data = await response.json();
    console.log('状态码:', response.status);
    console.log('响应数据:', JSON.stringify(data, null, 2));
    return data;
  } catch (error) {
    console.error('测试失败:', error);
  }
}

// 测试删除课程表
async function testDeleteCourseSchedule(id) {
  try {
    console.log(`\n测试: 删除ID为${id}的课程表`);
    const response = await fetch(`${BASE_URL}/course_schedules/${id}`, {
      method: 'DELETE'
    });
    
    const data = await response.json();
    console.log('状态码:', response.status);
    console.log('响应数据:', JSON.stringify(data, null, 2));
    return data;
  } catch (error) {
    console.error('测试失败:', error);
  }
}

// 运行所有测试
async function runTests() {
  // 获取课程表列表
  await testGetCourseSchedules();
  
  // 创建新课程表
  const createdCourse = await testCreateCourseSchedule();
  
  if (createdCourse && createdCourse.id) {
    // 更新刚创建的课程表
    await testUpdateCourseSchedule(createdCourse.id);
    
    // 删除刚创建的课程表
    await testDeleteCourseSchedule(createdCourse.id);
  }
  
  // 测试完成后再次获取列表，确认更改
  await testGetCourseSchedules();
  
  console.log('\n所有测试完成!');
}

// 执行测试
runTests(); 