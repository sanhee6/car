/**
 * API集成测试工具
 * 模拟客户端请求并验证API响应
 */
const axios = require('axios');

// 配置
const API_BASE_URL = 'http://localhost:3000/api';
const STUDENT_ID = '2023001';

// 客户端请求头
const CLIENT_HEADERS = {
  'User-Agent': 'okhttp/4.9.0',
  'Accept': 'application/json',
  'X-Client-Version': 'Android/1.0.0'
};

/**
 * 测试作业API
 */
async function testAssignmentsAPI() {
  try {
    console.log('\n======= 测试作业API =======');
    const response = await axios.get(`${API_BASE_URL}/assignments?page=1&limit=50`, {
      headers: CLIENT_HEADERS
    });
    
    console.log(`状态码: ${response.status}`);
    
    const assignments = response.data.data || [];
    console.log(`返回作业数量: ${assignments.length}`);
    
    if (assignments.length > 0) {
      const assignment = assignments[0];
      
      // 验证字段名是否符合前端要求
      const hasCorrectFields = assignment.due_date && assignment.max_score && 
                              !assignment.deadline && !assignment.total_score;
      
      console.log('\n字段名验证:');
      console.log(`- due_date字段存在: ${!!assignment.due_date}`);
      console.log(`- deadline字段存在: ${!!assignment.deadline}`);
      console.log(`- max_score字段存在: ${!!assignment.max_score}`);
      console.log(`- total_score字段存在: ${!!assignment.total_score}`);
      console.log(`- 字段名正确: ${hasCorrectFields ? '通过✅' : '失败❌'}`);
      
      // 显示样本数据
      console.log('\n作业数据示例:');
      console.log(assignment);
    }
    
    return true;
  } catch (error) {
    console.error('作业API测试失败:', error.message);
    if (error.response) {
      console.error(`状态码: ${error.response.status}`);
      console.error(error.response.data);
    }
    return false;
  }
}

/**
 * 测试成绩API
 */
async function testGradesAPI() {
  try {
    console.log('\n======= 测试成绩API =======');
    const response = await axios.get(`${API_BASE_URL}/grades/student/${STUDENT_ID}`, {
      headers: CLIENT_HEADERS
    });
    
    console.log(`状态码: ${response.status}`);
    
    const grades = response.data.data || [];
    console.log(`返回成绩数量: ${grades.length}`);
    
    if (grades.length > 0) {
      const grade = grades[0];
      
      // 验证字段名是否符合前端要求
      const hasCorrectFields = grade.feedback && !grade.comment;
      
      console.log('\n字段名验证:');
      console.log(`- feedback字段存在: ${!!grade.feedback}`);
      console.log(`- comment字段存在: ${!!grade.comment}`);
      console.log(`- 字段名正确: ${hasCorrectFields ? '通过✅' : '失败❌'}`);
      
      // 验证作业关联
      const assignmentRelated = grades.filter(g => g.assignment_id !== null).length > 0;
      console.log(`\n作业关联: ${assignmentRelated ? '通过✅' : '失败❌'}`);
      console.log(`- 有 ${grades.filter(g => g.assignment_id !== null).length} 条成绩与作业关联`);
      
      // 显示样本数据
      console.log('\n成绩数据示例:');
      console.log(grade);
    }
    
    return true;
  } catch (error) {
    console.error('成绩API测试失败:', error.message);
    if (error.response) {
      console.error(`状态码: ${error.response.status}`);
      console.error(error.response.data);
    }
    return false;
  }
}

/**
 * 运行所有测试
 */
async function runTests() {
  try {
    console.log('======= 开始API集成测试 =======');
    
    const assignmentsTestResult = await testAssignmentsAPI();
    const gradesTestResult = await testGradesAPI();
    
    console.log('\n======= 测试摘要 =======');
    console.log(`作业API测试: ${assignmentsTestResult ? '通过✅' : '失败❌'}`);
    console.log(`成绩API测试: ${gradesTestResult ? '通过✅' : '失败❌'}`);
    console.log(`整体结果: ${assignmentsTestResult && gradesTestResult ? '通过✅' : '失败❌'}`);
    
    if (!assignmentsTestResult || !gradesTestResult) {
      console.log('\n推荐修复:');
      if (!assignmentsTestResult) {
        console.log('- 确保作业API返回due_date而不是deadline');
        console.log('- 确保作业API返回max_score而不是total_score');
      }
      if (!gradesTestResult) {
        console.log('- 确保成绩API返回feedback而不是comment');
        console.log('- 检查成绩与作业的关联关系');
      }
    }
    
  } catch (error) {
    console.error('测试执行过程中出错:', error);
  }
}

// 启动测试
runTests(); 