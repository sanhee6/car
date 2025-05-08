const axios = require('axios');

/**
 * 检查API响应格式
 */
async function checkApiResponse() {
  try {
    console.log('开始检查API响应格式...');
    
    // 基础URL
    const baseUrl = 'http://localhost:3000/api';
    
    // 测试作业API
    console.log('\n作业API响应:');
    const assignmentsResponse = await axios.get(`${baseUrl}/assignments`);
    console.log(`状态码: ${assignmentsResponse.status}`);
    console.log('响应结构:');
    console.log(JSON.stringify(assignmentsResponse.data, null, 2));
    
    // 测试成绩API
    console.log('\n成绩API响应:');
    const gradesResponse = await axios.get(`${baseUrl}/grades`);
    console.log(`状态码: ${gradesResponse.status}`);
    console.log('响应结构:');
    console.log(JSON.stringify(gradesResponse.data, null, 2));
    
  } catch (error) {
    console.error('检查过程中出错:', error.message);
    if (error.response) {
      console.error('错误状态码:', error.response.status);
      console.error('错误消息:', error.response.data);
    }
  }
}

// 执行检查函数
checkApiResponse(); 