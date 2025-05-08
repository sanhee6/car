const express = require('express');
const router = express.Router();
const courseController = require('../controllers/course.controller.js');
const validator = require('../middleware/course.validator.js');

/**
 * 课程路由
 */

/**
 * @route GET /api/courses
 * @description 获取所有课程（支持分页）
 * @query page: int - 当前页码，默认为 1
 * @query limit: int - 每页显示的数量，默认为 10 
 * @access Public
 */
router.get('/', courseController.getCourses);

/**
 * @route GET /api/courses/:id
 * @description 获取指定ID的课程
 * @param id: string - 课程ID
 * @access Public
 */
router.get('/:id', validator.validateCourseId, courseController.getCourseById);

/**
 * @route POST /api/courses
 * @description 创建新课程
 * @body name: string - 课程名称，必填
 * @body teacher: string - 授课教师姓名，必填
 * @body classroom: string - 教室，可选
 * @body weekday: int - 周几(1-7)，必填
 * @body start_section: int - 开始节次，必填
 * @body end_section: int - 结束节次，必填
 * @body start_week: int - 开始周次，必填
 * @body end_week: int - 结束周次，必填
 * @body semester_id: string - 学期标识，必填
 * @access Public
 */
router.post('/', validator.validateCreateCourse, courseController.createCourse);

/**
 * @route PUT /api/courses/:id
 * @description 更新指定ID的课程
 * @param id: string - 课程ID
 * @body name: string - 课程名称，可选
 * @body teacher: string - 授课教师姓名，可选
 * @body classroom: string - 教室，可选
 * @body weekday: int - 周几(1-7)，可选
 * @body start_section: int - 开始节次，可选
 * @body end_section: int - 结束节次，可选
 * @body start_week: int - 开始周次，可选
 * @body end_week: int - 结束周次，可选
 * @body semester_id: string - 学期标识，可选
 * @access Public
 */
router.put('/:id', validator.validateUpdateCourse, courseController.updateCourse);

/**
 * @route DELETE /api/courses/:id
 * @description 删除指定ID的课程
 * @param id: string - 课程ID
 * @access Public
 */
router.delete('/:id', validator.validateCourseId, courseController.deleteCourse);

module.exports = router; 