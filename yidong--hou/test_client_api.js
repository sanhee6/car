const axios = require('axios');

/**
 * 模拟Android客户端测试API响应
 */
async function testClientApi() {
  try {
    console.log('开始模拟Android客户端测试API...');
    
    // 基础URL
    const baseUrl = 'http://localhost:3000/api';
    
    // 测试作业API - 模拟Android客户端请求头
    console.log('\n测试作业API (Android客户端)...');
    const assignmentsResponse = await axios.get(`${baseUrl}/assignments?page=1&limit=50`, {
      headers: {
        'User-Agent': 'okhttp/4.9.0',
        'Accept': 'application/json',
        'X-Client-Version': 'Android/1.0.0'
      }
    });
    
    console.log(`状态码: ${assignmentsResponse.status}`);
    const assignments = assignmentsResponse.data.data || [];
    console.log(`返回 ${assignments.length} 条作业记录`);
    
    if (assignments.length > 0) {
      // 检查字段结构和名称
      const firstAssignment = assignments[0];
      console.log('\n作业API响应结构（客户端视角）:');
      const keys = Object.keys(firstAssignment);
      console.log(keys);
      
      // 特别检查字段名称
      console.log('\n关键字段检查:');
      console.log('- due_date字段存在:', keys.includes('due_date'));
      console.log('- deadline字段存在:', keys.includes('deadline'));
      console.log('- max_score字段存在:', keys.includes('max_score'));
      console.log('- total_score字段存在:', keys.includes('total_score'));
      
      // 显示一条作业记录
      console.log('\n作业示例数据:');
      console.log(JSON.stringify(firstAssignment, null, 2));
    }
    
    // 测试成绩API - 模拟Android客户端请求头
    console.log('\n测试成绩API (Android客户端)...');
    const gradesResponse = await axios.get(`${baseUrl}/grades/student/2023001`, {
      headers: {
        'User-Agent': 'okhttp/4.9.0',
        'Accept': 'application/json',
        'X-Client-Version': 'Android/1.0.0'
      }
    });
    
    console.log(`状态码: ${gradesResponse.status}`);
    const grades = gradesResponse.data.data || [];
    console.log(`返回 ${grades.length} 条成绩记录`);
    
    if (grades.length > 0) {
      // 检查字段结构和名称
      const firstGrade = grades[0];
      console.log('\n成绩API响应结构（客户端视角）:');
      const keys = Object.keys(firstGrade);
      console.log(keys);
      
      // 特别检查字段名称
      console.log('\n关键字段检查:');
      console.log('- feedback字段存在:', keys.includes('feedback'));
      console.log('- comment字段存在:', keys.includes('comment'));
      
      // 显示一条成绩记录
      console.log('\n成绩示例数据:');
      console.log(JSON.stringify(firstGrade, null, 2));
    }
    
    console.log('\n客户端API测试完成！');
  } catch (error) {
    console.error('测试过程中出错:', error.message);
    if (error.response) {
      console.error('错误状态码:', error.response.status);
      console.error('错误消息:', error.response.data);
    }
  }
}

// 执行测试函数
testClientApi(); 