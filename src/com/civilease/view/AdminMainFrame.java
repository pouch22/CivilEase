package com.civilease.view;

import com.civilease.dao.ComplaintDAO;
import com.civilease.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminMainFrame extends JFrame {
    private User adminUser;
    private ComplaintDAO complaintDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private JComboBox<String> categoryFilter;
    private JComboBox<String> sortOrder;
    private List<Complaint> complaintList;
    
    // 통계용 라벨
    private JLabel pendingLabel;
    private JLabel supplementLabel;
    private JLabel completedLabel;

    public AdminMainFrame(User user) {
        this.adminUser = user;
        this.complaintDAO = new ComplaintDAO();
        initUI();
        loadComplaints();
    }

    private void initUI() {
        setTitle("CivilEase - 관리자 대시보드 (" + adminUser.getName() + ")");
        setSize(1000, 650); // 통계창 추가로 인해 높이를 약간 늘림
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        
     // 창 아이콘
        try {
            ImageIcon frameIcon = new ImageIcon(getClass().getResource("/resources/images/ui/KNU_LOGO/background_zero/LOGO_3.png"));    
            setIconImage(frameIcon.getImage());
        } catch (Exception e) {
            System.out.println("없는 파일");
        }

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. 상단 영역 (통계 + 필터)
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        
        // 1-1. 통계 패널
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        statsPanel.setBackground(new Color(245, 245, 245));
        statsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        pendingLabel = new JLabel("접수대기: 0");
        pendingLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        pendingLabel.setForeground(new Color(220, 53, 69)); // 빨간색 계열
        
        supplementLabel = new JLabel("보완필요: 0");
        supplementLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        supplementLabel.setForeground(new Color(255, 193, 7)); // 노란색 계열
        
        completedLabel = new JLabel("처리완료: 0");
        completedLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        completedLabel.setForeground(new Color(40, 167, 69)); // 초록색 계열
        
        statsPanel.add(new JLabel("<html><b>[민원 통계]</b></html>"));
        statsPanel.add(pendingLabel);
        statsPanel.add(supplementLabel);
        statsPanel.add(completedLabel);
        
        headerPanel.add(statsPanel, BorderLayout.NORTH);

        // 1-2. 필터 및 로그아웃 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("상태:"));
        statusFilter = new JComboBox<>();
        statusFilter.addItem("전체");
        for (ComplaintStatus s : ComplaintStatus.values()) {
            statusFilter.addItem(s.toString()); // toString()을 사용하여 한글 출력
        }
        filterPanel.add(statusFilter);

        filterPanel.add(new JLabel("카테고리:"));
        categoryFilter = new JComboBox<>();
        categoryFilter.addItem("전체");
        for (Category c : Category.values()) {
            categoryFilter.addItem(c.toString()); // toString()을 사용하여 한글 출력
        }
        filterPanel.add(categoryFilter);

        filterPanel.add(new JLabel("정렬:"));
        sortOrder = new JComboBox<>(new String[]{"최신순", "오래된순"});
        filterPanel.add(sortOrder);

        JButton searchBtn = new JButton("검색/새로고침");
        filterPanel.add(searchBtn);
        
        topPanel.add(filterPanel, BorderLayout.WEST);

        // 로그아웃 버튼 추가
        JButton logoutBtn = new JButton("로그아웃");
        try {
            ImageIcon logoutIcon = new ImageIcon(getClass().getResource("/resources/images/ui/logout_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24.png"));
            logoutBtn.setIcon(logoutIcon);
        } catch (Exception e) {
            System.out.println("로그아웃 아이콘 로드 실패");
        }
        
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.add(logoutBtn);
        topPanel.add(logoutPanel, BorderLayout.EAST);

        headerPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. 중앙 테이블 영역
        String[] columnNames = {"ID", "학번", "이름", "제목", "학과", "카테고리", "상태", "작성일"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 3. 하단 버튼 영역
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JLabel guideLabel = new JLabel(" * 처리할 민원을 선택한 후 더블클릭하거나 아래 버튼을 누르세요.");
        guideLabel.setForeground(Color.GRAY);
        bottomPanel.add(guideLabel, BorderLayout.WEST);

        JButton processBtn = new JButton("민원 처리하기");
        bottomPanel.add(processBtn, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 이벤트 리스너
        searchBtn.addActionListener(e -> loadComplaints());
        processBtn.addActionListener(e -> openProcessFrame());
        
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) openProcessFrame();
            }
        });
    }

    private void loadComplaints() {
        tableModel.setRowCount(0);
        
        // 통계 업데이트
        updateStatistics();
        
        String sSelected = statusFilter.getSelectedItem().toString();
        String cSelected = categoryFilter.getSelectedItem().toString();
        String order = sortOrder.getSelectedItem().toString().equals("최신순") ? "NEW" : "OLD";

        String sFilter = "";
        if (!"전체".equals(sSelected)) {
            for (ComplaintStatus s : ComplaintStatus.values()) {
                if (s.toString().equals(sSelected)) {
                    sFilter = s.name();
                    break;
                }
            }
        }

        String cFilter = "";
        if (!"전체".equals(cSelected)) {
            for (Category c : Category.values()) {
                if (c.toString().equals(cSelected)) {
                    cFilter = c.name();
                    break;
                }
            }
        }

        complaintList = complaintDAO.getAllComplaints(sFilter, cFilter, order);
        
        for (Complaint c : complaintList) {
            Object[] row = {
                c.getComplaintId(),
                c.getStudentId(),
                c.getStudentName(), // 학생 이름 추가
                c.getTitle(),
                c.getDepartment().toString(),
                c.getCategory().toString(),
                c.getStatus().toString(),
                c.getCreatedAt()
            };
            tableModel.addRow(row);
        }
    }

    private void updateStatistics() {
        java.util.Map<ComplaintStatus, Integer> counts = complaintDAO.getComplaintCounts();
        pendingLabel.setText("접수대기: " + counts.getOrDefault(ComplaintStatus.PENDING, 0));
        supplementLabel.setText("보완필요: " + counts.getOrDefault(ComplaintStatus.SUPPLEMENT, 0));
        completedLabel.setText("처리완료: " + counts.getOrDefault(ComplaintStatus.COMPLETED, 0));
    }

    private void openProcessFrame() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "처리할 민원을 선택해주세요.");
            return;
        }
        Complaint selected = complaintList.get(row);
        new AdminProcessFrame(selected, this).setVisible(true);
    }

    public void refreshData() {
        loadComplaints();
    }
}
