package com.smartlearning.lms.controller;

import com.smartlearning.lms.dto.StudentDtos.*;
import com.smartlearning.lms.service.StudentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * FastAPI(AI 서버)가 Function Calling 과정에서 호출하는 조회 API.
 * (API 계약서 4장 — 구간 C: FastAPI → Spring)
 *
 * 참고: 이 API를 브라우저가 아닌 'FastAPI 서버'가 호출하므로
 * CORS는 관여하지 않는다 (CorsConfig 주석 참고).
 * 인증도 v1.0 범위 제외 — studentId는 경로로 그대로 받는다.
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentApiController {

    private final StudentQueryService studentQueryService;

    @GetMapping("/{studentId}/courses")
    public CourseListResponse courses(@PathVariable Long studentId) {
        return studentQueryService.getCourses(studentId);
    }

    @GetMapping("/{studentId}/assignments/upcoming")
    public UpcomingAssignmentsResponse upcomingAssignments(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "7") int days) {
        return studentQueryService.getUpcomingAssignments(studentId, days);
    }

    @GetMapping("/{studentId}/summary")
    public StudentSummaryResponse summary(@PathVariable Long studentId) {
        return studentQueryService.getSummary(studentId);
    }
}
