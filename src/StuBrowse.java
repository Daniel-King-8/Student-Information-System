import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
public class StuBrowse extends JFrame {
    Connection conn = null; 
    PreparedStatement ps = null;
    ResultSet rs = null;
    JTable jTable;
    JScrollPane jScrollPane = new JScrollPane();
    Vector columnNames = null;
    Vector rowData = null;
    public StuBrowse() {
        JPanel jpforbutton = new JPanel(new GridLayout(1,1));
        columnNames = new Vector();
        columnNames.add("学号");
        columnNames.add("姓名");
        columnNames.add("性别");
        columnNames.add("出生日期");
        columnNames.add("学院");
        rowData = new Vector();
        jTable = new JTable(rowData, columnNames);
        jScrollPane = new JScrollPane(jTable);
        this.add(jScrollPane);
        this.setTitle("浏览学生信息");
        this.setLayout(new GridLayout(2,5));
        this.add(jpforbutton);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocation(300, 300);
        //设置窗口大小
        this.setSize(new Dimension(500,300));
        int windowWidth = this.getWidth();//获得窗口宽
        int windowHeight = this.getHeight();//获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit();//定义工具包
        Dimension screenSize = kit.getScreenSize();//获取屏幕的尺寸
        int screenWidth = screenSize.width;//获取屏幕的宽
        int screenHeight = screenSize.height;//获取屏幕的高
        //设置窗口居中
        this.setLocation(screenWidth/2-windowWidth/2,
                screenHeight/2-windowHeight/2);//设置窗口居中显示
        this.setVisible(true);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3308/stubrowse?useUnicode=true&characterEncoding=gbk","root", "8888");
            ps = conn.prepareStatement("select * from xuesheng");
            rs = ps.executeQuery();
            while(rs.next())
            {System.out.println("ok");
               Vector hang = new Vector();
               hang.add(rs.getString(1));
               hang.add(rs.getString(2));
               hang.add(rs.getString(3));
               hang.add(rs.getString(4));
               hang.add(rs.getString(5));
               rowData.add(hang);
            }
            DefaultTableModel tableModel = new DefaultTableModel(rowData, columnNames);
            jTable.setModel(tableModel);//JTable 对象设置 DefaultTableModle
        }catch(Exception q){
            q.printStackTrace();
        }
        finally{
            try{
                rs.close();
                ps.close();
                conn.close();
                System.out.println("关闭数据库成功");
            }catch (SQLException o){
                o.printStackTrace();
            }
        }
    }
}