INSERT INTO users (user_id, email, password_hash, name, status, last_login_at, created_at)
VALUES
    (1, 'learner1@example.com', 'hashed-password', 'learner1', 'ACTIVE', NOW(), NOW()),
    (2, 'educator1@example.com', 'hashed-password', 'educator1', 'ACTIVE', NOW(), NOW());

INSERT INTO courses (course_id, author_id, title, description, status)
VALUES
    (1, 2, 'Spring Async Practice', 'Complete enrollment and trigger post-processing.', 'PUBLISHED');

INSERT INTO enrollments (enrollment_id, user_id, course_id, status, enrolled_at, completed_at)
VALUES
    (1, 1, 1, 'ACTIVE', NOW(), NULL);
