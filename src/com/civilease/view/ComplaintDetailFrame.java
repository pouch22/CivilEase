package com.civilease.view;

import com.civilease.model.Complaint;
import com.civilease.model.ComplaintStatus;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ComplaintDetailFrame extends JFrame {
    private Complaint complaint;
    private StudentMainFrame parent;

    public ComplaintDetailFrame(Complaint complaint, StudentMainFrame parent) {
        this.complaint = complaint;
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        setTitle("민원 상세 보기");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
     // 창 아이콘
        try {
            ImageIcon frameIcon = new ImageIcon(getClass().getResource("/resources/images/ui/KNU_LOGO/background_zero/LOGO_3.png"));    
            setIconImage(frameIcon.getImage());
        } catch (Exception e) {
            System.out.println("없는 파일");
        }

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. 상단 정보 (상태 및 카테고리)
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        infoPanel.add(new JLabel("처리 상태:"));
        JLabel statusLabel = new JLabel(complaint.getStatus().toString());
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        infoPanel.add(statusLabel);

        infoPanel.add(new JLabel("카테고리:"));
        infoPanel.add(new JLabel(complaint.getDepartment().toString() + " / " + complaint.getCategory().toString()));
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // 2. 중앙 내용 (제목, 본문, 답변)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        JTextField titleField = new JTextField(complaint.getTitle());
        titleField.setEditable(false);
        titleField.setBorder(BorderFactory.createTitledBorder("제목"));
        centerPanel.add(titleField, BorderLayout.NORTH);

        JTextArea contentArea = new JTextArea(complaint.getContent());
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(BorderFactory.createTitledBorder("민원 내용"));
        centerPanel.add(contentScroll, BorderLayout.CENTER);

        JTextArea replyArea = new JTextArea(complaint.getAdminReply() != null ? complaint.getAdminReply() : "아직 등록된 답변이 없습니다.");
        replyArea.setEditable(false);
        replyArea.setLineWrap(true);
        replyArea.setBackground(new Color(245, 245, 245));
        JScrollPane replyScroll = new JScrollPane(replyArea);
        replyScroll.setPreferredSize(new Dimension(0, 150));
        replyScroll.setBorder(BorderFactory.createTitledBorder("관리자 답변"));
        centerPanel.add(replyScroll, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 3. 하단 버튼 영역
        JPanel bottomBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // '보완필요' 상태일 때 수정 버튼 추가
        if (complaint.getStatus() == ComplaintStatus.SUPPLEMENT) {
            JButton editBtn = new JButton("내용 수정하기");
            editBtn.addActionListener(e -> {
                new ComplaintEditFrame(complaint, parent).setVisible(true);
                dispose();
            });
            bottomBtnPanel.add(editBtn);
        }

        // 첨부 파일 버튼
        if (complaint.getAttachmentPath() != null && !complaint.getAttachmentPath().isEmpty()) {
            JButton viewFileBtn = new JButton("첨부 파일 보기");
            viewFileBtn.addActionListener(e -> {
                try {
                    Desktop.getDesktop().open(new File(complaint.getAttachmentPath()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "파일을 열 수 없습니다.");
                }
            });
            bottomBtnPanel.add(viewFileBtn);
        }

        JButton closeBtn = new JButton("닫기");
        closeBtn.addActionListener(e -> dispose());
        bottomBtnPanel.add(closeBtn);

        mainPanel.add(bottomBtnPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}
