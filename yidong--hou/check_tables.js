const pool = require('./models/db.js');

async function checkTables() {
  try {
    // 检查表是否存在
    const [tables] = await pool.query('SHOW TABLES FROM coursedb');
    console.log('Tables in coursedb:');
    console.log(tables.map(row => Object.values(row)[0]));
    
    // 检查作业表结构
    const [assignmentColumns] = await pool.query('SHOW COLUMNS FROM assignments');
    console.log('\nAssignments table structure:');
    console.log(assignmentColumns.map(col => `${col.Field} (${col.Type})`));
    
    // 检查考试表结构
    const [examColumns] = await pool.query('SHOW COLUMNS FROM exams');
    console.log('\nExams table structure:');
    console.log(examColumns.map(col => `${col.Field} (${col.Type})`));
    
    // 检查成绩表结构
    const [gradeColumns] = await pool.query('SHOW COLUMNS FROM grades');
    console.log('\nGrades table structure:');
    console.log(gradeColumns.map(col => `${col.Field} (${col.Type})`));
    
    process.exit(0);
  } catch (err) {
    console.error('Error:', err.message);
    process.exit(1);
  }
}

checkTables(); 