import java.sql.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

public class StuBrowse extends JFrame {
    JTable jTable;
    JScrollPane jScrollPane = new JScrollPane();
    Vector<String> columnNames = new Vector<>();
    Vector<Vector<String>> rowData = new Vector<>();
    
    public StuBrowse() {
        columnNames.add("学号");
        columnNames.add("姓名");
        columnNames.add("性别");
        columnNames.add("出生日期");
        columnNames.add("学院");
        
        jTable = new JTable(rowData, columnNames);
        jScrollPane = new JScrollPane(jTable);
        this.add(jScrollPane);
        this.setTitle("浏览学生信息");
        this.setLayout(new BorderLayout());
        this.add(jScrollPane, BorderLayout.CENTER);
        
        this.setSize(500, 300);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        this.setLocation(screenSize.width/2 - 250, screenSize.height/2 - 150);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
        
        loadData();
    }
    
    private void loadData() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement("select * from xuesheng");
            rs = ps.executeQuery();
            
            rowData.clear();
            while(rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString(1));
                row.add(rs.getString(2));
                row.add(rs.getString(3));
                row.add(rs.getString(4));
                row.add(rs.getString(5));
                rowData.add(row);
            }
            jTable.updateUI();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "加载数据失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, ps, rs);
        }
    }
}