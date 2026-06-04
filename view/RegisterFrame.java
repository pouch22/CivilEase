package com.civilease.view;

import com.civilease.dao.UserDAO;
import com.civilease.model.Department;
import com.civilease.model.User;
import com.civilease.model.UserRole;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField idField, nameField;
    private JPasswordField pwField;
    private JComboBox<Department> deptBox;
    private JButton registerBtn, cancelBtn;
    private UserDAO userDAO;

    public RegisterFrame() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("CivilEase - 학생 회원가입");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
     // 창 아이콘
        try {
            ImageIcon frameIcon = new ImageIcon(getClass().getResource("/resources/images/ui/KNU_LOGO/background_zero/LOGO_3.png"));    
            setIconImage(frameIcon.getImage());
        } catch (Exception e) {
            System.out.println("없는 파일");
        }

        JPanel mainPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel titleLabel = new JLabel("학생 회원가입", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        mainPanel.add(titleLabel);

        idField = new JTextField();
        idField.setBorder(BorderFactory.createTitledBorder("학번 (ID)"));
        mainPanel.add(idField);

        pwField = new JPasswordField();
        pwField.setBorder(BorderFactory.createTitledBorder("생년월일 (PW)"));
        mainPanel.add(pwField);

        nameField = new JTextField();
        nameField.setBorder(BorderFactory.createTitledBorder("이름"));
        mainPanel.add(nameField);

        deptBox = new JComboBox<>(Department.values());
        deptBox.setBorder(BorderFactory.createTitledBorder("소속 학과"));
        mainPanel.add(deptBox);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        registerBtn = new JButton("가입하기");
        cancelBtn = new JButton("취소");
        btnPanel.add(registerBtn);
        btnPanel.add(cancelBtn);
        mainPanel.add(btnPanel);

        add(mainPanel);

        registerBtn.addActionListener(e -> handleRegister());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void handleRegister() {
        String id = idField.getText().trim();
        String pw = new String(pwField.getPassword()).trim();
        String name = nameField.getText().trim();
        Department dept = (Department) deptBox.getSelectedItem();

        if (id.isEmpty() || pw.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 정보를 입력해주세요.");
            return;
        }

        if (userDAO.isIdExists(id)) {
            JOptionPane.showMessageDialog(this, "이미 등록된 학번입니다.");
            return;
        }

        User user = new User(id, pw, name, dept, UserRole.STUDENT);
        if (userDAO.registerUser(user)) {
            JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다! 로그인해주세요.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "가입 도중 오류가 발생했습니다.");
        }
    }
}
