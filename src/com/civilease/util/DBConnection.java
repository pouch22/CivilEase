package com.civilease.util;
/***
 * 자바와 MySQL을 연결해주는 클래스
 * DB에 접근 할때마다 DBConnection.getConnection()을 호출하면 설정된 주소, 아이디, 비번으로 DB에 접속할 Connection 객체를 반환한다.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // MySQL 연결 정보
    private static final String URL = "jdbc:mysql://localhost:3306/civilease_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // 설치 시 설정한 root 비밀번호를 입력하세요.

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // JDBC 드라이버 로드
        	// JDBC는 자바에서 DB에 접속하고 실행하고 그 결과들을 받는 표준 API이다. 자바와 DB사이를 잇는 표준을 제공한다
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 연결 객체 생성
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("DB 연결 성공!");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 드라이버를 찾을 수 없습니다: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("DB 연결 실패: " + e.getMessage());
        }
        return conn;
    }

    // 연결 테스트용 메인 메소드
    public static void main(String[] args) {
        getConnection();
    }
}
