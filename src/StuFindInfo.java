
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class StuFindInfo extends Jframe implements ActionListener {

    JLabel jlnumber = new JLabel("学号:");
    JLabel jlname = new JLabel("姓名:");
    JLabel jlsex = new JLabel("性别:");
    JLabel jlbirthday = new JLabel("出生日期:");
    JLabel jldepartemnt = new JLabel("学院:");
    JTextField jtnumber = new JTextField("", 20);
    JTextField jtname = new JTextField("", 20);
    JTextField jtsex = new JTextField();
    JTextField jtbirthday = new JTextField();
    JTextField jtdepartment = new JTextField();
    JButton bfind = new JButton("查询");
    JButton breturn = new JButton("返回");
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;

    public StuFindInfo() {
        this.setTitle("按学号或姓名查询学生信息");
        this.setLayout(new GridLayout(6, 2));
        this.add(jlnumber);
        this.add(jtnumber);
        this.add(jlname);
        this.add(jtname);
        this.add(jlsex);
        this.add(jtsex);
        this.add(jlbirthday);
        this.add(jtbirthday);
        this.add(jldepartment);
        this.add(jtdepartment);
        this.add(bfind);
        this.add(breturn);
        bfind.assActionListener(this);
        breturn.addActionListener(this);
        //设置窗口大小
        this.setSize(new Dimension(400, 250));
        int windowWidth = this.getWidth();//获得窗口宽
        int windowHeight = this.getHeight();//获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit();//定义工具包
        Dimension screenSize = kit.getScreenSize();//获取屏幕的尺寸
        int screenWidth = screenSize.width;//获取屏幕的宽
        int screenHeight = screenSize.height;//获取屏幕的高
        //设置窗口居中
        this.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);
        //设置窗口居中显示
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj.equals(bfind)) {
            String xh = jtnumber.getText();
            String xm = jtname.getText();
            if (xh.length() == 0 && xm.length() == 0) {
                JOptionPane.showMessageDialog(this, "学号或姓名不能为空", "学生信息管理系统", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String sql = "select * from xuesheng where xuehao = ? or xingming = ?;";
            try {
                Class.forName("com.mysql. jdbc.Driver");
                conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3308/student?
                    useUnicode = true & characterEncoding = gbk","root
                ","8888");
                    ps = conn.prepareStatement(sql);
                ps.setString(1, jtnumber.getText());
                ps.setString(2, jtname.getText());
                rs = ps.executeQuery();
                while (rs.next()) {
                    jtnumber.setText(rs.getString(1));
                    jtname.setText(rs.getString(2));
                    jtsex.setText(rs.getString(3));
                    jtbirthday.setText(rs.getString(4));
                    jtdepartment.setText(rs.getString(5));
                }
            } catch (ClassNotFoundException a) {
                System.out.println("驱动程序不存在");
                a.printStackTrace();
            } catch (SQLException b) {
                b.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException c) {
                    c.printStackTrace();
                }
            }
        }
        if (obj.equals(breturn)) {
            this.dispose();
        }
    }

}
