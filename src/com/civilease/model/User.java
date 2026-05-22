package com.civilease.model;
/***
 * 사용자 한 명의 정보를 담는 부분
 * 아이디, 비밀번호, 이름, 소속, 권한 정보를 저장할 수 있고 이 정보들은 private으로 저장되어 있기에 getter와 setter를 활용해 정보를 넣고 뺀다.
 */
public class User {
    private String userId;
    private String password;
    private String name;
    private Department department;
    private UserRole role;

    public User() {}

    public User(String userId, String password, String name, Department department, UserRole role) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.department = department;
        this.role = role;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
