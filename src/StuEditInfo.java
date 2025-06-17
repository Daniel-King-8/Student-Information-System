import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class StuEditInfo extends JFrame implements ActionListener {
    JLabel jlnumber = new JLabel("学号:");
    JLabel jlname = new JLabel("姓名:");
    JLabel jlsex = new JLabel("性别:");  
    JLabel jlbirthday = new JLabel("出生日期:");
    JLabel jldepartment = new JLabel("学院:");
    JTextField jtnumber = new JTextField("",20);
    JTextField jtname = new JTextField("",20);
    JTextField jtsex = new JTextField("",20);
    JTextField jtbirthday = new JTextField("",20);
    JTextField jtdepartment = new JTextField("",20);
    JButton bfind = new JButton("查询");
    JButton bedit = new JButton("修改");
    JButton breturn = new JButton("返回");
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;
    
    public StuEditInfo() {
        this.setTitle("按学生学号或姓名查询并修改学生信息");
        JPanel pnorth = new JPanel();
        pnorth.setLayout(new GridLayout(5,2));
        pnorth.add(jlnumber);
        pnorth.add(jtnumber);
        pnorth.add(jlname);
        pnorth.add(jtname);
        pnorth.add(jlsex);
        pnorth.add(jtsex);
        pnorth.add(jlbirthday);
        pnorth.add(jtbirthday);
        pnorth.add(jldepartment);
        pnorth.add(jtdepartment);
        
        JPanel pcenter = new JPanel();
        pcenter.setLayout(new GridLayout(1,3));
        pcenter.add(bfind);
        pcenter.add(bedit);
        pcenter.add(breturn);
        
        bfind.addActionListener(this);
        bedit.addActionListener(this);
        breturn.addActionListener(this);
        
        this.add(pnorth , BorderLayout.NORTH);
        this.add(pcenter , BorderLayout.CENTER);

        this.setSize(new Dimension(400,180));
        int windowWidth = this.getWidth();
        int windowHeight = this.getHeight();
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        this.setLocation(screenWidth/2-windowWidth/2 , screenHeight/2-windowHeight/2);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        String number = jtnumber.getText();
        String name = jtname.getText();
        String sex = jtsex.getText().trim();
        String birthday = jtbirthday.getText();
        String department = jtdepartment.getText();
        
        if(number.length() == 0) {
            JOptionPane.showMessageDialog(this, "学号必须输入正确", "学生信息管理系统", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if(obj.equals(bfind)) {
            String sql ="select * from xuesheng where xuehao = ? or xingming=?";
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3308/student?useUnicode=true&characterEncoding=gbk",
                    "root", "8888");
                ps = conn.prepareStatement(sql);
                ps.setString(1, jtnumber.getText());
                ps.setString(2, jtname.getText());
                rs = ps.executeQuery();
                if(rs.next()) {
                    jtnumber.setText(rs.getString(1));
                    jtname.setText(rs.getString(2));
                    jtsex.setText(rs.getString(3));
                    jtbirthday.setText(rs.getString(4));
                    jtdepartment.setText(rs.getString(5));
                } else {
                    JOptionPane.showMessageDialog(this, "未找到相关学生信息", "学生信息管理系统", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch(ClassNotFoundException a) {
                System.out.println("驱动程序不存在");
                a.printStackTrace();
            } catch(SQLException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if(rs != null) rs.close();
                    if(ps != null) ps.close();
                    if(conn != null) conn.close();
                } catch(SQLException c) {
                    c.printStackTrace();
                }
            }        
        }
        
        if(obj.equals(bedit)) {
            String sql = "update xuesheng set xingming=?, xingbie=?, chushengriqi=?, xueyuan=? where xuehao=?";
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3308/student?useUnicode=true&characterEncoding=gbk",
                    "root", "8888");
                ps = conn.prepareStatement(sql);
                ps.setString(1, jtname.getText());
                ps.setString(2, jtsex.getText());
                ps.setString(3, jtbirthday.getText());
                ps.setString(4, jtdepartment.getText());
                ps.setString(5, jtnumber.getText());
                
                int rows = ps.executeUpdate();
                if(rows > 0) {
                    JOptionPane.showMessageDialog(this, "学生信息修改成功", "学生信息管理系统", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "学生信息修改失败", "学生信息管理系统", JOptionPane.ERROR_MESSAGE);
                }
            } catch(ClassNotFoundException a) {
                System.out.println("驱动程序不存在");
                a.printStackTrace();
            } catch(SQLException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if(rs != null) rs.close();
                    if(ps != null) ps.close();
                    if(conn != null) conn.close();
                } catch(SQLException c) {
                    c.printStackTrace();
                }
            }
        }
        
        if(obj.equals(breturn)) {
            this.dispose();
        }
    }
    
    public static void main(String[] args) {
        new StuEditInfo();
    }
}