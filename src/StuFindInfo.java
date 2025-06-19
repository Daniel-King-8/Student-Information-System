import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class StuFindInfo extends JFrame implements ActionListener {
    // 查询条件组件
    private JPanel searchPanel;
    private JLabel lblNumber = new JLabel("学号:");
    private JLabel lblName = new JLabel("姓名:"); 
    private JLabel lblDepartment = new JLabel("学院:");
    private JTextField txtNumber = new JTextField(12);
    private JTextField txtName = new JTextField(12);
    private JTextField txtDepartment = new JTextField(12);
    private JButton btnFind = new JButton("查询");
    private JButton btnClear = new JButton("清空");
    private JButton btnReturn = new JButton("返回");

    // 学生信息展示组件
    private JPanel infoPanel;
    private JTextField txtFoundNumber = new JTextField(15);
    private JTextField txtFoundName = new JTextField(15);
    private JTextField txtFoundSex = new JTextField(8);
    private JTextField txtFoundBirthday = new JTextField(12);
    private JTextField txtFoundDepartment = new JTextField(15);

    // 成绩表格组件
    private JTable tblScores;
    private JScrollPane scrScores;
    private final String[] SCORE_COLUMNS = {"语文", "高数", "英语", "Java", "Go", "Linux", "双创", "思政", "实训"};
    private DefaultTableModel scoreModel;

    public StuFindInfo() {
        setTitle("学生信息与成绩查询");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // 1. 创建查询面板 ---------------------------------
        searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("查询条件"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 第一行：学号
        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(lblNumber, gbc);
        gbc.gridx = 1;
        searchPanel.add(txtNumber, gbc);

        // 第二行：姓名
        gbc.gridx = 0; gbc.gridy = 1;
        searchPanel.add(lblName, gbc);
        gbc.gridx = 1;
        searchPanel.add(txtName, gbc);

        // 第三行：学院
        gbc.gridx = 0; gbc.gridy = 2;
        searchPanel.add(lblDepartment, gbc);
        gbc.gridx = 1;
        searchPanel.add(txtDepartment, gbc);

        // 按钮面板
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnPanel.add(btnFind);
        btnPanel.add(btnClear);
        btnPanel.add(btnReturn);
        searchPanel.add(btnPanel, gbc);

        // 2. 创建信息展示面板 ------------------------------
        infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("学生信息"));
        infoPanel.setPreferredSize(new Dimension(600, 150));
        
        // 配置GridBag布局
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 第一行：学号+姓名
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("学号:"), gbc);
        gbc.gridx = 1;
        txtFoundNumber.setEditable(false);
        txtFoundNumber.setBackground(new Color(240, 240, 240));
        infoPanel.add(txtFoundNumber, gbc);

        gbc.gridx = 2;
        infoPanel.add(new JLabel("姓名:"), gbc); 
        gbc.gridx = 3;
        txtFoundName.setEditable(false);
        infoPanel.add(txtFoundName, gbc);

        // 第二行：性别+出生日期
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("性别:"), gbc);
        gbc.gridx = 1;
        txtFoundSex.setEditable(false);
        infoPanel.add(txtFoundSex, gbc);

        gbc.gridx = 2;
        infoPanel.add(new JLabel("出生日期:"), gbc);
        gbc.gridx = 3;
        txtFoundBirthday.setEditable(false);
        infoPanel.add(txtFoundBirthday, gbc);

        // 第三行：学院
        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("学院:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        txtFoundDepartment.setEditable(false);
        infoPanel.add(txtFoundDepartment, gbc);

        // 3. 创建成绩表格 --------------------------------
        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.setBorder(BorderFactory.createTitledBorder("成绩记录"));
        
        scoreModel = new DefaultTableModel(null, SCORE_COLUMNS) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblScores = new JTable(scoreModel);
        tblScores.setPreferredScrollableViewportSize(new Dimension(650, 200));
        tblScores.setFillsViewportHeight(true);
        scrScores = new JScrollPane(tblScores);
        scorePanel.add(scrScores, BorderLayout.CENTER);

        // 组装主界面 -------------------------------------
        add(searchPanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
        add(scorePanel, BorderLayout.SOUTH);

        // 事件监听
        btnFind.addActionListener(this);
        btnClear.addActionListener(this);
        btnReturn.addActionListener(this);

        setSize(700, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnFind) {
            findStudentInfo();
        } 
        else if (e.getSource() == btnClear) {
            clearAllFields();
        } 
        else if (e.getSource() == btnReturn) {
            this.dispose();
        }
    }
    
    private void findStudentInfo() {
        String number = txtNumber.getText().trim();
        String name = txtName.getText().trim();
        String department = txtDepartment.getText().trim();
        
        if (number.isEmpty() && name.isEmpty() && department.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "请输入学号、姓名或学院至少一项查询条件", 
                "输入错误", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            // 构建动态SQL
            StringBuilder sql = new StringBuilder(
                "SELECT xuehao, xingming, xingbie, chushengriqi, xueyuan " +
                "FROM xuesheng WHERE ");
            
            if (!number.isEmpty()) sql.append("xuehao = ? ");
            if (!name.isEmpty()) {
                if (!number.isEmpty()) sql.append("OR ");
                sql.append("xingming = ? ");
            }
            if (!department.isEmpty()) {
                if (!number.isEmpty() || !name.isEmpty()) sql.append("OR ");
                sql.append("xueyuan = ?");
            }
            
            pstmt = conn.prepareStatement(sql.toString());
            
            int paramIndex = 1;
            if (!number.isEmpty()) pstmt.setString(paramIndex++, number);
            if (!name.isEmpty()) pstmt.setString(paramIndex++, name);
            if (!department.isEmpty()) pstmt.setString(paramIndex, department);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // 填充学生信息
                txtFoundNumber.setText(rs.getString("xuehao"));
                txtFoundName.setText(rs.getString("xingming"));
                txtFoundSex.setText(rs.getString("xingbie"));
                txtFoundBirthday.setText(
                    DateUtil.convertToChineseDate(rs.getDate("chushengriqi")));
                txtFoundDepartment.setText(rs.getString("xueyuan"));
                
                // 加载成绩
                loadStudentScores(rs.getString("xuehao"), rs.getString("xueyuan"));
                
                // 检查是否有多条记录
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, 
                        "找到多个匹配学生，仅显示第一条结果", 
                        "查询提示", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "未找到匹配的学生信息", 
                    "查询结果", 
                    JOptionPane.INFORMATION_MESSAGE);
                clearDetails();
            }
        } catch (SQLException ex) {
            showError("数据库错误", ex);
        } catch (ClassNotFoundException ex) {
            showError("数据库驱动错误", ex);
        } finally {
            DatabaseUtil.close(conn, pstmt, rs);
        }
    }
    
    private void loadStudentScores(String studentId, String department) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM scores WHERE student_id = ? AND xueyuan = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            pstmt.setString(2, department);
            rs = pstmt.executeQuery();
            
            // 清空现有数据
            scoreModel.setRowCount(0);
            
            if (rs.next()) {
                Object[] rowData = new Object[SCORE_COLUMNS.length];
                
                for (int i = 0; i < SCORE_COLUMNS.length; i++) {
                    Object value = rs.getObject(SCORE_COLUMNS[i]);
                    rowData[i] = (value == null) ? "无记录" : String.format("%.1f", rs.getFloat(SCORE_COLUMNS[i]));
                }
                
                scoreModel.addRow(rowData);
            } else {
                Object[] rowData = new Object[SCORE_COLUMNS.length];
                rowData[0] = "暂无成绩记录";
                scoreModel.addRow(rowData);
            }
        } catch (SQLException ex) {
            showError("成绩查询错误", ex);
            scoreModel.setRowCount(0);
            Object[] rowData = new Object[SCORE_COLUMNS.length];
            rowData[0] = "加载失败";
            scoreModel.addRow(rowData);
        } catch (ClassNotFoundException ex) {
            showError("数据库驱动错误", ex);
        } finally {
            DatabaseUtil.close(conn, pstmt, rs);
        }
    }
    
    private void clearDetails() {
        txtFoundNumber.setText("");
        txtFoundName.setText("");
        txtFoundSex.setText("");
        txtFoundBirthday.setText("");
        txtFoundDepartment.setText("");
        scoreModel.setRowCount(0);
    }
    
    private void clearAllFields() {
        txtNumber.setText("");
        txtName.setText("");
        txtDepartment.setText("");
        clearDetails();
    }
    
    private void showError(String title, Exception ex) {
        String errorMsg = title + ": " + ex.getMessage();
        if (ex instanceof SQLException) {
            errorMsg += "\nSQL状态: " + ((SQLException) ex).getSQLState();
        }
        JOptionPane.showMessageDialog(this, errorMsg, "系统错误", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StuFindInfo::new);
    }
}
