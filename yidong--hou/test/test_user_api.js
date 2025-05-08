/**
 * 用户API集成测试
 */
const axios = require('axios');

// 配置
const API_BASE_URL = 'http://localhost:3000/api';

// 测试数据
const TEST_USER = {
  username: `testuser_${Date.now()}`,
  password: 'password123'
};

// 存储测试过程中创建的数据
let adminToken = '';
let userId = null;

/**
 * 测试用户认证API
 */
async function testAuthAPI() {
  try {
    console.log('\n======= 测试用户认证API =======');
    
    // 测试登录
    console.log('\n测试管理员登录:');
    const loginResponse = await axios.post(`${API_BASE_URL}/auth/login`, {
      username: 'admin',
      password: 'admin123'
    });
    
    console.log(`状态码: ${loginResponse.status}`);
    console.log(`登录结果: ${loginResponse.data.status}`);
    
    if (loginResponse.data.status === 'success') {
      adminToken = loginResponse.data.data.token;
      console.log('管理员登录成功 ✅');
    } else {
      console.log('管理员登录失败 ❌');
      return false;
    }
    
    // 测试注册
    console.log('\n测试用户注册:');
    const registerResponse = await axios.post(`${API_BASE_URL}/auth/register`, TEST_USER);
    
    console.log(`状态码: ${registerResponse.status}`);
    console.log(`注册结果: ${registerResponse.data.status}`);
    
    if (registerResponse.data.status === 'success') {
      console.log('用户注册成功 ✅');
      return true;
    } else {
      console.log('用户注册失败 ❌');
      return false;
    }
  } catch (error) {
    console.error('认证API测试失败:', error.message);
    if (error.response) {
      console.error(`状态码: ${error.response.status}`);
      console.error(error.response.data);
    }
    return false;
  }
}

/**
 * 测试用户管理API
 */
async function testUserAPI() {
  try {
    console.log('\n======= 测试用户管理API =======');
    
    if (!adminToken) {
      console.log('没有管理员令牌，跳过用户管理API测试');
      return false;
    }
    
    // 测试获取用户列表
    console.log('\n测试获取用户列表:');
    const usersResponse = await axios.get(`${API_BASE_URL}/users`, {
      headers: {
        Authorization: `Bearer ${adminToken}`
      }
    });
    
    console.log(`状态码: ${usersResponse.status}`);
    
    const users = usersResponse.data.data || [];
    console.log(`用户数量: ${users.length}`);
    
    if (users.length > 0) {
      console.log('\n用户列表获取成功 ✅');
      console.log('示例用户数据:');
      console.log(users[0]);
    }
    
    // 测试创建用户
    console.log('\n测试创建用户:');
    const createResponse = await axios.post(`${API_BASE_URL}/users`, {
      username: `created_user_${Date.now()}`,
      password: 'password123',
      is_admin: false
    }, {
      headers: {
        Authorization: `Bearer ${adminToken}`
      }
    });
    
    console.log(`状态码: ${createResponse.status}`);
    console.log(`创建结果: ${createResponse.data.status}`);
    
    if (createResponse.data.status === 'success') {
      userId = createResponse.data.data.id;
      console.log(`创建用户成功 ✅ (ID: ${userId})`);
    } else {
      console.log('创建用户失败 ❌');
      return false;
    }
    
    // 测试获取单个用户
    console.log('\n测试获取单个用户:');
    const userResponse = await axios.get(`${API_BASE_URL}/users/${userId}`, {
      headers: {
        Authorization: `Bearer ${adminToken}`
      }
    });
    
    console.log(`状态码: ${userResponse.status}`);
    console.log(`获取结果: ${userResponse.data.status}`);
    
    if (userResponse.data.status === 'success') {
      console.log('获取单个用户成功 ✅');
      console.log(userResponse.data.data);
    }
    
    // 测试更新用户
    console.log('\n测试更新用户:');
    const updateResponse = await axios.put(`${API_BASE_URL}/users/${userId}`, {
      username: `updated_user_${Date.now()}`
    }, {
      headers: {
        Authorization: `Bearer ${adminToken}`
      }
    });
    
    console.log(`状态码: ${updateResponse.status}`);
    console.log(`更新结果: ${updateResponse.data.status}`);
    
    if (updateResponse.data.status === 'success') {
      console.log('更新用户成功 ✅');
      console.log(updateResponse.data.data);
    }
    
    // 测试删除用户
    console.log('\n测试删除用户:');
    const deleteResponse = await axios.delete(`${API_BASE_URL}/users/${userId}`, {
      headers: {
        Authorization: `Bearer ${adminToken}`
      }
    });
    
    console.log(`状态码: ${deleteResponse.status}`);
    console.log(`删除结果: ${deleteResponse.data.status}`);
    
    if (deleteResponse.data.status === 'success') {
      console.log('删除用户成功 ✅');
    }
    
    return true;
  } catch (error) {
    console.error('用户管理API测试失败:', error.message);
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
    console.log('======= 开始用户API集成测试 =======');
    
    const authTestResult = await testAuthAPI();
    const userTestResult = await testUserAPI();
    
    console.log('\n======= 测试摘要 =======');
    console.log(`认证API测试: ${authTestResult ? '通过✅' : '失败❌'}`);
    console.log(`用户管理API测试: ${userTestResult ? '通过✅' : '失败❌'}`);
    console.log(`整体结果: ${authTestResult && userTestResult ? '通过✅' : '失败❌'}`);
    
  } catch (error) {
    console.error('测试执行过程中出错:', error);
  }
}

// 启动测试
runTests();