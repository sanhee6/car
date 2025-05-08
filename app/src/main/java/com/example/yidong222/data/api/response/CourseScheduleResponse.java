package com.example.yidong222.data.api.response;

import android.util.Log;
import com.example.yidong222.models.CourseSchedule;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CourseScheduleResponse {
    private static final String TAG = "CourseScheduleResponse";
    private static final Gson gson = new Gson();

    private String status;
    private String message;
    @SerializedName("data")
    private Object data;
    @SerializedName("single_data")
    private CourseSchedule singleData;
    private Pagination pagination;

    public static class Pagination {
        private int total;
        private int page;
        private int limit;
        @SerializedName("totalPages")
        private int totalPages;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CourseSchedule> getData() {
        List<CourseSchedule> result = new ArrayList<>();
        if (data == null) {
            return result;
        }

        if (data instanceof List) {
            List<?> dataList = (List<?>) data;
            for (Object item : dataList) {
                if (item instanceof CourseSchedule) {
                    result.add((CourseSchedule) item);
                } else if (item instanceof Map) {
                    CourseSchedule course = convertMapToCourseSchedule((Map<String, Object>) item);
                    if (course != null) {
                        result.add(course);
                    }
                }
            }
        }
        return result;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public CourseSchedule getSingleData() {
        return singleData;
    }

    public void setSingleData(CourseSchedule singleData) {
        this.singleData = singleData;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<CourseSchedule> getDataList() {
        return getData();
    }

    private CourseSchedule convertMapToCourseSchedule(Map<String, Object> map) {
        try {
            int id = ((Double) map.getOrDefault("id", 0.0)).intValue();
            String courseName = (String) map.getOrDefault("course_name", "");
            String teacherName = (String) map.getOrDefault("teacher_name", "");
            String classTime = (String) map.getOrDefault("class_time", "");
            String classroom = (String) map.getOrDefault("classroom", "");

            Date createdAt = null;
            Date updatedAt = null;

            if (map.containsKey("created_at") && map.get("created_at") != null) {
                String createdAtStr = (String) map.get("created_at");
                try {
                    createdAt = gson.fromJson("\"" + createdAtStr + "\"", Date.class);
                } catch (Exception e) {
                    Log.e(TAG, "解析created_at日期失败: " + createdAtStr, e);
                }
            }

            if (map.containsKey("updated_at") && map.get("updated_at") != null) {
                String updatedAtStr = (String) map.get("updated_at");
                try {
                    updatedAt = gson.fromJson("\"" + updatedAtStr + "\"", Date.class);
                } catch (Exception e) {
                    Log.e(TAG, "解析updated_at日期失败: " + updatedAtStr, e);
                }
            }

            return new CourseSchedule(id, courseName, teacherName, classTime, classroom, createdAt, updatedAt);
        } catch (Exception e) {
            Log.e(TAG, "转换Map到CourseSchedule失败", e);
            return null;
        }
    }
}