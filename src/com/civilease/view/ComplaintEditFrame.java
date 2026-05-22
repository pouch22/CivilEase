package com.civilease.view;

import com.civilease.dao.ComplaintDAO;
import com.civilease.model.Complaint;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ComplaintEditFrame extends JFrame {
    private Complaint complaint;
    private StudentMainFrame parent;
    private ComplaintDAO complaintDAO;

    private JTextField titleField;
    private JTextArea contentArea;
    private JButton fileBtn, saveBtn, cancelBtn;
    private JLabel fileLabel;
    private File selectedFile;

    public ComplaintEditFrame(Complaint complaint, StudentMainFrame parent) {
        this.complaint = complaint;
        this.parent = parent;
        this.complaintDAO = new ComplaintDAO();
        initUI();
    }

    private void initUI() {
        setTitle("민원 내용 수정 (보완 필요)");
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
     // 창 아이콘
        try {
            ImageIcon frameIcon = new ImageIcon("src/resources/images/ui/KNU_LOGO/background_zero/LOGO_3.png");    
            setIconImage(frameIcon.getImage());
        } catch (Exception e) {
            System.out.println("없는 파일");
        }

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 상단: 파일 수정
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fileBtn = new JButton("첨부 파일 변경");
        fileLabel = new JLabel(complaint.getAttachmentPath() != null ? new File(complaint.getAttachmentPath()).getName() : "첨부 없음");
        topPanel.add(fileBtn);
        topPanel.add(fileLabel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 중앙: 제목 및 내용
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        titleField = new JTextField(complaint.getTitle());
        titleField.setBorder(BorderFactory.createTitledBorder("제목"));
        centerPanel.add(titleField, BorderLayout.NORTH);

        contentArea = new JTextArea(complaint.getContent());
        contentArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("내용"));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 하단: 버튼
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveBtn = new JButton("수정 완료");
        cancelBtn = new JButton("취소");
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 리스너
        fileBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();
                fileLabel.setText(selectedFile.getName());
            }
        });

        saveBtn.addActionListener(e -> handleUpdate());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void handleUpdate() {
        String newTitle = titleField.getText().trim();
        String newContent = contentArea.getText().trim();
        String attachmentPath = complaint.getAttachmentPath();

        if (newTitle.isEmpty() || newContent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "제목과 내용을 입력해주세요.");
            return;
        }

        // 새 파일이 선택된 경우 복사
        if (selectedFile != null) {
            try {
                File uploadDir = new File("uploads");
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destFile = new File(uploadDir, fileName);
                java.nio.file.Files.copy(selectedFile.toPath(), destFile.toPath());
                attachmentPath = destFile.getPath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (complaintDAO.updateComplaintContent(complaint.getComplaintId(), newTitle, newContent, attachmentPath)) {
            JOptionPane.showMessageDialog(this, "민원이 수정되었습니다.");
            if (parent != null) parent.loadComplaints();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "수정 중 오류가 발생했습니다.");
        }
    }
}
