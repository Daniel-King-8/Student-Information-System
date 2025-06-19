import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class StuFindInfo extends JFrame implements ActionListener {
    // 组件声明
    private JLabel jlnumber = new JLabel("学号:");
    private JLabel jlname = new JLabel("姓名:");
    private JLabel jlsex = new JLabel("性别:");
    private JLabel jlbirthday = new JLabel("出生日期:");
    private JLabel jldepartment = new JLabel("学院:");
    
    private JTextField jtnumber = new JTextField(20);
    private JTextField jtname = new JTextField(20);
    private JTextField jtsex = new JTextField(20);
    private JTextField jtbirthday = new JTextField(20);
    private JTextField jtdepartment = new JTextField(20);
    
    private JButton bfind = new JButton("查询");
    private JButton breturn = new JButton("返回");
    private JButton bclear = new JButton("清空");
    
    // 新增成绩表格组件
    private JTable scoreTable;
    private JScrollPane scoreScrollPane;
    private final String[] scoreColumns = {"语文", "高数", "英语", "Java", "Go", "Linux", "双创", "思政", "实训"};
    private DefaultTableModel scoreModel;

    public StuFindInfo() {
        // 窗口设置
        this.setTitle("学生信息与成绩查询系统");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // 主面板布局
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 输入面板
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.add(jlnumber);
        inputPanel.add(jtnumber);
        inputPanel.add(jlname);
        inputPanel.add(jtname);
        inputPanel.add(jlsex);
        inputPanel.add(jtsex);
        inputPanel.add(jlbirthday);
        inputPanel.add(jtbirthday);
        inputPanel.add(jldepartment);
        inputPanel.add(jtdepartment);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(bfind);
        buttonPanel.add(bclear);
        buttonPanel.add(breturn);
        
        // 禁用结果字段
        jtsex.setEditable(false);
        jtbirthday.setEditable(false);
        jtdepartment.setEditable(false);
        
        // 创建成绩显示区域
        JPanel scorePanel = new JPanel(new BorderLayout(5, 5));
        scorePanel.setBorder(BorderFactory.createTitledBorder("成绩信息"));
        scoreModel = new DefaultTableModel(null, scoreColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 使表格不可编辑
            }
        };
        scoreTable = new JTable(scoreModel);
        scoreScrollPane = new JScrollPane(scoreTable);
        scorePanel.add(scoreScrollPane, BorderLayout.CENTER);
        
        // 添加事件监听
        bfind.addActionListener(this);
        breturn.addActionListener(this);
        bclear.addActionListener(this);
        
        // 添加组件到主面板
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(scorePanel, BorderLayout.SOUTH);
        
        this.add(mainPanel);
        this.pack();
        
        // 窗口居中
        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == bfind) {
            findStudentInfo();
        } 
        else if (source == bclear) {
            clearAllFields();
        } 
        else if (source == breturn) {
            this.dispose();
        }
    }
    
    private void findStudentInfo() {
        String number = jtnumber.getText().trim();
        String name = jtname.getText().trim();
        
        // 验证输入
        if (number.isEmpty() && name.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "请输入学号或姓名至少一项", 
                "输入错误", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // 获取数据库连接
            conn = DatabaseUtil.getConnection();
            
            // 准备SQL查询学生基本信息
            String sql = "SELECT * FROM xuesheng WHERE xuehao = ? OR xingming = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, number);
            pstmt.setString(2, name);
            
            // 执行查询
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // 显示查询结果
                String studentId = rs.getString("xuehao");
                jtnumber.setText(studentId);
                jtname.setText(rs.getString("xingming"));
                jtsex.setText(rs.getString("xingbie"));
                jtbirthday.setText(rs.getString("chushengriqi"));
                jtdepartment.setText(rs.getString("xueyuan"));
                
                // 查询该学生的成绩
                loadStudentScores(studentId);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "未找到匹配的学生信息", 
                    "查询结果", 
                    JOptionPane.INFORMATION_MESSAGE);
                clearAllFields();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "数据库错误: " + ex.getMessage(), 
                "系统错误", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, 
                "数据库驱动加载失败", 
                "系统错误", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, pstmt, rs);
        }
    }
    
    private void loadStudentScores(String studentId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            // 查询成绩信息
            // 特别注意：使用反引号包裹中文字段名（MySQL语法）
            String scoreSql = "SELECT `语文`, `高数`, `英语`, `Java`, `Go`, `Linux`, `双创`, `思政`, `实训` " +
                             "FROM scores WHERE student_id = ?";
            pstmt = conn.prepareStatement(scoreSql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            // 初始化模型
            scoreModel.setRowCount(0);
            
            if (rs.next()) {
                // 创建单行数据
                Object[] rowData = new Object[scoreColumns.length];
                
                // 读取各科目成绩
                for (int i = 0; i < scoreColumns.length; i++) {
                    Object value = rs.getObject(scoreColumns[i]);
                    rowData[i] = (value != null) ? String.format("%.1f", rs.getFloat(scoreColumns[i])) : "无";
                }
                
                // 添加到表格
                scoreModel.addRow(rowData);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "该学生暂无成绩记录", 
                    "成绩查询", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "成绩查询失败: " + ex.getMessage(), 
                "数据库错误", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, 
                "数据库驱动加载失败", 
                "系统错误", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, pstmt, rs);
        }
    }
    
    private void clearAllFields() {
        // 清空基本信息字段
        jtnumber.setText("");
        jtname.setText("");
        jtsex.setText("");
        jtbirthday.setText("");
        jtdepartment.setText("");
        
        // 清空成绩表
        scoreModel.setRowCount(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StuFindInfo();
        });
    }
}
