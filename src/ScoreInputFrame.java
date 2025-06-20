import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.text.NumberFormat;
import java.text.ParseException;

public class ScoreInputFrame extends JFrame implements ActionListener {
    private JTextField tfNumber, tfName, tfDepartment;
    private JTextField[] scoreFields;
    private JButton btnFind, btnSave;
    private final String[] subjects = { "语文", "高数", "英语", "Java", "Go", "Linux", "双创", "思政", "实训" };
    
    public ScoreInputFrame() {
        setTitle("成绩录入");
        setLayout(new BorderLayout(10, 10));
        setResizable(true);
        
        // 顶部面板 - 搜索区域
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        topPanel.add(new JLabel("学号:", SwingConstants.RIGHT));
        tfNumber = new JTextField();
        topPanel.add(tfNumber);
        
        topPanel.add(new JLabel("姓名:", SwingConstants.RIGHT));
        tfName = new JTextField();
        topPanel.add(tfName);
        
        topPanel.add(new JLabel("学院:", SwingConstants.RIGHT));
        tfDepartment = new JTextField();
        topPanel.add(tfDepartment);
        
        btnFind = new JButton("查找学生");
        btnFind.addActionListener(this);
        // 为学院字段添加边框
        JPanel findPanel = new JPanel(new BorderLayout());
        findPanel.add(btnFind, BorderLayout.WEST);
        topPanel.add(findPanel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // 中间面板 - 成绩输入区域
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createTitledBorder("成绩输入"));
        
        JPanel scoreGrid = new JPanel(new GridLayout(0, 2, 5, 5));
        scoreGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        scoreFields = new JTextField[subjects.length];
        for (int i = 0; i < subjects.length; i++) {
            JLabel label = new JLabel(subjects[i] + ": ", SwingConstants.RIGHT);
            scoreGrid.add(label);
            
            scoreFields[i] = new JTextField(5);
            scoreGrid.add(scoreFields[i]);
        }
        
        centerPanel.add(scoreGrid);
        add(new JScrollPane(centerPanel), BorderLayout.CENTER);
        
        // 底部面板 - 保存按钮
        btnSave = new JButton("保存成绩");
        btnSave.addActionListener(this);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(btnSave);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // 设置窗口属性
        pack();
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnFind) {
            findStudent();
        } else if (e.getSource() == btnSave) {
            saveScores();
        }
    }
    
    private void findStudent() {
        String number = tfNumber.getText().trim();
        String name = tfName.getText().trim();
        String department = tfDepartment.getText().trim();
        
        // 允许只输入学号查找
        if (number.isEmpty() && name.isEmpty() && department.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入至少一个查询条件", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT xuehao, xingming, xueyuan FROM xuesheng " +
                         "WHERE xuehao LIKE ? OR xingming LIKE ? OR xueyuan LIKE ? LIMIT 10";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // 使用模糊查询提高查找成功率
                String param = "%" + (number.isEmpty() ? (name.isEmpty() ? department : name) : number) + "%";
                
                pstmt.setString(1, param);
                pstmt.setString(2, param);
                pstmt.setString(3, param);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // 更新界面显示找到的学生
                        tfNumber.setText(rs.getString("xuehao"));
                        tfName.setText(rs.getString("xingming"));
                        tfDepartment.setText(rs.getString("xueyuan"));
                        
                        // 载入该学生的现有成绩
                        loadExistingScores(rs.getString("xuehao"));
                        
                        // 检查是否有多个匹配项
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(this, 
                                "找到多个匹配的学生，将显示第一条结果", 
                                "查询结果", 
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "未找到匹配的学生信息", 
                            "查询结果", 
                            JOptionPane.INFORMATION_MESSAGE);
                        clearStudentInfo();
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            handleDatabaseError("查找学生失败", ex);
        }
    }
    
    private void loadExistingScores(String studentId) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM scores WHERE student_id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, studentId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    // 清空所有成绩字段
                    for (JTextField field : scoreFields) {
                        field.setText("");
                    }
                    
                    if (rs.next()) {
                        // 加载各科目成绩
                        for (int i = 0; i < subjects.length; i++) {
                            float score = rs.getFloat(subjects[i]);
                            if (!rs.wasNull()) {
                                scoreFields[i].setText(String.format("%.1f", score));
                            }
                        }
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            handleDatabaseError("加载成绩失败", ex);
        }
    }
    
    // 处理数据库错误的通用方法
    private void handleDatabaseError(String task, Exception ex) {
        String errorMsg = task + ": " + ex.getMessage();
        if (ex instanceof SQLException) {
            errorMsg += "\nSQL状态: " + ((SQLException) ex).getSQLState();
        }
        JOptionPane.showMessageDialog(this, errorMsg, "数据库错误", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    
    private void saveScores() {
        String studentId = tfNumber.getText().trim();
        String studentName = tfName.getText().trim();
        String department = tfDepartment.getText().trim(); // 获取学院字段
        
        // 验证学生ID
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先通过[查找学生]功能确定学生学号", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 验证学院信息 - 新增的关键验证
        if (department.isEmpty()) {
            JOptionPane.showMessageDialog(this, "学院信息不能为空，请通过查找功能获取学院信息", "数据缺失", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 验证学生是否存在
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement checkStmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM xuesheng WHERE xuehao = ? AND xueyuan = ?")) {
                
                checkStmt.setString(1, studentId);
                checkStmt.setString(2, department);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        JOptionPane.showMessageDialog(this, 
                            "学号 " + studentId + " 和学院 " + department + " 在系统中不存在", 
                            "验证失败", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            
            // 构建SQL插入/更新语句（使用反引号包裹中文列名）
            StringBuilder sqlBuilder = new StringBuilder("INSERT INTO scores (student_id, xueyuan"); // 重要: 增加xueyuan字段
            
            // 添加科目字段
            for (String subject : subjects) {
                sqlBuilder.append(", `").append(subject).append("`");
            }
            
            sqlBuilder.append(") VALUES (?, ?"); // student_id 和 xueyuan
            
            // 添加科目占位符
            for (int i = 0; i < subjects.length; i++) {
                sqlBuilder.append(", ?");
            }
            
            sqlBuilder.append(") ON DUPLICATE KEY UPDATE ");
            
            // 添加更新部分
            for (int i = 0; i < subjects.length; i++) {
                if (i > 0) sqlBuilder.append(", ");
                sqlBuilder.append("`").append(subjects[i]).append("` = ?");
            }
            
            // 执行SQL
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement saveStmt = conn.prepareStatement(sqlBuilder.toString())) {
                
                // 设置新增部分的参数 - 索引1和2
                saveStmt.setString(1, studentId);
                saveStmt.setString(2, department); // 关键: 设置学院字段
                
                int paramIndex = 3;
                for (JTextField field : scoreFields) {
                    String scoreText = field.getText().trim();
                    if (!scoreText.isEmpty()) {
                        saveStmt.setFloat(paramIndex, parseScore(scoreText));
                    } else {
                        saveStmt.setNull(paramIndex, Types.FLOAT);
                    }
                    paramIndex++;
                }
                
                // 设置更新部分的参数
                for (JTextField field : scoreFields) {
                    String scoreText = field.getText().trim();
                    if (!scoreText.isEmpty()) {
                        saveStmt.setFloat(paramIndex, parseScore(scoreText));
                    } else {
                        saveStmt.setNull(paramIndex, Types.FLOAT);
                    }
                    paramIndex++;
                }
                
                // 执行更新
                int rowsAffected = saveStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, 
                        studentName + "(" + studentId + ")" + "的成绩保存成功！",
                        "操作成功", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "成绩保存失败，请重试", 
                        "操作失败", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "成绩必须是有效的数字（0-100）！请输入正确的数值（如：85.5）", 
                "格式错误", 
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | ClassNotFoundException ex) {
            handleDatabaseError("保存成绩失败", ex);
        }
    }
    
    // 解析成绩文本为浮点数
    private float parseScore(String scoreText) throws NumberFormatException {
        float score = Float.parseFloat(scoreText);
        if (score < 0 || score > 100) {
            throw new NumberFormatException("成绩必须在0到100之间");
        }
        return score;
    }
    
    private void clearStudentInfo() {
        tfNumber.setText("");
        tfName.setText("");
        tfDepartment.setText("");
        
        for (JTextField field : scoreFields) {
            field.setText("");
        }
    }

    // 如果需要单独运行此窗口（主要用于测试）
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ScoreInputFrame();
        });
    }
}
