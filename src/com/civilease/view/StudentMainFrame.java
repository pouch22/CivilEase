package com.civilease.view;

import com.civilease.dao.ComplaintDAO;
import com.civilease.model.Complaint;
import com.civilease.model.ComplaintStatus;
import com.civilease.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentMainFrame extends JFrame {
    private User currentUser;
    private ComplaintDAO complaintDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Complaint> complaintList;

    public StudentMainFrame(User user) {
        this.currentUser = user;
        this.complaintDAO = new ComplaintDAO();
        initUI();
        loadComplaints();
    }

    private void initUI() {
        String deptName = (currentUser.getDepartment() != null) ? currentUser.getDepartment().getName() : "소속 없음";
        setTitle("CivilEase - 학생 메인 (" + currentUser.getName() + "님 / " + deptName + ")");
        setSize(950, 500); // 가로 배치에 맞춰 창 너비를 950으로 넉넉하게 조정했습니다.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 창 아이콘
        try {
            ImageIcon frameIcon = new ImageIcon("src/resources/images/ui/KNU_LOGO/background_zero/LOGO_3.png");    
            setIconImage(frameIcon.getImage());
        } catch (Exception e) {
            System.out.println("없는 파일");
        }

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 상단 영역 전체를 감싸는 메인 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // 1. 환영 메시지 생성
        JLabel welcomeLabel = new JLabel(currentUser.getName() + "님의 민원 현황입니다.");
        welcomeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        
        // 2. 내 정보 패널 (2행 2열 격자 구조)
        JPanel userInfoPanel = new JPanel(new GridLayout(2, 2, 15, 5));
        userInfoPanel.setBorder(BorderFactory.createTitledBorder("내 정보"));
        
        JLabel nameLabel = new JLabel("이름: " + currentUser.getName());
        JLabel idLabel = new JLabel("학번: " + currentUser.getUserId());
        JLabel deptLabel = new JLabel("학과: " + deptName);
        JLabel roleLabel = new JLabel("권한: 학생");
        
        userInfoPanel.add(nameLabel);
        userInfoPanel.add(idLabel);
        userInfoPanel.add(deptLabel);
        userInfoPanel.add(roleLabel);
        
        // 3. 왼쪽 조립 영역: FlowLayout을 사용하여 환영 메시지 바로 오른쪽에 내 정보 배치
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.add(welcomeLabel);
        leftPanel.add(userInfoPanel);
        
        // 4. 오른쪽 버튼 패널 구성
        JButton writeBtn = new JButton("새 민원 작성");
        JButton cancelBtn = new JButton("민원 취소");
        JButton refreshBtn = new JButton("새로고침");
        JButton logoutBtn = new JButton("로그아웃");
        try {
            ImageIcon logoutIcon = new ImageIcon("src/resources/images/ui/logout_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24.png");
            logoutBtn.setIcon(logoutIcon);
        } catch (Exception e) {
            System.out.println("로그아웃 아이콘 로드 실패");
        }
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(refreshBtn);
        btnPanel.add(cancelBtn);
        btnPanel.add(writeBtn);
        btnPanel.add(logoutBtn);
        
        // 상단 패널의 왼쪽에 가로 조립 패널을, 오른쪽에 버튼 패널을 배치
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(btnPanel, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // 로그아웃 버튼 이벤트 리스너
        logoutBtn.addActionListener(e -> { 
            dispose(); 
            new LoginFrame().setVisible(true);
        });

        // 민원 목록 테이블
        String[] columnNames = {"ID", "제목", "학과", "카테고리", "상태", "작성일"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 가이드 메시지
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JLabel guideLabel = new JLabel(" * 더블클릭: 상세 보기 | 접수대기 상태의 민원만 '민원 취소'가 가능합니다.", JLabel.LEFT);
        guideLabel.setForeground(Color.GRAY);
        bottomPanel.add(guideLabel, BorderLayout.WEST);
        try {
          ImageIcon logoIcon = new ImageIcon("src/resources/images/ui/KNU_LOGO/background_zero/LOGO_2.png");
          Image scaledLogo = logoIcon.getImage().getScaledInstance(150, 20, Image.SCALE_SMOOTH);
          JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
          bottomPanel.add(logoLabel, BorderLayout.EAST);
          } catch (Exception e) {
               System.out.println("하단 로고 로드 실패");
          }
          
          mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // 이벤트 리스너
        writeBtn.addActionListener(e -> {
            new ComplaintFormFrame(currentUser.getUserId(), currentUser.getDepartment()).setVisible(true);
        });

        cancelBtn.addActionListener(e -> handleCancel());

        refreshBtn.addActionListener(e -> loadComplaints());

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1 && complaintList != null) {
                        new ComplaintDetailFrame(complaintList.get(row), StudentMainFrame.this).setVisible(true);
                    }
                }
            }
        });
    }

    private void handleCancel() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "취소할 민원을 먼저 선택해주세요.");
            return;
        }

        Complaint selected = complaintList.get(row);
        if (selected.getStatus() != ComplaintStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "접수대기 중인 민원만 취소할 수 있습니다.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "정말 이 민원을 취소하시겠습니까?", "취소 확인", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (complaintDAO.deleteComplaint(selected.getComplaintId())) {
                JOptionPane.showMessageDialog(this, "민원이 성공적으로 취소되었습니다.");
                loadComplaints();
            } else {
                JOptionPane.showMessageDialog(this, "취소 실패: 다시 시도해주세요.");
            }
        }
    }

    public void loadComplaints() {
        tableModel.setRowCount(0);
        complaintList = complaintDAO.getComplaintsByStudent(currentUser.getUserId());
        for (Complaint c : complaintList) {
            Object[] row = {
                c.getComplaintId(),
                c.getTitle(),
                c.getDepartment().toString(),
                c.getCategory().toString(),
                c.getStatus().toString(),
                c.getCreatedAt()
            };
            tableModel.addRow(row);
        }
    }
}