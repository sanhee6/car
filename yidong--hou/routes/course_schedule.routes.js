const express = require('express');
const router = express.Router();
const courseScheduleController = require('../controllers/course_schedule.controller.js');
const validator = require('../middleware/validator.js');

/**
 * 课程表路由
 */

/**
 * @route GET /api/course_schedules
 * @description 获取所有课程表（支持分页）
 * @query page: int - 当前页码，默认为 1
 * @query limit: int - 每页显示的数量，默认为 10 
 * @access Public
 */
router.get('/', courseScheduleController.getCourseSchedules);

/**
 * @route POST /api/course_schedules
 * @description 创建新课程表
 * @body course_name: string - 课程名称，必填
 * @body teacher_name: string - 授课教师姓名，必填
 * @body class_time: string - 上课时间，必填
 * @body classroom: string - 教室，必填
 * @access Public
 */
router.post('/', validator.validateCreateCourseSchedule, courseScheduleController.createCourseSchedule);

/**
 * @route PUT /api/course_schedules/:id
 * @description 更新指定 ID 的课程表信息
 * @param id: string - 课程表 ID
 * @body course_name: string - 课程名称，可选
 * @body teacher_name: string - 授课教师姓名，可选
 * @body class_time: string - 上课时间，可选
 * @body classroom: string - 教室，可选
 * @access Public
 */
router.put('/:id', validator.validateUpdateCourseSchedule, courseScheduleController.updateCourseSchedule);

/**
 * @route DELETE /api/course_schedules/:id
 * @description 删除指定 ID 的课程表
 * @param id: string - 课程表 ID
 * @access Public
 */
router.delete('/:id', validator.validateCourseScheduleId, courseScheduleController.deleteCourseSchedule);

module.exports = router; 