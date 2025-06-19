import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.time.DateTimeException;
import java.time.LocalDate;

public class StuAddInfo extends JFrame implements ActionListener {
    private final int FIELD_WIDTH = 5;
    
    private JLabel lblNumber = new JLabel("学号:");
    private JLabel lblName = new JLabel("姓名:");
    private JLabel lblSex = new JLabel("性别:");
    private JLabel lblBirthYear = new JLabel("出生年份:");
    private JLabel lblBirthMonth = new JLabel("月份:");
    private JLabel lblBirthDay = new JLabel("日期:");
    private JLabel lblDepartment = new JLabel("学院:");
    
    private JTextField txtNumber = new JTextField(20);
    private JTextField txtName = new JTextField(20);
    
    private JRadioButton radMale = new JRadioButton("男");
    private JRadioButton radFemale = new JRadioButton("女");
    private ButtonGroup btnGroupSex = new ButtonGroup();
    
    private JTextField txtBirthYear = new JTextField(FIELD_WIDTH);
    private JTextField txtBirthMonth = new JTextField(FIELD_WIDTH);
    private JTextField txtBirthDay = new JTextField(FIELD_WIDTH);
    
    private JTextField txtDepartment = new JTextField(20);
    
    private JButton btnAdd = new JButton("添加");
    private JButton btnReturn = new JButton("返回");

