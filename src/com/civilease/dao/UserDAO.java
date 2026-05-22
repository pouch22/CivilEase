package com.civilease.dao;
/***
 * DAO는 DBConnection 클래스로
 */
import com.civilease.model.Department;
import com.civilease.model.User;
import com.civilease.model.UserRole;
import com.civilease.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    
    // 회원가입 (학생 등록)
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (user_id, password, name, department, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getDepartment() != null ? user.getDepartment().name() : null);
            pstmt.setString(5, user.getRole().name());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 로그인 인증
    public User login(String userId, String password) {
        String sql = "SELECT * FROM users WHERE user_id = ? AND password = ?";
        System.out.println("로그인 시도 - ID: " + userId + ", PW: " + password);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("사용자 발견: " + rs.getString("name"));
                    User user = new User();
                    user.setUserId(rs.getString("user_id"));
                    user.setPassword(rs.getString("password"));
                    user.setName(rs.getString("name"));
                    String deptStr = rs.getString("department");
                    if (deptStr != null) user.setDepartment(Department.valueOf(deptStr));
                    user.setRole(UserRole.valueOf(rs.getString("role")));
                    return user;
                } else {
                    System.out.println("일치하는 사용자가 없습니다.");
                }
            }
        } catch (SQLException e) {
            System.err.println("로그인 쿼리 실행 중 에러 발생:");
            e.printStackTrace();
        }
        return null;
    }

    // 중복 아이디 체크
    public boolean isIdExists(String userId) {
        String sql = "SELECT 1 FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
