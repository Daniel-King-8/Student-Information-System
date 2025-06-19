import java.sql.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

public class StuBrowse extends JFrame {
    private JTable jTable;
    private JScrollPane jScrollPane;
    private Vector<String> columnNames = new Vector<>();
    private Vector<Vector<String>> rowData = new Vector<>();
    private JButton btnRefresh = new JButton("刷新");
    
    public StuBrowse() {
        setTitle("浏览学生信息");
        setLayout(new BorderLayout(10, 10));
        
        // 创建表头
        columnNames.add("学号");
        columnNames.add("姓名");
        columnNames.add("性别");
        columnNames.add("出生日期");
        columnNames.add("学院");
        
        // 创建表格
        jTable = new JTable(rowData, columnNames);
        jScrollPane = new JScrollPane(jTable);
        
        // 添加刷新按钮
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(btnRefresh);
        
        // 添加组件
        add(topPanel, BorderLayout.NORTH);
        add(jScrollPane, BorderLayout.CENTER);
        
        // 设置窗口属性
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        
        // 加载数据
        loadData();
        
        // 添加刷新按钮事件
        btnRefresh.addActionListener(e -> loadData());
    }
    
    private void loadData() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        // 清除现有数据
        rowData.clear();
        
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement("SELECT * FROM xuesheng");
            rs = ps.executeQuery();
            
            while(rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("xuehao"));
                row.add(rs.getString("xingming"));
                row.add(rs.getString("xingbie"));
                
                // 转换日期格式为中文显示
                java.sql.Date birthday = rs.getDate("chushengriqi");
                row.add(DateUtil.convertToChineseDate(birthday));
                
                row.add(rs.getString("xueyuan"));
                rowData.add(row);
            }
            
            // 更新表格
            jTable.repaint();
            jTable.updateUI();
            
            JOptionPane.showMessageDialog(this, 
                "加载了 " + rowData.size() + " 条学生记录", 
                "数据加载完成", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, 
                "加载数据失败: " + e.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, ps, rs);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StuBrowse::new);
    }
}
