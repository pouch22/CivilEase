package com.civilease.view;

import com.civilease.dao.ComplaintDAO;
import com.civilease.model.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ComplaintFormFrame extends JFrame {
    private JTextField titleField;
    private JTextArea contentArea;
    private JComboBox<Department> deptBox;
    private JComboBox<Category> categoryBox;
    private JButton submitBtn;
    private JButton cancelBtn;
    private JButton fileBtn; // 파일 첨부 버튼
    private JLabel fileLabel; // 선택된 파일명 표시
    private File selectedFile; // 선택된 파일 객체
    
    private String studentId; // 현재 로그인한 학생의 학번
    private Department studentDept; // 학생의 소속 학과
    private ComplaintDAO complaintDAO;

    public ComplaintFormFrame(String studentId, Department dept) {
        this.studentId = studentId;
        this.studentDept = dept;
        this.complaintDAO = new ComplaintDAO();
        initUI();
    }

    private void initUI() {
        setTitle("민원 및 건의사항 작성");
        setSize(500, 700); // 높이 약간 키움
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
     // 창 아이콘
        try {
            ImageIcon frameIcon = new ImageIcon(getClass().getResource("/resources/images/ui/KNU_LOGO/background_zero/LOGO_3.png"));    
            setIconImage(frameIcon.getImage());
        } catch (Exception e) {
            System.out.println("없는 파일");
        }

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. 상단 설정 (카테고리 및 파일 첨부)
        JPanel topPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        
        topPanel.add(new JLabel("카테고리:"));
        categoryBox = new JComboBox<>(Category.values());
        topPanel.add(categoryBox);

        fileBtn = new JButton("파일 첨부 (PNG, PDF)");
        fileLabel = new JLabel("선택된 파일 없음");
        topPanel.add(fileBtn);
        topPanel.add(fileLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 2. 중앙 입력 (제목 및 내용)
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        
        titleField = new JTextField();
        titleField.setBorder(BorderFactory.createTitledBorder("제목 (소속: " + studentDept.getName() + ")"));
        centerPanel.add(titleField, BorderLayout.NORTH);

        contentArea = new JTextArea();
        contentArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("내용"));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 3. 하단 버튼
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        submitBtn = new JButton("제출하기");
        cancelBtn = new JButton("취소");
        
        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 이벤트 리스너
        fileBtn.addActionListener(e -> handleFileChoose());
        submitBtn.addActionListener(e -> handleSubmit());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void handleFileChoose() {
        JFileChooser chooser = new JFileChooser();
        int ret = chooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            fileLabel.setText(selectedFile.getName());
        }
    }

    private void handleSubmit() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "제목과 내용을 모두 입력해주세요.");
            return;
        }

        Complaint complaint = new Complaint();
        complaint.setTitle(title);
        complaint.setContent(content);
        complaint.setStudentId(studentId);
        complaint.setDepartment(studentDept);
        complaint.setCategory((Category) categoryBox.getSelectedItem());
        complaint.setStatus(ComplaintStatus.PENDING);

        // 파일 처리
        if (selectedFile != null) {
            try {
                // uploads 폴더 생성
                File uploadDir = new File("uploads");
                if (!uploadDir.exists()) uploadDir.mkdir();

                // 파일 복사 (중복 방지를 위해 앞에 시간을 붙임)
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destFile = new File(uploadDir, fileName);
                java.nio.file.Files.copy(selectedFile.toPath(), destFile.toPath());
                
                complaint.setAttachmentPath(destFile.getPath()); // 상대 경로 저장
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "파일 저장 중 오류가 발생했습니다.");
                return;
            }
        }

        if (complaintDAO.insertComplaint(complaint)) {
            JOptionPane.showMessageDialog(this, "민원이 성공적으로 접수되었습니다.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "오류가 발생했습니다.");
        }
    }

    // 단독 실행 테스트용
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ComplaintFormFrame("20240001", Department.PLSOFT).setVisible(true);
        });
    }
}
