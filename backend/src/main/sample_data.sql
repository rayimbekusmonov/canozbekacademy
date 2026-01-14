-- ====================================
-- SAMPLE DATA FOR CANOZBEK ACADEMY
-- ====================================

-- Clean existing data (optional - use carefully!)
-- TRUNCATE TABLE homework_submissions, assignments, lesson_progress, lessons, course_sections, enrollments, reviews, orders, courses, users CASCADE;

-- ==================== USERS ====================

-- Password for all users: password123
-- BCrypt hash: $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LZWvKxDnNqJ3p5CQ6

INSERT INTO users (email, password_hash, full_name, phone, role, is_active, email_verified, created_at, updated_at) VALUES
-- Students
('student1@test.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LZWvKxDnNqJ3p5CQ6', 'Aziza Karimova', '+998901234567', 'STUDENT', true, true, NOW(), NOW()),
('student2@test.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LZWvKxDnNqJ3p5CQ6', 'Jasur Toshmatov', '+998901234568', 'STUDENT', true, true, NOW(), NOW()),
('student3@test.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LZWvKxDnNqJ3p5CQ6', 'Dilnoza Raximova', '+998901234569', 'STUDENT', true, false, NOW(), NOW()),

-- Teachers
('teacher1@test.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LZWvKxDnNqJ3p5CQ6', 'Mehmet YÄ±lmaz', '+998901234570', 'TEACHER', true, true, NOW(), NOW()),
('teacher2@test.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LZWvKxDnNqJ3p5CQ6', 'AyÅŸe Demir', '+998901234571', 'TEACHER', true, true, NOW(), NOW()),

-- Admin
('admin@test.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LZWvKxDnNqJ3p5CQ6', 'Admin User', '+998901234572', 'ADMIN', true, true, NOW(), NOW());

-- ==================== COURSES ====================

INSERT INTO courses (title, slug, description, category, level, language, price, discount_price, duration_weeks, total_lessons, teacher_id, rating, total_students, total_reviews, is_published, created_at, updated_at) VALUES
                                                                                                                                                                                                                             ('Turkish for Beginners', 'turkish-for-beginners', 'Learn Turkish from scratch with our comprehensive beginner course. Master basic grammar, vocabulary, and conversation skills.', 'Turkish Language', 'BEGINNER', 'Turkish', 99000, 79000, 8, 24, 4, 4.8, 150, 45, true, NOW(), NOW()),
                                                                                                                                                                                                                             ('Advanced Turkish Grammar', 'advanced-turkish-grammar', 'Deep dive into complex Turkish grammar structures. Perfect for intermediate to advanced learners.', 'Turkish Language', 'ADVANCED', 'Turkish', 149000, 129000, 10, 32, 4, 4.9, 85, 28, true, NOW(), NOW()),
                                                                                                                                                                                                                             ('Turkish Conversation Practice', 'turkish-conversation-practice', 'Improve your speaking skills with real-life conversation scenarios and interactive exercises.', 'Turkish Language', 'INTERMEDIATE', 'Turkish', 79000, NULL, 6, 18, 5, 4.7, 220, 67, true, NOW(), NOW()),
                                                                                                                                                                                                                             ('Business Turkish', 'business-turkish', 'Learn professional Turkish for business contexts. Includes email writing, presentations, and negotiations.', 'Turkish Language', 'INTERMEDIATE', 'Turkish', 129000, 109000, 12, 36, 5, 4.6, 65, 22, true, NOW(), NOW());

-- ==================== COURSE SECTIONS ====================

-- Turkish for Beginners - Sections
INSERT INTO course_sections (course_id, title, description, order_index, created_at) VALUES
                                                                                         (1, 'Getting Started', 'Introduction to Turkish language and alphabet', 1, NOW()),
                                                                                         (1, 'Basic Grammar', 'Essential grammar rules and sentence structure', 2, NOW()),
                                                                                         (1, 'Daily Conversations', 'Common phrases and everyday dialogues', 3, NOW());

-- Advanced Turkish Grammar - Sections
INSERT INTO course_sections (course_id, title, description, order_index, created_at) VALUES
                                                                                         (2, 'Complex Sentence Structures', 'Advanced grammar patterns', 1, NOW()),
                                                                                         (2, 'Formal Turkish', 'Professional and formal language usage', 2, NOW());

-- ==================== LESSONS ====================

