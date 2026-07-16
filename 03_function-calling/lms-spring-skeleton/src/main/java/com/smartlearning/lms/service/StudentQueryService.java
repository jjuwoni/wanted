package com.smartlearning.lms.service;

import com.smartlearning.lms.domain.Assignment;
import com.smartlearning.lms.domain.Enrollment;
import com.smartlearning.lms.domain.Student;
import com.smartlearning.lms.dto.StudentDtos.*;
import com.smartlearning.lms.repository.AssignmentRepository;
import com.smartlearning.lms.repository.EnrollmentRepository;
import com.smartlearning.lms.repository.StudentRepository;
import com.smartlearning.lms.repository.SubmissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * FastAPI(AI 서버)가 Function Calling으로 조회해 갈 데이터를 만드는 서비스.
 *
 * 설계 포인트: 챗봇용이라고 특별한 게 없다! 평범한 조회 서비스다.
 * "AI 연동"의 실체는 (잘 만든) 일반 REST API를 제공하는 것 —
 * 지금까지 배운 Spring 실력이 그대로 AI 프로젝트의 실력이 된다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentQueryService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;

    private Student getStudentOrThrow(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "학생을 찾을 수 없습니다: " + studentId));
    }

    /** 수강 목록 + 진도율 (챗봇: "내가 뭐 듣고 있지?") */
    public CourseListResponse getCourses(Long studentId) {
        getStudentOrThrow(studentId);   // 없는 학생이면 404 (계약서 4장)

        List<CourseListResponse.CourseItem> items =
                enrollmentRepository.findAllByStudentIdWithCourse(studentId).stream()
                        .map(e -> new CourseListResponse.CourseItem(
                                e.getCourse().getCode(),
                                e.getCourse().getTitle(),
                                e.getCourse().getCategory(),
                                e.getProgressRate(),
                                e.getScore(),
                                e.getStatus()))
                        .toList();
        return new CourseListResponse(studentId, items);
    }

    /** 마감 임박 과제 + 제출 여부 (챗봇: "이번 주 마감 과제 있어?") */
    public UpcomingAssignmentsResponse getUpcomingAssignments(Long studentId, int days) {
        getStudentOrThrow(studentId);

        LocalDate today = LocalDate.now();
        List<Assignment> upcoming = assignmentRepository
                .findUpcomingForStudent(studentId, today, today.plusDays(days));

        // 이 학생이 제출한 과제 ID 집합을 미리 만들어 두고 대조한다
        Set<Long> submittedIds = submissionRepository.findAllByStudentId(studentId).stream()
                .map(s -> s.getAssignment().getId())
                .collect(Collectors.toSet());

        List<UpcomingAssignmentsResponse.AssignmentItem> items = upcoming.stream()
                .map(a -> new UpcomingAssignmentsResponse.AssignmentItem(
                        a.getCourse().getTitle(),
                        a.getTitle(),
                        a.getDueDate(),
                        submittedIds.contains(a.getId())))
                .toList();
        return new UpcomingAssignmentsResponse(studentId, items);
    }

    /** 학습 요약 (챗봇: "나 잘하고 있어?") */
    public StudentSummaryResponse getSummary(Long studentId) {
        Student student = getStudentOrThrow(studentId);
        List<Enrollment> enrollments =
                enrollmentRepository.findAllByStudentIdWithCourse(studentId);

        double avgProgress = enrollments.stream()
                .mapToInt(Enrollment::getProgressRate).average().orElse(0);
        // 성적이 아직 없는(null) 수강은 평균에서 제외한다
        Double avgScore = enrollments.stream()
                .filter(e -> e.getScore() != null)
                .mapToInt(Enrollment::getScore).average()
                .stream().boxed().findFirst().orElse(null);

        return new StudentSummaryResponse(
                studentId, student.getName(), enrollments.size(),
                Math.round(avgProgress * 10) / 10.0, avgScore);
    }
}
