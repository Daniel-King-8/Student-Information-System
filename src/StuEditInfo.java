import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.DateTimeException;

public class StuEditInfo extends JFrame implements ActionListener {
    private JLabel lblNumber = new JLabel("学号:");
    private JLabel lblName = new JLabel("姓名:");
    private JLabel lblSex = new JLabel("性别:");
    private JLabel lblBirthYear = new JLabel("出生年份:");
    private JLabel lblBirthMonth = new JLabel("月份:");
    private JLabel lblBirthDay = new JLabel("日期:");
    private JLabel lblDepartment = new JLabel("学院:");
    
    private JTextField txtNumber = new JTextField(20);
    private JTextField txtName = new JTextField(20);
    private JTextField txtSex = new JTextField(20);
    
    private JTextField txtBirthYear = new JTextField(4);
    private JTextField txtBirthMonth = new JTextField(2);
    private JTextField txtBirthDay = new JTextField(2);
    
    private JTextField txtDepartment = new JTextField(20);
    
    private JButton btnFind = new JButton("查找");
    private JButton btnSave = new JButton("保存");
    private JButton btnReturn = new JButton("返回");
    
    private String originalStudentId;
    private String originalDepartment;

    public StuEditInfo() {
        setTitle("编辑学生信息");
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 输入区域
        gbc.gridx = 0; gbc.gridy = 0;
        add(lblNumber, gbc);
        
        gbc.gridx = 1;
        JPanel pnlNumber = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlNumber.add(txtNumber);
        pnlNumber.add(btnFind);
        add(pnlNumber, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        add(lblName, gbc);
        
        gbc.gridx = 1;
        add(txtName, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        add(lblSex, gbc);
        
        gbc.gridx = 1;
        txtSex.setEditable(false);
        add(txtSex, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        add(lblBirthYear, gbc);
        
        gbc.gridx = 1;
        JPanel pnlBirthday = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlBirthday.add(txtBirthYear);
        pnlBirthday.add(lblBirthMonth);
        pnlBirthday.add(txtBirthMonth);
        pnlBirthday.add(lblBirthDay);
        pnlBirthday.add(txtBirthDay);
        add(pnlBirthday, gbc);
        
        addInputLimiter(txtBirthYear, 4);
        addInputLimiter(txtBirthMonth, 2);
        addInputLimiter(txtBirthDay, 2);
        
        gbc.gridx = 0; gbc.gridy = 4;
        add(lblDepartment, gbc);
        
        gbc.gridx = 1;
        add(txtDepartment, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlButtons.add(btnSave);
        pnlButtons.add(btnReturn);
        add(pnlButtons, gbc);
        
        btnFind.addActionListener(this);
        btnSave.addActionListener(this);
        btnReturn.addActionListener(this);
        btnSave.setEnabled(false);
        
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void addInputLimiter(JTextField field, int maxLength) {
        field.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (field.getText().length() >= maxLength) {
                    e.consume();
                }
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnReturn) {
            dispose();
        }
        else if (e.getSource() == btnFind) {
            findStudent();
        }
        else if (e.getSource() == btnSave) {
            saveChanges();
        }
    }
    
    private void findStudent() {
        String number = txtNumber.getText().trim();
        String name = txtName.getText().trim();
        String department = txtDepartment.getText().trim();
        
        if (number.isEmpty() && name.isEmpty() && department.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "请至少输入一项查找条件", 
                "输入错误", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            StringBuilder sqlSb = new StringBuilder("SELECT * FROM xuesheng WHERE 1=1");
            if (!number.isEmpty()) sqlSb.append(" AND xuehao = ?");
            if (!name.isEmpty()) sqlSb.append(" AND xingming = ?");
            if (!department.isEmpty()) sqlSb.append(" AND xueyuan = ?");
            
            pstmt = conn.prepareStatement(sqlSb.toString());
            int paramIndex = 1;
            if (!number.isEmpty()) pstmt.setString(paramIndex++, number);
            if (!name.isEmpty()) pstmt.setString(paramIndex++, name);
            if (!department.isEmpty()) pstmt.setString(paramIndex, department);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String dbDept = rs.getString("xueyuan");
                // 检查学院是否匹配（关键修正）
                if (!department.isEmpty() && !dbDept.equals(department)) {
                    JOptionPane.showMessageDialog(this, 
                        "姓名和学院不匹配", 
                        "查询冲突", 
                        JOptionPane.WARNING_MESSAGE);
                    clearFields();
                    return;
                }
                
                originalStudentId = rs.getString("xuehao");
                originalDepartment = dbDept;
                
                txtNumber.setText(originalStudentId);
                txtName.setText(rs.getString("xingming"));
                txtSex.setText(rs.getString("xingbie"));
                txtDepartment.setText(originalDepartment);
                
                java.sql.Date birthday = rs.getDate("chushengriqi");
                LocalDate birthDate = birthday.toLocalDate();
                txtBirthYear.setText(String.valueOf(birthDate.getYear()));
                txtBirthMonth.setText(String.valueOf(birthDate.getMonthValue()));
                txtBirthDay.setText(String.valueOf(birthDate.getDayOfMonth()));
                
                btnSave.setEnabled(true);
                
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, 
                        "找到多个匹配学生，请提供更多查询条件", 
                        "查询结果", 
                        JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "未找到匹配的学生", 
                    "查询结果", 
                    JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            }
        } catch (SQLException ex) {
            showError("数据库错误", ex);
        } catch (ClassNotFoundException ex) {
            showError("数据库驱动错误", ex);
        } finally {
            DatabaseUtil.close(conn, pstmt, rs);
        }
    }
    
    private void saveChanges() {
        String number = txtNumber.getText().trim();
        String name = txtName.getText().trim();
        String sex = txtSex.getText().trim();
        String year = txtBirthYear.getText().trim();
        String month = txtBirthMonth.getText().trim();
        String day = txtBirthDay.getText().trim();
        String department = txtDepartment.getText().trim();
        
        if (number.isEmpty() || name.isEmpty() || department.isEmpty() || 
            year.isEmpty() || month.isEmpty() || day.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "所有字段都必须填写", 
                "输入不完整", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!isValidDate(year, month, day)) {
            JOptionPane.showMessageDialog(this, 
                "出生日期格式不正确", 
                "输入错误", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        java.sql.Date birthDate = createSqlDate(year, month, day);
        if (birthDate == null) {
            JOptionPane.showMessageDialog(this, 
                "无效的出生日期", 
                "日期错误", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseUtil.getConnection();
            
            String sql = "UPDATE xuesheng SET " +
                         "xuehao = ?, " +
                         "xingming = ?, " +
                         "xingbie = ?, " +
                         "chushengriqi = ?, " +
                         "xueyuan = ? " +
                         "WHERE xuehao = ? AND xueyuan = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, number);
            pstmt.setString(2, name);
            pstmt.setString(3, sex);
            pstmt.setDate(4, birthDate);
            pstmt.setString(5, department);
            pstmt.setString(6, originalStudentId);
            pstmt.setString(7, originalDepartment);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, 
                    "学生信息更新成功", 
                    "操作成功", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                originalStudentId = number;
                originalDepartment = department;
            } else {
                JOptionPane.showMessageDialog(this, 
                    "没有找到匹配的学生记录或数据未更改", 
                    "更新失败", 
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            if (ex.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(this, 
                    "该学号在本学院已存在: [" + number + "]", 
                    "学号冲突", 
                    JOptionPane.ERROR_MESSAGE);
            } else {
                showError("数据库约束错误", ex);
            }
        } catch (SQLException ex) {
            showError("数据库错误", ex);
        } catch (ClassNotFoundException ex) {
            showError("数据库驱动错误", ex);
        } finally {
            DatabaseUtil.close(conn, pstmt, null);
        }
    }
    
    private boolean isValidDate(String yearStr, String monthStr, String dayStr) {
        try {
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            int day = Integer.parseInt(dayStr);
            LocalDate.of(year, month, day);
            return true;
        } catch (NumberFormatException | DateTimeException e) {
            return false;
        }
    }
    
    private java.sql.Date createSqlDate(String yearStr, String monthStr, String dayStr) {
        try {
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            int day = Integer.parseInt(dayStr);
            String dateStr = String.format("%04d-%02d-%02d", year, month, day);
            return java.sql.Date.valueOf(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
    
    private void clearFields() {
        txtNumber.setText("");
        txtName.setText("");
        txtSex.setText("");
        txtBirthYear.setText("");
        txtBirthMonth.setText("");
        txtBirthDay.setText("");
        txtDepartment.setText("");
        btnSave.setEnabled(false);
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
        SwingUtilities.invokeLater(StuEditInfo::new);
    }
}
