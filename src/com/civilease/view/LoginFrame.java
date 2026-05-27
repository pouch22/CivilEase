package com.civilease.view;

import com.civilease.dao.UserDAO;
import com.civilease.model.User;
import com.civilease.model.UserRole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField idField;
    private JPasswordField pwField;
    private JButton loginBtn;
    private JButton registerBtn;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("CivilEase - 로그인");
        setSize(470, 450); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙에 배치

        // 창 아이콘
        try {
            ImageIcon frameIcon = new ImageIcon(getClass().getResource("/resources/images/ui/KNU_LOGO/background_zero/LOGO_3.png"));    
            setIconImage(frameIcon.getImage());
        } catch (Exception e) {
            System.out.println("없는 파일");
        }

        
        // 메인 패널 
        JPanel mainPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        
        // 타이틀 라벨
        JLabel titleLabel = new JLabel("CivilEase 로그인", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        mainPanel.add(titleLabel);
        
        
      
        // 아이디 입력
        idField = new JTextField();
        idField.setBorder(BorderFactory.createTitledBorder("학번"));
        mainPanel.add(idField);

        // 비밀번호 입력
        pwField = new JPasswordField();
        pwField.setBorder(BorderFactory.createTitledBorder("생년월일"));
        mainPanel.add(pwField);

        // 버튼 패널
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        loginBtn = new JButton("로그인");
        registerBtn = new JButton("회원가입");
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        mainPanel.add(btnPanel);
        
        
     // 경북대 로고 
        try {
           ImageIcon icon = new ImageIcon(getClass().getResource("/resources/images/ui/KNU_LOGO/background_zero/LOGO_2.png"));
           Image img = icon.getImage().getScaledInstance(300, 40, Image.SCALE_SMOOTH);
           JLabel iconLabel = new JLabel(new ImageIcon(img), JLabel.CENTER);
           
           // 로고 위에 마우스 올리면 커서 모양 바뀜
           iconLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
           
           // 마우스 클릭시 이벤트 예외처리를 필요로함
           iconLabel.addMouseListener(new java.awt.event.MouseAdapter() {
               @Override
               public void mouseClicked(java.awt.event.MouseEvent e) {
                   try {
                       java.awt.Desktop.getDesktop().browse(new java.net.URI("https://www.knu.ac.kr"));
                   } catch (Exception ex) {
                       System.out.println("웹페이지를 열 수 없음  " + ex.getMessage());
                   }
               }
           });
           
          mainPanel.add(iconLabel);
        } catch (Exception e) {
           // 이미지가 없을 때 위치 표기용
          mainPanel.add(new JLabel("(아이콘 위치)", JLabel.CENTER));
        }
        


        add(mainPanel);

        // 이벤트
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterFrame().setVisible(true);
            }
        });
    }

    private void handleLogin() {
        String id = idField.getText();
        String pw = new String(pwField.getPassword());

        if (id.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 입력해주세요.");
            return;
        }

        User user = userDAO.login(id, pw);
        if (user != null) {
            JOptionPane.showMessageDialog(this, user.getName() + "님 환영합니다!");
            this.dispose(); // 현재 창 닫기
            
            if (user.getRole() == UserRole.ADMIN) {
                new AdminMainFrame(user).setVisible(true);
            } else {
                new StudentMainFrame(user).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    public static void main(String[] args) {
        // Swing UI는 Event Dispatch Thread(EDT)에서 실행해야 안전하다.
    	// 여러 Thread가 ui을 그릴려고하면 오류가 발생할 수 있어서 스윙은 EDT라는 Thread를 쓴다.
    	// 아래 코드는 로그인 창 띄우는걸 EDT에게 넘기라는 뜻

    	// 쓰레드
    	// 인터페이스 Runnable의 run 메소드를 재정의
    	// new Runnable() 익명 클래스 생성
    	// run을 바로 쓰는 이유는 새로운 쓰레드를 만드는게 아니라 이미 있는 EDT라는 스레드에게 나중에 일해라는 뜻이기에
    	
    	// System.out.print(Thread.currentThread().getName());
    	// 출력 main
    	SwingUtilities.invokeLater(new Runnable() { 
    		// run 메소드 재정의
    	    @Override
    	    public void run() {
    	    	// 창 띄우기
    	    	// System.out.print(Thread.currentThread().getName());
    	    	// 출력 AWT-EventQueue-0
    	        new LoginFrame().setVisible(true);
    	    }
    	});
    }
}
