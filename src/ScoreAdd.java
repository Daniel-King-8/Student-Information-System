import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class ScoreAdd extends JFrame implements ActionListener {
    // 界面组件
    private JLabel lblStudentId = new JLabel("学号:");
    private JLabel lblSubject = new JLabel("科目:");
    private JLabel lblScore = new JLabel("成绩:");

    private JTextField txtStudentId = new JTextField(20);
    private String[] subjects = {"高数", "语文", "英语", "Go", "Linux"};
    private JComboBox<String> cmbSubject = new JComboBox<>(subjects); // 使用下拉框限制科目
    private JTextField txtScore = new JTextField(20);

    private JButton btnSubmit = new JButton("提交");
    private JButton btnReturn = new JButton("返回");

    // 数据库配置
    private static final String JDBC_URL =
        "jdbc:mysql://localhost:3308/student?useUnicode=true&characterEncoding=utf8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "8888";

    public ScoreAdd() {
        this.setTitle("成绩录入");
        this.setSize(400, 250);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 设置窗口居中
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(screenSize.width / 2 - this.getWidth() / 2,
                        screenSize.height / 2 - this.getHeight() / 2);

        // 布局设置
        JPanel panel = new JPanel(new GridLayout(4, 2));

        panel.add(lblStudentId);
        panel.add(txtStudentId);
        panel.add(lblSubject);
        panel.add(cmbSubject); // 替换为下拉框
        panel.add(lblScore);
        panel.add(txtScore);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnReturn);

        btnSubmit.addActionListener(this);
        btnReturn.addActionListener(this);

        this.add(panel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSubmit) {
            String studentIdStr = txtStudentId.getText();
            String subject = (String) cmbSubject.getSelectedItem(); // 获取选中的科目
            String scoreStr = txtScore.getText();

            if (studentIdStr.isEmpty() || scoreStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "学号和成绩必须填写！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int studentId = Integer.parseInt(studentIdStr);
                double score = Double.parseDouble(scoreStr);

                insertScore(studentId, subject, score);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "学号或成绩必须为数字！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnReturn) {
            this.dispose(); // 关闭当前窗口
        }
    }

    /**
     * 将成绩信息插入到数据库中
     */
    private void insertScore(int studentId, String subject, double score) {
        String sql = "INSERT INTO scores (student_id, subject, score) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setString(2, subject); // 插入下拉框选中的科目
            pstmt.setDouble(3, score);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "成绩录入成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "成绩录入失败，请检查数据是否正确。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库连接失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ScoreAdd());
    }
}