const pool = require('./models/db.js');

/**
 * 更新成绩数据，建立与作业的关联
 */
async function updateGrades() {
  try {
    console.log('开始更新成绩表数据...');
    
    // 获取所有作业信息
    const [assignments] = await pool.query('SELECT id, course_id, title FROM assignments');
    console.log(`找到 ${assignments.length} 条作业记录`);
    
    // 获取所有成绩信息
    const [grades] = await pool.query('SELECT id, course_id, grade_type FROM grades WHERE grade_type = "assignment" AND assignment_id IS NULL');
    console.log(`找到 ${grades.length} 条需要关联的成绩记录`);
    
    // 更新每个成绩记录，关联到对应课程的作业
    let updatedCount = 0;
    for (const grade of grades) {
      // 查找该课程下的作业
      const courseAssignments = assignments.filter(a => a.course_id === grade.course_id);
      
      if (courseAssignments.length > 0) {
        // 选择第一个匹配课程的作业进行关联
        const assignment = courseAssignments[0];
        
        // 更新成绩记录的assignment_id
        const [result] = await pool.query(
          'UPDATE grades SET assignment_id = ? WHERE id = ?',
          [assignment.id, grade.id]
        );
        
        if (result.affectedRows > 0) {
          console.log(`成功更新成绩ID: ${grade.id}，关联到作业: "${assignment.title}" (ID: ${assignment.id})`);
          updatedCount++;
        }
      }
    }
    
    console.log(`更新完成！共更新 ${updatedCount} 条成绩记录。`);

    // 检查更新后的关联情况
    const [linked] = await pool.query(`
      SELECT g.id, g.student_id, g.assignment_id, a.title as assignment_title
      FROM grades g
      LEFT JOIN assignments a ON g.assignment_id = a.id
      WHERE g.assignment_id IS NOT NULL
    `);
    
    console.log('\n更新后的关联情况:');
    console.log(linked);
    console.log(`共有 ${linked.length} 条成绩记录与作业关联`);
    
    process.exit(0);
  } catch (error) {
    console.error('更新过程中出错:', error.message);
    process.exit(1);
  }
}

// 执行更新函数
updateGrades(); 