-- Turkish for Beginners - Getting Started
INSERT INTO lessons (section_id, title, description, video_url, video_duration, order_index, is_preview, created_at, updated_at) VALUES
                                                                                                                                     (1, 'Introduction to Turkish Alphabet', 'Learn the Turkish alphabet and pronunciation basics', 'https://example.com/video1.mp4', 734, 1, true, NOW(), NOW()),
                                                                                                                                     (1, 'Basic Greetings', 'Common greetings and introductions', 'https://example.com/video2.mp4', 920, 2, true, NOW(), NOW()),
                                                                                                                                     (1, 'Numbers 1-100', 'Learn to count in Turkish', 'https://example.com/video3.mp4', 645, 3, false, NOW(), NOW()),

-- Turkish for Beginners - Basic Grammar
INSERT INTO lessons (section_id, title, description, video_url, video_duration, order_index, is_preview, created_at, updated_at) VALUES
    (2, 'Personal Pronouns', 'Understanding Turkish pronouns', 'https://example.com/video4.mp4', 1110, 1, false, NOW(), NOW()),
    (2, 'Present Tense', 'How to form present tense in Turkish', 'https://example.com/video5.mp4', 1215, 2, false, NOW(), NOW()),
    (2, 'Question Forms', 'Asking questions in Turkish', 'https://example.com/video6.mp4', 890, 3, false, NOW(), NOW()),

-- Turkish for Beginners - Daily Conversations
INSERT INTO lessons (section_id, title, description, video_url, video_duration, order_index, is_preview, created_at, updated_at) VALUES
    (3, 'At the Restaurant', 'Ordering food and drinks', 'https://example.com/video7.mp4', 1050, 1, false, NOW(), NOW()),
    (3, 'Shopping Basics', 'Buying items and asking for prices', 'https://example.com/video8.mp4', 980, 2, false, NOW(), NOW()),
    (3, 'Giving Directions', 'Asking for and giving directions', 'https://example.com/video9.mp4', 1120, 3, false, NOW(), NOW());

-- ==================== ASSIGNMENTS ====================

INSERT INTO assignments (lesson_id, title, description, max_score, due_days, created_at) VALUES
                                                                                             (1, 'Alphabet Practice', 'Write all Turkish letters with example words. Submit a video of yourself pronouncing each letter.', 100, 7, NOW()),
                                                                                             (3, 'Count to 100', 'Record yourself counting from 1 to 100 in Turkish.', 100, 5, NOW()),
                                                                                             (5, 'Present Tense Exercise', 'Complete the worksheet on present tense conjugation. Write 10 sentences using different verbs.', 100, 7, NOW()),
                                                                                             (7, 'Restaurant Dialogue', 'Create a dialogue between a waiter and customer. Record yourself playing both roles.', 100, 7, NOW());

-- ==================== ENROLLMENTS ====================

INSERT INTO enrollments (user_id, course_id, progress_percentage, enrolled_at) VALUES
                                                                                   (1, 1, 45.50, NOW() - INTERVAL '15 days'),
                                                                                   (1, 3, 12.00, NOW() - INTERVAL '5 days'),
                                                                                   (2, 1, 78.25, NOW() - INTERVAL '30 days'),
                                                                                   (2, 2, 25.00, NOW() - INTERVAL '10 days'),
                                                                                   (3, 1, 5.00, NOW() - INTERVAL '2 days');

-- ==================== LESSON PROGRESS ====================

