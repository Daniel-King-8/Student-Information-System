import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class LoginFrame extends JFrame implements ActionListener {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin, btnCancel;
    
    public LoginFrame() {
        setTitle("学生信息系统登录");
        setLayout(new GridLayout(3, 2, 10, 10));
        setResizable(false);
        
        add(new JLabel("用户名:"));
        tfUsername = new JTextField();
        add(tfUsername);
        
        add(new JLabel("密码:"));
        pfPassword = new JPasswordField();
        add(pfPassword);
        
        btnLogin = new JButton("登录");
        btnCancel = new JButton("取消");
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancel);
        add(buttonPanel);
        
        btnLogin.addActionListener(this);
        btnCancel.addActionListener(this);
        
        pack();
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCancel) {
            System.exit(0);
        } 
        else if (e.getSource() == btnLogin) {
            String username = tfUsername.getText().trim();
            String password = new String(pfPassword.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "输入错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (validateUser(username, password)) {
                boolean isAdmin = isAdminUser(username);
                this.dispose();
                new MainFrame(isAdmin);
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateUser(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "数据库错误: " + ex.getMessage(), "系统错误", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.close(conn, pstmt, rs);
        }
    }
    
    private boolean isAdminUser(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT is_admin FROM user WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_admin");
            }
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "无法获取用户权限", "系统错误", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.close(conn, pstmt, rs);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}
