import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class UserManagementFrame extends JFrame implements ActionListener {
    private JTextField tfUsername, tfPassword;
    private JCheckBox chkAdmin;
    private JButton btnAdd, btnClear;
    private JTable userTable;
    private DefaultTableModel tableModel;
    
    public UserManagementFrame() {
        setTitle("账号管理");
        setLayout(new BorderLayout(10, 10));
        
        // 添加用户表单
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        tfUsername = new JTextField();
        tfPassword = new JTextField();
        chkAdmin = new JCheckBox();
        
        formPanel.add(new JLabel("用户名:"));
        formPanel.add(tfUsername);
        formPanel.add(new JLabel("密码:"));
        formPanel.add(tfPassword);
        formPanel.add(new JLabel("管理员:"));
        formPanel.add(chkAdmin);
        
        btnAdd = new JButton("添加账号");
        btnClear = new JButton("清空");
        formPanel.add(btnAdd);
        formPanel.add(btnClear);
        
        add(formPanel, BorderLayout.NORTH);
        
        // 用户列表表格
        tableModel = new DefaultTableModel(new Object[]{"用户名", "管理员"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };
        
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // 删除按钮
        JButton btnDelete = new JButton("删除选中账号");
        btnDelete.addActionListener(this);
        JPanel southPanel = new JPanel();
        southPanel.add(btnDelete);
        add(southPanel, BorderLayout.SOUTH);
        
        btnAdd.addActionListener(this);
        btnClear.addActionListener(this);
        
        loadUsers();
        
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    private void loadUsers() {
        tableModel.setRowCount(0); // 清空表格
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM user")) {
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("username"),
                    rs.getBoolean("is_admin") ? "是" : "否"
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            addUser();
            loadUsers();
        } else if (e.getSource() == btnClear) {
            tfUsername.setText("");
            tfPassword.setText("");
            chkAdmin.setSelected(false);
        } else {
            deleteSelectedUsers();
        }
    }
    
    private void addUser() {
        String username = tfUsername.getText().trim();
        String password = tfPassword.getText().trim();
        boolean isAdmin = chkAdmin.isSelected();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空");
            return;
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO user (username, password, is_admin) VALUES (?, ?, ?)")) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setBoolean(3, isAdmin);
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "账号添加成功");
            tfUsername.setText("");
            tfPassword.setText("");
            chkAdmin.setSelected(false);
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "用户名已存在", "添加失败", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "添加失败: " + ex.getMessage());
        }
    }
    
    private void deleteSelectedUsers() {
        int[] selectedRows = userTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的账号");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "确定删除选中的 " + selectedRows.length + " 个账号吗？",
            "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM user WHERE username = ?")) {
                
                for (int row : selectedRows) {
                    String username = (String) tableModel.getValueAt(row, 0);
                    pstmt.setString(1, username);
                    pstmt.addBatch();
                }
                
                pstmt.executeBatch();
                loadUsers();
                JOptionPane.showMessageDialog(this, "删除成功");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "删除失败: " + ex.getMessage());
            }
        }
    }
}
