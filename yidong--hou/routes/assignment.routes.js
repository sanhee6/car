const express = require('express');
const router = express.Router();
const assignmentController = require('../controllers/assignment.controller.js');
const assignmentValidator = require('../middleware/assignment.validator.js');

/**
 * 作业路由
 */

/**
 * @route GET /api/assignments
 * @description 获取所有作业（支持分页）
 * @query page: int - 当前页码，默认为 1
 * @query limit: int - 每页显示的数量，默认为 10 
 * @access Public
 */
router.get('/', assignmentController.getAssignments);

/**
 * @route GET /api/assignments/search
 * @description 搜索作业
 * @query query: string - 搜索关键词
 * @access Public
 */
router.get('/search', assignmentController.searchAssignments);

/**
 * @route GET /api/assignments/course/:courseId
 * @description 获取指定课程 ID 的所有作业
 * @param courseId: string - 课程 ID
 * @access Public
 */
router.get('/course/:courseId', assignmentValidator.validateCourseId, assignmentController.getAssignmentsByCourseId);

/**
 * @route GET /api/assignments/:id
 * @description 获取指定 ID 的作业信息
 * @param id: string - 作业 ID
 * @access Public
 */
router.get('/:id', assignmentValidator.validateAssignmentId, assignmentController.getAssignmentById);

/**
 * @route POST /api/assignments
 * @description 创建新作业
 * @body course_id: number - 课程ID，必填
 * @body title: string - 作业标题，必填
 * @body description: string - 作业描述，可选
 * @body due_date: date - 截止日期，必填
 * @body max_score: number - 最高分值，必填
 * @access Public
 */
router.post('/', assignmentValidator.validateCreateAssignment, assignmentController.createAssignment);

/**
 * @route PUT /api/assignments/:id
 * @description 更新指定 ID 的作业信息
 * @param id: string - 作业 ID
 * @body course_id: number - 课程ID，可选
 * @body title: string - 作业标题，可选
 * @body description: string - 作业描述，可选
 * @body due_date: date - 截止日期，可选
 * @body max_score: number - 最高分值，可选
 * @access Public
 */
router.put('/:id', assignmentValidator.validateUpdateAssignment, assignmentController.updateAssignment);

/**
 * @route DELETE /api/assignments/:id
 * @description 删除指定 ID 的作业
 * @param id: string - 作业 ID
 * @access Public
 */
router.delete('/:id', assignmentValidator.validateAssignmentId, assignmentController.deleteAssignment);

module.exports = router; 