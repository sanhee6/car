const axios = require('axios');

/**
 * 测试API修复情况
 */
async function testApis() {
  try {
    console.log('开始测试API...');
    
    // 基础URL
    const baseUrl = 'http://localhost:3000/api';
    
    // 测试作业API
    console.log('\n测试作业API...');
    const assignmentsResponse = await axios.get(`${baseUrl}/assignments`);
    console.log(`状态码: ${assignmentsResponse.status}`);
    const assignments = assignmentsResponse.data.data || [];
    console.log(`找到 ${assignments.length} 条作业记录`);
    
    // 检查字段名称是否正确
    if (assignments.length > 0) {
      const assignment = assignments[0];
      console.log('作业字段名称检查:');
      console.log('- due_date字段存在:', assignment.hasOwnProperty('due_date'));
      console.log('- max_score字段存在:', assignment.hasOwnProperty('max_score'));
      console.log('- deadline字段存在:', assignment.hasOwnProperty('deadline'));
      console.log('- total_score字段存在:', assignment.hasOwnProperty('total_score'));
    }
    
    // 测试成绩API
    console.log('\n测试成绩API...');
    const gradesResponse = await axios.get(`${baseUrl}/grades`);
    console.log(`状态码: ${gradesResponse.status}`);
    const grades = gradesResponse.data.data || [];
    console.log(`找到 ${grades.length} 条成绩记录`);
    
    // 检查字段名称是否正确
    if (grades.length > 0) {
      const grade = grades[0];
      console.log('成绩字段名称检查:');
      console.log('- feedback字段存在:', grade.hasOwnProperty('feedback'));
      console.log('- comment字段存在:', grade.hasOwnProperty('comment'));
      console.log('- assignment_id字段存在:', grade.hasOwnProperty('assignment_id'));
      console.log('- assignment_title字段存在:', grade.hasOwnProperty('assignment_title'));
    }
    
    // 检查作业关联
    const linkedGrades = grades.filter(g => g.assignment_id !== null);
    console.log(`\n成绩与作业关联检查: ${linkedGrades.length} 条成绩与作业关联`);
    
    if (linkedGrades.length > 0) {
      console.log('抽样展示一条关联成绩:');
      console.log(linkedGrades[0]);
    }
    
    console.log('\nAPI测试完成！');
  } catch (error) {
    console.error('测试过程中出错:', error.message);
    if (error.response) {
      console.error('错误状态码:', error.response.status);
      console.error('错误消息:', error.response.data);
    }
  }
}

// 执行测试函数
testApis(); 