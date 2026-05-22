# CivilEase Class Diagram

```mermaid
classDiagram
    class User {
        -String userId
        -String password
        -String name
        -UserRole role
    }

    class Complaint {
        -int complaintId
        -String title
        -String content
        -String studentId
        -Department dept
        -Category category
        -ComplaintStatus status
        -String adminReply
        -Timestamp createdAt
        -Timestamp updatedAt
    }

    class UserRole {
        <<enumeration>>
        STUDENT
        ADMIN
    }

    class Department {
        <<enumeration>>
        PLSOFT
        SIMCOM
    }

    class Category {
        <<enumeration>>
        SCHOLARSHIP
        FACILITY
        GRADUATE
        OTHERS
    }

    class ComplaintStatus {
        <<enumeration>>
        PENDING
        RECEIVED
        PROCESSING
        SUPPLEMENT
        COMPLETED
        REJECTED
    }

    class DBConnection {
        +getConnection() Connection
    }

    class UserDAO {
        +insertUser(User user)
        +getUserById(String userId) User
        +authenticate(String userId, String password) User
    }

    class ComplaintDAO {
        +insertComplaint(Complaint complaint)
        +updateComplaint(Complaint complaint)
        +getComplaintById(int id) Complaint
        +getComplaintsByStudent(String studentId) List
        +getAllComplaints(FilterOptions options) List
    }

    class LoginFrame {
        -JTextField idField
        -JPasswordField pwField
        -JButton loginBtn
        -JButton registerBtn
    }

    class StudentMainFrame {
        -ComplaintTable table
        -JButton createBtn
    }

    class AdminMainFrame {
        -ComplaintTable table
        -JComboBox statusFilter
        -JComboBox categoryFilter
    }

    class ComplaintFormFrame {
        -JTextField titleField
        -JTextArea contentArea
        -JComboBox deptBox
        -JComboBox categoryBox
    }

    class ComplaintDetailFrame {
        -JLabel statusLabel
        -JTextArea replyArea
        -JButton updateBtn
    }

    UserDAO ..> User : handles
    ComplaintDAO ..> Complaint : handles
    UserDAO ..> DBConnection : uses
    ComplaintDAO ..> DBConnection : uses
    
    LoginFrame ..> UserDAO : uses
    StudentMainFrame ..> ComplaintDAO : uses
    AdminMainFrame ..> ComplaintDAO : uses
```

## 패키지 구조 (Package Structure)

1.  **`com.civilease.model`**
    *   `User.java`, `Complaint.java` (DTO)
    *   `UserRole.java`, `Department.java`, `Category.java`, `ComplaintStatus.java` (Enums)
2.  **`com.civilease.dao`**
    *   `UserDAO.java`, `ComplaintDAO.java`
3.  **`com.civilease.view`**
    *   `LoginFrame.java`, `RegisterFrame.java`, `StudentMainFrame.java`, `AdminMainFrame.java`, `ComplaintFormFrame.java`, `ComplaintDetailFrame.java`
4.  **`com.civilease.util`**
    *   `DBConnection.java`
