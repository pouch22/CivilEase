-- CivilEase Database Schema

CREATE DATABASE IF NOT EXISTS civilease_db;
USE civilease_db;

-- 1. 사용자 테이블 (학생 및 관리자)
CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(20) PRIMARY KEY,       -- 학번 또는 관리자 ID
    password VARCHAR(100) NOT NULL,        -- 생년월일 또는 비밀번호
    name VARCHAR(50) NOT NULL,             -- 이름
    role ENUM('STUDENT', 'ADMIN') NOT NULL -- 역할 구분
);

-- 2. 민원 테이블
CREATE TABLE IF NOT EXISTS complaints (
    complaint_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    student_id VARCHAR(20),
    department ENUM('PLSOFT', 'SIMCOM') NOT NULL,
    category ENUM('SCHOLARSHIP', 'FACILITY', 'GRADUATE', 'OTHERS') NOT NULL,
    status ENUM('PENDING', 'RECEIVED', 'PROCESSING', 'SUPPLEMENT', 'COMPLETED', 'REJECTED') DEFAULT 'PENDING',
    admin_reply TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(user_id)
);

-- 초기 관리자 계정 생성 (ID: admin, PW: admin123)
-- 실무에서는 비밀번호를 암호화해야 하지만, 초기 개발 단계에서는 평문으로 예시를 듭니다.
INSERT INTO users (user_id, password, name, role) 
VALUES ('admin', 'admin123', 'System Admin', 'ADMIN')
ON DUPLICATE KEY UPDATE user_id=user_id;

-- 테스트용 학생 계정 (학번: 20240001, PW: 020505)
INSERT INTO users (user_id, password, name, role) 
VALUES ('20240001', '020505', '홍길동', 'STUDENT')
ON DUPLICATE KEY UPDATE user_id=user_id;
