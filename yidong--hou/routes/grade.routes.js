const express = require('express');
const router = express.Router();
const gradeController = require('../controllers/grade.controller.js');
const gradeValidator = require('../middleware/grade.validator.js');

/**
 * 成绩路由
 */

/**
 * @route GET /api/grades
 * @description 获取所有成绩（支持分页）
 * @query page: int - 当前页码，默认为 1
 * @query limit: int - 每页显示的数量，默认为 10 
 * @access Public
 */
router.get('/', gradeController.getGrades);

/**
 * @route GET /api/grades/search
 * @description 搜索成绩
 * @query query: string - 搜索关键词
 * @access Public
 */
router.get('/search', gradeController.searchGrades);

/**
 * @route GET /api/grades/course/:courseId
 * @description 获取指定课程 ID 的所有成绩
 * @param courseId: string - 课程 ID
 * @access Public
 */
router.get('/course/:courseId', gradeValidator.validateCourseId, gradeController.getGradesByCourseId);

/**
 * @route GET /api/grades/course/:courseId/stats
 * @description 获取指定课程 ID 的统计信息
 * @param courseId: string - 课程 ID
 * @access Public
 */
router.get('/course/:courseId/stats', gradeValidator.validateCourseId, gradeController.getCourseStats);

/**
 * @route GET /api/grades/student/:studentId
 * @description 获取指定学生 ID 的所有成绩
 * @param studentId: string - 学生 ID
 * @access Public
 */
router.get('/student/:studentId', gradeValidator.validateStudentId, gradeController.getGradesByStudentId);

/**
 * @route GET /api/grades/assignment/:assignmentId
 * @description 获取指定作业 ID 的所有成绩
 * @param assignmentId: string - 作业 ID
 * @access Public
 */
router.get('/assignment/:assignmentId', gradeValidator.validateAssignmentId, gradeController.getGradesByAssignmentId);

/**
 * @route GET /api/grades/:id
 * @description 获取指定 ID 的成绩信息
 * @param id: string - 成绩 ID
 * @access Public
 */
router.get('/:id', gradeValidator.validateGradeId, gradeController.getGradeById);

/**
 * @route POST /api/grades
 * @description 创建新成绩
 * @body student_id: number - 学生ID，必填
 * @body course_id: number - 课程ID，必填
 * @body assignment_id: number - 作业ID，必填
 * @body score: number - 分数，必填
 * @body feedback: string - 反馈，可选
 * @body submission_date: date - 提交日期，可选，默认为当前时间
 * @access Public
 */
router.post('/', gradeValidator.validateCreateGrade, gradeController.createGrade);

/**
 * @route PUT /api/grades/:id
 * @description 更新指定 ID 的成绩信息
 * @param id: string - 成绩 ID
 * @body student_id: number - 学生ID，可选
 * @body course_id: number - 课程ID，可选
 * @body assignment_id: number - 作业ID，可选
 * @body score: number - 分数，可选
 * @body feedback: string - 反馈，可选
 * @body submission_date: date - 提交日期，可选
 * @access Public
 */
router.put('/:id', gradeValidator.validateUpdateGrade, gradeController.updateGrade);

/**
 * @route DELETE /api/grades/:id
 * @description 删除指定 ID 的成绩
 * @param id: string - 成绩 ID
 * @access Public
 */
router.delete('/:id', gradeValidator.validateGradeId, gradeController.deleteGrade);

module.exports = router; 