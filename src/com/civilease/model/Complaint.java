package com.civilease.model;
/***
 * 민원 글 하나의 정보를 담는 바구니 입니다. 민원번호, 제목, 내용, 작성자 아이디/이름, 카테고리, 상태, 답변
 */
import java.sql.Timestamp;

public class Complaint {
    private int complaintId; // 민원 번호
    private String title; // 제목
    private String content; // 내용
    private String studentId; // 아이디
    private String studentName; // 추가
    private Department department; // 소속
    private Category category; // 카테고리
    private ComplaintStatus status; // 상태
    private String adminReply; // 답변
    private String attachmentPath; // 경로
    private Timestamp createdAt; // 타임스탬프

    public Complaint() {}

    // Getters and Setters
    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }
    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }
    public String getAttachmentPath() { return attachmentPath; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
