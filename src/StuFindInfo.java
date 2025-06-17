import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

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

    public StuFindInfo() {
        // 窗口设置
        this.setTitle("学生信息查询系统");
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
        
        // 添加事件监听
        bfind.addActionListener(this);
        breturn.addActionListener(this);
        bclear.addActionListener(this);
        
        // 添加组件到主面板
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
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
            clearFields();
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
            
            // 准备SQL查询
            String sql = "SELECT * FROM xuesheng WHERE xuehao = ? OR xingming = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, number);
            pstmt.setString(2, name);
            
            // 执行查询
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // 显示查询结果
                jtnumber.setText(rs.getString("xuehao"));
                jtname.setText(rs.getString("xingming"));
                jtsex.setText(rs.getString("xingbie"));
                jtbirthday.setText(rs.getString("chushengriqi"));
                jtdepartment.setText(rs.getString("xueyuan"));
            } else {
                JOptionPane.showMessageDialog(this, 
                    "未找到匹配的学生信息", 
                    "查询结果", 
                    JOptionPane.INFORMATION_MESSAGE);
                clearResultFields();
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
    
    private void clearFields() {
        jtnumber.setText("");
        jtname.setText("");
        clearResultFields();
    }
    
    private void clearResultFields() {
        jtsex.setText("");
        jtbirthday.setText("");
        jtdepartment.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StuFindInfo();
        });
    }
}
