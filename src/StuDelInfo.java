import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class StuDelInfo extends JFrame implements ActionListener {
    private static final int MAX_TRANSACTION_ATTEMPTS = 3; // 事务重试次数
    JLabel jlnumber = new JLabel("学号：");
    JTextField jtnumber = new JTextField(20);
    JButton bdel = new JButton("删除");
    JButton breturn = new JButton("返回");
    
    public StuDelInfo() {
        this.setTitle("删除学生信息");
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 学号标签
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(jlnumber, gbc);
        
        // 学号输入框
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        contentPanel.add(jtnumber, gbc);
        
        // 按钮面板
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(bdel);
        buttonPanel.add(breturn);
        contentPanel.add(buttonPanel, gbc);
        
        bdel.addActionListener(this);
        breturn.addActionListener(this);
        
        this.add(contentPanel);
        this.pack();
        this.setSize(350, 150);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == breturn) {
            this.dispose();
            return;
        }
        
        if (e.getSource() != bdel) {
            return;
        }
        
        String number = jtnumber.getText().trim();
        if (number.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "请输入学号", 
                "输入错误", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 加载学生信息以便在确认框中显示
        String studentInfo = getStudentInfo(number);
        String message = "您确定要删除学号为 " + number + " 的学生记录吗？";
        
        if (studentInfo != null) {
            message = "您将删除以下学生：\n\n" + studentInfo + 
                     "\n\n此操作将一并删除所有相关成绩记录！";
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            message, 
            "确认删除", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // 执行删除操作（带重试机制）
        performDelete(number, 0);
    }
    
    private String getStudentInfo(String studentId) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT xingming, xingbie, xueyuan, chushengriqi FROM xuesheng WHERE xuehao = ?")) {
            
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return "姓名: " + rs.getString("xingming") + "\n" +
                       "性别: " + rs.getString("xingbie") + "\n" +
                       "学院: " + rs.getString("xueyuan") + "\n" +
                       "出生日期: " + rs.getString("chushengriqi");
            }
        } catch (Exception ex) {
            // 忽略错误，使用通用确认信息
        }
        return null;
    }
    
    private void performDelete(String studentId, int attempt) {
        Connection conn = null;
        try {
            // 第一次尝试使用级联删除
            if (attempt == 0) {
                conn = DatabaseUtil.getConnection();
                conn.setAutoCommit(false);
                
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM xuesheng WHERE xuehao = ?")) {
                    pstmt.setString(1, studentId);
                    int rowsAffected = pstmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        conn.commit();
                        JOptionPane.showMessageDialog(this, 
                            "学生及相关成绩记录已成功删除", 
                            "删除成功", 
                            JOptionPane.INFORMATION_MESSAGE);
                        return;
                    } else {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, 
                            "未找到学号为 " + studentId + " 的学生信息", 
                            "删除失败", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            
            // 第二次尝试手动删除（当数据库不支持级联删除时）
            if (attempt < MAX_TRANSACTION_ATTEMPTS) {
                if (conn == null) {
                    conn = DatabaseUtil.getConnection();
                    conn.setAutoCommit(false);
                }
                
                // 1. 先删除成绩记录
                String deleteScores = "DELETE FROM scores WHERE student_id = ?";
                try (PreparedStatement scoresStmt = conn.prepareStatement(deleteScores)) {
                    scoresStmt.setString(1, studentId);
                    scoresStmt.executeUpdate();
                }
                
                // 2. 删除学生记录
                String deleteStudent = "DELETE FROM xuesheng WHERE xuehao = ?";
                try (PreparedStatement studentStmt = conn.prepareStatement(deleteStudent)) {
                    studentStmt.setString(1, studentId);
                    int rowsAffected = studentStmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        conn.commit();
                        JOptionPane.showMessageDialog(this, 
                            "学生及相关成绩记录已成功删除", 
                            "删除成功", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, 
                            "未找到学号为 " + studentId + " 的学生信息", 
                            "删除失败", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "多次尝试删除操作均失败，请联系管理员", 
                    "严重错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                ex.addSuppressed(rollbackEx);
            }
            
            if (attempt < MAX_TRANSACTION_ATTEMPTS) {
                // 自动重试
                performDelete(studentId, attempt + 1);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "删除失败: " + ex.getMessage() + 
                    "\n可能原因：数据库外键配置问题\n解决方案请咨询管理员", 
                    "删除错误", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                // 忽略
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StuDelInfo::new);
    }
}
