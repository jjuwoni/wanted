package com.smartlearning.lms.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * FastAPI에게 제공하는 조회 API의 응답 DTO 모음.
 *
 * record 필드는 camelCase로 쓰지만, application.yml의
 * SNAKE_CASE 전략 덕분에 JSON으로 나갈 때 자동으로 snake_case가 된다.
 *   progressRate → "progress_rate"   (API 계약서 4장의 형식과 일치)
 */
public class StudentDtos {

    /** GET /api/students/{id}/courses 응답 */
    public record CourseListResponse(Long studentId, List<CourseItem> courses) {
        public record CourseItem(String code, String title, String category,
                                 int progressRate, Integer score, String status) {}
    }

    /** GET /api/students/{id}/assignments/upcoming 응답 */
    public record UpcomingAssignmentsResponse(Long studentId, List<AssignmentItem> assignments) {
        public record AssignmentItem(String courseTitle, String title,
                                     LocalDate dueDate, boolean submitted) {}
    }

    /** GET /api/students/{id}/summary 응답 */
    public record StudentSummaryResponse(Long studentId, String studentName,
                                         int courseCount, double avgProgressRate,
                                         Double avgScore) {}
}
