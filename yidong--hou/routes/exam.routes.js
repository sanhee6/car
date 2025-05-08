const express = require('express');
const router = express.Router();
const examController = require('../controllers/exam.controller.js');
const examValidator = require('../middleware/exam.validator.js');

/**
 * 考试路由
 */

/**
 * @route GET /api/exams
 * @description 获取所有考试（支持分页）
 * @query page: int - 当前页码，默认为 1
 * @query limit: int - 每页显示的数量，默认为 10 
 * @access Public
 */
router.get('/', examController.getExams);

/**
 * @route GET /api/exams/search
 * @description 搜索考试
 * @query query: string - 搜索关键词
 * @access Public
 */
router.get('/search', examController.searchExams);

/**
 * @route GET /api/exams/course/:courseId
 * @description 获取指定课程 ID 的所有考试
 * @param courseId: string - 课程 ID
 * @access Public
 */
router.get('/course/:courseId', examValidator.validateCourseId, examController.getExamsByCourseId);

/**
 * @route GET /api/exams/:id
 * @description 获取指定 ID 的考试信息
 * @param id: string - 考试 ID
 * @access Public
 */
router.get('/:id', examValidator.validateExamId, examController.getExamById);

/**
 * @route POST /api/exams
 * @description 创建新考试
 * @body course_id: number - 课程ID，必填
 * @body title: string - 考试标题，必填
 * @body exam_date: date - 考试日期，必填
 * @body duration: number - 考试时长(分钟)，必填
 * @body location: string - 考试地点，必填
 * @body description: string - 考试描述，可选
 * @access Public
 */
router.post('/', examValidator.validateCreateExam, examController.createExam);

/**
 * @route PUT /api/exams/:id
 * @description 更新指定 ID 的考试信息
 * @param id: string - 考试 ID
 * @body course_id: number - 课程ID，可选
 * @body title: string - 考试标题，可选
 * @body exam_date: date - 考试日期，可选
 * @body duration: number - 考试时长(分钟)，可选
 * @body location: string - 考试地点，可选
 * @body description: string - 考试描述，可选
 * @access Public
 */
router.put('/:id', examValidator.validateUpdateExam, examController.updateExam);

/**
 * @route DELETE /api/exams/:id
 * @description 删除指定 ID 的考试
 * @param id: string - 考试 ID
 * @access Public
 */
router.delete('/:id', examValidator.validateExamId, examController.deleteExam);

module.exports = router; 