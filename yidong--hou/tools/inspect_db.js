/**
 * 数据库检查工具
 * 用于直接检查表结构和数据
 */
const pool = require('../models/db.js');

async function inspectDatabase() {
  try {
    console.log('开始检查数据库表结构与数据...');
    
    // 检查assignments表结构
    console.log('\n===== 作业表结构 =====');
    const [assignmentColumns] = await pool.query('DESCRIBE assignments');
    
    console.log('作业表字段:');
    assignmentColumns.forEach(col => {
      console.log(`- ${col.Field} (${col.Type})`);
    });
    
    // 检查grades表结构
    console.log('\n===== 成绩表结构 =====');
    const [gradeColumns] = await pool.query('DESCRIBE grades');
    
    console.log('成绩表字段:');
    gradeColumns.forEach(col => {
      console.log(`- ${col.Field} (${col.Type})`);
    });
    
    // 检查作业数据
    console.log('\n===== 作业数据 =====');
    const [assignments] = await pool.query('SELECT * FROM assignments LIMIT 1');
    if (assignments.length > 0) {
      console.log('作业数据样本:');
      console.log(assignments[0]);
    }
    
    // 检查成绩数据
    console.log('\n===== 成绩数据 =====');
    const [grades] = await pool.query('SELECT * FROM grades LIMIT 1');
    if (grades.length > 0) {
      console.log('成绩数据样本:');
      console.log(grades[0]);
    }
    
    // 检查实际API数据处理
    console.log('\n===== API查询测试 =====');
    
    // 测试作业API查询
    const [apiAssignments] = await pool.query(
      `SELECT a.id, a.course_id, c.name AS course_name, a.title, 
      a.description, a.deadline AS due_date, a.total_score AS max_score, a.created_at, a.updated_at
      FROM assignments a
      LEFT JOIN courses c ON a.course_id = c.id
      LIMIT 1`
    );
    
    if (apiAssignments.length > 0) {
      console.log('作业API查询结果样本:');
      console.log(apiAssignments[0]);
      
      // 确认字段映射
      console.log('字段映射检查:');
      console.log('- 包含due_date:', 'due_date' in apiAssignments[0]);
      console.log('- 包含deadline:', 'deadline' in apiAssignments[0]);
      console.log('- 包含max_score:', 'max_score' in apiAssignments[0]);
      console.log('- 包含total_score:', 'total_score' in apiAssignments[0]);
    }
    
    // 测试成绩API查询
    const [apiGrades] = await pool.query(
      `SELECT g.id, g.student_id, g.course_id, g.assignment_id, 
      c.name AS course_name, a.title AS assignment_title,
      g.score, g.comment AS feedback, g.created_at, g.updated_at
      FROM grades g
      LEFT JOIN courses c ON g.course_id = c.id
      LEFT JOIN assignments a ON g.assignment_id = a.id
      LIMIT 1`
    );
    
    if (apiGrades.length > 0) {
      console.log('\n成绩API查询结果样本:');
      console.log(apiGrades[0]);
      
      // 确认字段映射
      console.log('字段映射检查:');
      console.log('- 包含feedback:', 'feedback' in apiGrades[0]);
      console.log('- 包含comment:', 'comment' in apiGrades[0]);
    }
    
    console.log('\n数据库检查完成!');
    process.exit(0);
  } catch (error) {
    console.error('检查过程中出错:', error);
    process.exit(1);
  }
}

// 执行检查
inspectDatabase(); 