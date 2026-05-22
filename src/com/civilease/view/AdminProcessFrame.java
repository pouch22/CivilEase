package com.civilease.view;

import com.civilease.dao.ComplaintDAO;
import com.civilease.model.Complaint;
import com.civilease.model.ComplaintStatus;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class AdminProcessFrame extends JFrame {
    private Complaint complaint;
    private AdminMainFrame parent;
    private ComplaintDAO complaintDAO;
    
    private JComboBox<ComplaintStatus> statusBox;
    private JTextArea replyArea;

    public AdminProcessFrame(Complaint complaint, AdminMainFrame parent) {
        this.complaint = complaint;
        this.parent = parent;
        this.complaintDAO = new ComplaintDAO();
        initUI();
    }

    private void initUI() {
        setTitle("민원 처리 - ID: " + complaint.getComplaintId());
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
     // 창 아이콘
        try {
            ImageIcon frameIcon = new ImageIcon("src/resources/images/ui/KNU_LOGO/background_zero/LOGO_3.png");    
            setIconImage(frameIcon.getImage());
        } catch (Exception e) {
            System.out.println("없는 파일");
        }

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. 민원 정보 표시 (읽기 전용)
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.add(new JLabel("작성자: " + complaint.getStudentId()));
        infoPanel.add(new JLabel("학과/카테고리: " + complaint.getDepartment().toString() + " / " + complaint.getCategory().toString()));
        
        JTextField titleField = new JTextField(complaint.getTitle());
        titleField.setEditable(false);
        titleField.setBorder(BorderFactory.createTitledBorder("제목"));
        
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.add(infoPanel, BorderLayout.NORTH);
        topWrapper.add(titleField, BorderLayout.SOUTH);
        mainPanel.add(topWrapper, BorderLayout.NORTH);

        // 2. 민원 내용 표시 (읽기 전용)
        JTextArea contentArea = new JTextArea(complaint.getContent());
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(BorderFactory.createTitledBorder("민원 내용"));
        mainPanel.add(contentScroll, BorderLayout.CENTER);

        // 3. 처리 영역 (상태 변경 및 답변 작성)
        JPanel processPanel = new JPanel(new BorderLayout(10, 10));
        
        // JComboBox<ComplaintStatus>는 자동으로 toString()을 호출하여 한글을 보여줍니다.
        statusBox = new JComboBox<>(ComplaintStatus.values());
        statusBox.setSelectedItem(complaint.getStatus());
        statusBox.setBorder(BorderFactory.createTitledBorder("상태 변경"));
        processPanel.add(statusBox, BorderLayout.NORTH);

        replyArea = new JTextArea(complaint.getAdminReply());
        replyArea.setLineWrap(true);
        JScrollPane replyScroll = new JScrollPane(replyArea);
        replyScroll.setPreferredSize(new Dimension(0, 150));
        replyScroll.setBorder(BorderFactory.createTitledBorder("관리자 답변 작성"));
        processPanel.add(replyScroll, BorderLayout.CENTER);

        // 버튼
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // 첨부파일 확인 버튼 추가
        if (complaint.getAttachmentPath() != null && !complaint.getAttachmentPath().isEmpty()) {
            JButton viewFileBtn = new JButton("첨부 파일 보기");
            viewFileBtn.addActionListener(e -> {
                try {
                    java.awt.Desktop.getDesktop().open(new java.io.File(complaint.getAttachmentPath()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "파일을 열 수 없습니다.");
                }
            });
            btnPanel.add(viewFileBtn);
        }

        JButton saveBtn = new JButton("저장하기");
        JButton cancelBtn = new JButton("취소");
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        processPanel.add(btnPanel, BorderLayout.SOUTH);

        mainPanel.add(processPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 리스너
        saveBtn.addActionListener(e -> handleSave());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void handleSave() {
        ComplaintStatus newStatus = (ComplaintStatus) statusBox.getSelectedItem();
        String reply = replyArea.getText().trim();

        if (complaintDAO.updateComplaint(complaint.getComplaintId(), newStatus, reply)) {
            JOptionPane.showMessageDialog(this, "처리가 완료되었습니다.");
            parent.refreshData(); // 메인 화면 목록 새로고침
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "저장 중 오류가 발생했습니다.");
        }
    }
}
