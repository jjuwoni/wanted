package com.smartlearning.lms.repository;

import com.smartlearning.lms.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