    public StuAddInfo() {
        setTitle("添加学生信息");
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        
        // 第1行 - 学号
        gbc.gridx = 0; gbc.gridy = 0;
        add(lblNumber, gbc);
        
        gbc.gridx = 1;
        txtNumber.setToolTipText("允许不同学院有相同学号");
        add(txtNumber, gbc);
        
        // 第2行 - 姓名
        gbc.gridx = 0; gbc.gridy = 1;
        add(lblName, gbc);
        
        gbc.gridx = 1;
        add(txtName, gbc);
        
        // 第3行 - 性别
        gbc.gridx = 0; gbc.gridy = 2;
        add(lblSex, gbc);
        
        gbc.gridx = 1;
        JPanel pnlGender = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        radMale.setSelected(true);
        btnGroupSex.add(radMale);
        btnGroupSex.add(radFemale);
        pnlGender.add(radMale);
        pnlGender.add(radFemale);
        add(pnlGender, gbc);
        
        // 第4行 - 出生日期
        gbc.gridx = 0; gbc.gridy = 3;
        add(lblBirthYear, gbc);
        
        gbc.gridx = 1;
        JPanel pnlBirthday = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlBirthday.add(txtBirthYear);
        pnlBirthday.add(lblBirthMonth);
        pnlBirthday.add(txtBirthMonth);
        pnlBirthday.add(lblBirthDay);
        pnlBirthday.add(txtBirthDay);
        
        // 添加日期输入格式提示
        txtBirthYear.setToolTipText("请输入4位年份（如：2000）");
        txtBirthMonth.setToolTipText("请输入月份（1-12）");
        txtBirthDay.setToolTipText("请输入日期（1-31）");
        addInputLimiter(txtBirthYear, 4); // 限制年份最多4位
        addInputLimiter(txtBirthMonth, 2); // 限制月份最多2位
        addInputLimiter(txtBirthDay, 2); // 限制日期最多2位
        
        // 添加自动跳转和补零功能
        addDateNavigation();
        
        add(pnlBirthday, gbc);
        
        // 第5行 - 学院
        gbc.gridx = 0; gbc.gridy = 4;
        add(lblDepartment, gbc);
        
        gbc.gridx = 1;
        add(txtDepartment, gbc);
        
        // 第6行 - 按钮
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlButtons.add(btnAdd);
        pnlButtons.add(btnReturn);
        add(pnlButtons, gbc);
        
        btnAdd.addActionListener(this);
        btnReturn.addActionListener(this);
        
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /** 添加输入长度限制 */
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

    /** 添加日期字段之间的自动跳转逻辑 */
    private void addDateNavigation() {
        txtBirthYear.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (txtBirthYear.getText().length() == 4 && 
                    e.getKeyCode() != KeyEvent.VK_BACK_SPACE &&
                    e.getKeyCode() != KeyEvent.VK_DELETE) {
                    txtBirthMonth.requestFocus();
                }
            }
        });
        
        txtBirthMonth.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (txtBirthMonth.getText().length() == 2 && 
                    e.getKeyCode() != KeyEvent.VK_BACK_SPACE &&
                    e.getKeyCode() != KeyEvent.VK_DELETE) {
                    txtBirthDay.requestFocus();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnReturn) {
            dispose();
            return;
        }
        
        if (e.getSource() == btnAdd) {
            String number = txtNumber.getText().trim();
            String name = txtName.getText().trim();
            String year = txtBirthYear.getText().trim();
            String month = txtBirthMonth.getText().trim();
            String day = txtBirthDay.getText().trim();
            String sex = radMale.isSelected() ? "男" : "女";
            String department = txtDepartment.getText().trim();
            
            // 验证输入
            StringBuilder errorMsgs = new StringBuilder();
            if (number.isEmpty()) errorMsgs.append("学号不能为空\n");
            if (name.isEmpty()) errorMsgs.append("姓名不能为空\n");
            if (department.isEmpty()) errorMsgs.append("学院不能为空\n");
            
            if (year.isEmpty() || month.isEmpty() || day.isEmpty()) {
                errorMsgs.append("出生日期不能为空\n");
            } 
            else if (!isValidDate(year, month, day)) {
                errorMsgs.append("出生日期格式不正确\n");
            }
            
            if (errorMsgs.length() > 0) {
                JOptionPane.showMessageDialog(this, errorMsgs.toString(), "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 创建SQL日期
            java.sql.Date birthDate = createSqlDate(year, month, day);
            if (birthDate == null) {
                JOptionPane.showMessageDialog(this, "无效的出生日期", "日期错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 保存到数据库
            Connection conn = null;
            PreparedStatement pstmt = null;
            try {
                conn = DatabaseUtil.getConnection();
                String sql = "INSERT INTO xuesheng (xuehao, xingming, xingbie, chushengriqi, xueyuan) " +
                             "VALUES (?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, number);
                pstmt.setString(2, name);
                pstmt.setString(3, sex);
                pstmt.setDate(4, birthDate);
                pstmt.setString(5, department);
                
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "学生 [" + name + "] 信息添加成功", 
                        "操作成功", 
                        JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
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
    }
    
    /** 验证日期是否有效 */
    private boolean isValidDate(String yearStr, String monthStr, String dayStr) {
        try {
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            int day = Integer.parseInt(dayStr);
            
            // 使用Java 8日期API验证
            LocalDate.of(year, month, day); // 会自动验证有效性
            return true;
        } catch (NumberFormatException | DateTimeException e) {
            return false;
        }
    }
    
    /** 创建SQL日期对象 */
    private java.sql.Date createSqlDate(String yearStr, String monthStr, String dayStr) {
        try {
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            int day = Integer.parseInt(dayStr);
            
            // 确保月份和日期补零
            String dateStr = String.format("%04d-%02d-%02d", year, month, day);
            return java.sql.Date.valueOf(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
    
    /** 清空输入字段 */
    private void clearFields() {
        txtNumber.setText("");
        txtName.setText("");
        txtBirthYear.setText("");
        txtBirthMonth.setText("");
        txtBirthDay.setText("");
        txtDepartment.setText("");
        radMale.setSelected(true);
    }
    
    /** 显示错误消息 */
    private void showError(String title, Exception ex) {
        String errorMsg = title + ": " + ex.getMessage();
        if (ex instanceof SQLException) {
            errorMsg += "\nSQL状态: " + ((SQLException) ex).getSQLState();
        }
        JOptionPane.showMessageDialog(this, errorMsg, "系统错误", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StuAddInfo::new);
    }
}