-- Student 1 progress in Course 1
INSERT INTO lesson_progress (user_id, lesson_id, is_completed, video_progress, completed_at, last_watched_at) VALUES
                                                                                                                  (1, 1, true, 734, NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days'),
                                                                                                                  (1, 2, true, 920, NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days'),
                                                                                                                  (1, 3, true, 645, NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days'),
                                                                                                                  (1, 4, false, 450, NULL, NOW() - INTERVAL '1 day');

-- Student 2 progress in Course 1
INSERT INTO lesson_progress (user_id, lesson_id, is_completed, video_progress, completed_at, last_watched_at) VALUES
                                                                                                                  (2, 1, true, 734, NOW() - INTERVAL '28 days', NOW() - INTERVAL '28 days'),
                                                                                                                  (2, 2, true, 920, NOW() - INTERVAL '27 days', NOW() - INTERVAL '27 days'),
                                                                                                                  (2, 3, true, 645, NOW() - INTERVAL '26 days', NOW() - INTERVAL '26 days'),
                                                                                                                  (2, 4, true, 1110, NOW() - INTERVAL '25 days', NOW() - INTERVAL '25 days'),
                                                                                                                  (2, 5, true, 1215, NOW() - INTERVAL '24 days', NOW() - INTERVAL '24 days'),
                                                                                                                  (2, 6, true, 890, NOW() - INTERVAL '23 days', NOW() - INTERVAL '23 days'),
                                                                                                                  (2, 7, false, 650, NULL, NOW() - INTERVAL '2 days');

-- ==================== HOMEWORK SUBMISSIONS ====================

INSERT INTO homework_submissions (assignment_id, user_id, content, submitted_at, graded_at, score, feedback, status) VALUES
                                                                                                                         (1, 1, 'I have completed the alphabet practice. Here is my video link: https://example.com/my-video.mp4', NOW() - INTERVAL '13 days', NOW() - INTERVAL '12 days', 95, 'Excellent work! Your pronunciation is very good. Just pay attention to the "Ä°" sound.', 'GRADED'),
                                                                                                                         (1, 2, 'Completed alphabet practice. Video: https://example.com/video2.mp4', NOW() - INTERVAL '27 days', NOW() - INTERVAL '26 days', 88, 'Good job! Keep practicing the soft G sound.', 'GRADED'),
                                                                                                                         (2, 1, 'Video of counting 1-100: https://example.com/count.mp4', NOW() - INTERVAL '11 days', NOW() - INTERVAL '10 days', 100, 'Perfect! You got all numbers correct.', 'GRADED'),
                                                                                                                         (3, 2, 'Present tense homework attached. 10 sentences completed.', NOW() - INTERVAL '20 days', NULL, NULL, NULL, 'SUBMITTED');

-- ==================== REVIEWS ====================

INSERT INTO reviews (course_id, user_id, rating, comment, created_at) VALUES
                                                                          (1, 1, 5, 'Amazing course! The instructor explains everything very clearly. I learned so much in just a few weeks.', NOW() - INTERVAL '10 days'),
                                                                          (1, 2, 5, 'Best Turkish course I''ve taken. Great structure and engaging content.', NOW() - INTERVAL '5 days'),
                                                                          (3, 1, 4, 'Good conversation practice. Would love more real-life scenarios.', NOW() - INTERVAL '3 days');

-- ==================== ORDERS ====================

INSERT INTO orders (user_id, course_id, amount, payment_method, payment_status, transaction_id, created_at, paid_at) VALUES
                                                                                                                         (1, 1, 79000, 'CLICK', 'COMPLETED', 'CLK-2024-0001', NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days'),
                                                                                                                         (1, 3, 79000, 'PAYME', 'COMPLETED', 'PAY-2024-0001', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
                                                                                                                         (2, 1, 79000, 'CLICK', 'COMPLETED', 'CLK-2024-0002', NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days'),
                                                                                                                         (2, 2, 129000, 'PAYME', 'COMPLETED', 'PAY-2024-0002', NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'),
                                                                                                                         (3, 1, 79000, 'CLICK', 'PENDING', 'CLK-2024-0003', NOW() - INTERVAL '2 days', NULL);

-- ==================== VERIFICATION ====================

-- Verify data
SELECT 'Users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'Courses', COUNT(*) FROM courses
UNION ALL
SELECT 'Course Sections', COUNT(*) FROM course_sections
UNION ALL
SELECT 'Lessons', COUNT(*) FROM lessons
UNION ALL
SELECT 'Assignments', COUNT(*) FROM assignments
UNION ALL
SELECT 'Enrollments', COUNT(*) FROM enrollments
UNION ALL
SELECT 'Lesson Progress', COUNT(*) FROM lesson_progress
UNION ALL
SELECT 'Homework Submissions', COUNT(*) FROM homework_submissions
UNION ALL
SELECT 'Reviews', COUNT(*) FROM reviews
UNION ALL
SELECT 'Orders', COUNT(*) FROM orders;

-- Show sample data
SELECT
    u.full_name as student,
    c.title as course,
    e.progress_percentage,
    e.enrolled_at
FROM enrollments e
         JOIN users u ON e.user_id = u.id
         JOIN courses c ON e.course_id = c.id
ORDER BY e.enrolled_at DESC;