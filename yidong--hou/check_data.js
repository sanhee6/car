const pool = require('./models/db.js');

async function checkData() {
  try {
    // 检查作业数据
    const [assignments] = await pool.query('SELECT * FROM assignments');
    console.log('作业数据:');
    console.log(assignments);
    console.log(`共 ${assignments.length} 条作业记录\n`);
    
    // 检查成绩数据
    const [grades] = await pool.query('SELECT * FROM grades');
    console.log('成绩数据:');
    console.log(grades);
    console.log(`共 ${grades.length} 条成绩记录\n`);
    
    // 检查成绩和作业的关联
    if (grades.length > 0 && assignments.length > 0) {
      const [linked] = await pool.query(`
        SELECT g.id, g.student_id, g.assignment_id, a.title 
        FROM grades g
        LEFT JOIN assignments a ON g.assignment_id = a.id
        WHERE g.assignment_id IS NOT NULL
      `);
      console.log('成绩与作业关联:');
      console.log(linked);
      console.log(`共 ${linked.length} 条关联记录`);
    }
    
    process.exit(0);
  } catch (err) {
    console.error('Error:', err.message);
    process.exit(1);
  }
}

checkData(); 