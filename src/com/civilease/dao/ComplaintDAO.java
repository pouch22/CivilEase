package com.civilease.dao;

import com.civilease.model.*;
import com.civilease.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplaintDAO {

    // 상태별 민원 개수 조회 (통계용)
    public Map<ComplaintStatus, Integer> getComplaintCounts() {
        Map<ComplaintStatus, Integer> counts = new HashMap<>();
        // 모든 상태를 0으로 초기화
        for (ComplaintStatus s : ComplaintStatus.values()) {
            counts.put(s, 0);
        }

        String sql = "SELECT status, COUNT(*) as cnt FROM complaints GROUP BY status";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                try {
                    counts.put(ComplaintStatus.valueOf(rs.getString("status")), rs.getInt("cnt"));
                } catch (IllegalArgumentException e) {
                    // DB에 정의되지 않은 상태값이 있을 경우 무시
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counts;
    }

    // 민원 등록
    public boolean insertComplaint(Complaint complaint) {
        String sql = "INSERT INTO complaints (title, content, student_id, department, category, status, attachment_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, complaint.getTitle());
            pstmt.setString(2, complaint.getContent());
            pstmt.setString(3, complaint.getStudentId());
            pstmt.setString(4, complaint.getDepartment().name());
            pstmt.setString(5, complaint.getCategory().name());
            pstmt.setString(6, complaint.getStatus().name());
            pstmt.setString(7, complaint.getAttachmentPath());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 학생별 민원 조회
    public List<Complaint> getComplaintsByStudent(String studentId) {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE student_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToComplaint(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 관리자용 전체 민원 조회 (정렬 및 필터링 가능하도록 확장 가능)
    public List<Complaint> getAllComplaints(String statusFilter, String categoryFilter, String sortOrder) {
        List<Complaint> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT c.*, u.name as student_name FROM complaints c JOIN users u ON c.student_id = u.user_id WHERE 1=1");
        
        if (statusFilter != null && !statusFilter.isEmpty()) sql.append(" AND c.status = '").append(statusFilter).append("'");
        if (categoryFilter != null && !categoryFilter.isEmpty()) sql.append(" AND c.category = '").append(categoryFilter).append("'");
        
        if ("OLD".equals(sortOrder)) {
            sql.append(" ORDER BY c.created_at ASC");
        } else {
            sql.append(" ORDER BY c.created_at DESC");
        }

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql.toString())) {
            
            while (rs.next()) {
                list.add(mapResultSetToComplaint(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 민원 상태 및 답변 업데이트
    public boolean updateComplaint(int complaintId, ComplaintStatus status, String reply) {
        String sql = "UPDATE complaints SET status = ?, admin_reply = ? WHERE complaint_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status.name());
            pstmt.setString(2, reply);
            pstmt.setInt(3, complaintId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 민원 제목 및 내용 수정 (보완 필요 시)
    public boolean updateComplaintContent(int complaintId, String title, String content, String attachmentPath) {
        String sql = "UPDATE complaints SET title = ?, content = ?, attachment_path = ? WHERE complaint_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setString(3, attachmentPath);
            pstmt.setInt(4, complaintId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 민원 삭제 (취소)
    public boolean deleteComplaint(int complaintId) {
        String sql = "DELETE FROM complaints WHERE complaint_id = ? AND status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, complaintId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ResultSet을 Complaint 객체로 매핑하는 헬퍼 메소드
    private Complaint mapResultSetToComplaint(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setComplaintId(rs.getInt("complaint_id"));
        c.setTitle(rs.getString("title"));
        c.setContent(rs.getString("content"));
        c.setStudentId(rs.getString("student_id"));
        
        // 학생 이름 (JOIN 쿼리 시에만 존재)
        try {
            c.setStudentName(rs.getString("student_name"));
        } catch (SQLException e) {
            // 컬럼이 없는 경우 무시
        }
        
        c.setDepartment(Department.valueOf(rs.getString("department")));
        c.setCategory(Category.valueOf(rs.getString("category")));
        c.setStatus(ComplaintStatus.valueOf(rs.getString("status")));
        c.setAdminReply(rs.getString("admin_reply"));
        c.setAttachmentPath(rs.getString("attachment_path"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
