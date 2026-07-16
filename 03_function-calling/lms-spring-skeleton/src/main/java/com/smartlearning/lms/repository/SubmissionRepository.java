package com.smartlearning.lms.repository;

import com.smartlearning.lms.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findAllByStudentId(Long studentId);
}
