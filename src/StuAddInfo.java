import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class StuAddInfo extends JFrame implements ActionListener {
    JLabel jlnumber = new JLabel("学号:");
    JLabel jlname = new JLabel("姓名:");
    JLabel jlsex = new JLabel("性别:");
    JLabel jlbirthday = new JLabel("出生日期:");
    JLabel jldepartment = new JLabel("学院:");
    JTextField jtnumber = new JTextField("", 20);
    JTextField jtname = new JTextField("", 20);
    JRadioButton jsexman = new JRadioButton("男");
    JRadioButton jsexwoman = new JRadioButton("女");
    ButtonGroup bgr = new ButtonGroup();
    JTextField jtsex = new JTextField("", 20);
    JTextField jtbirthday = new JTextField("", 20);
    JTextField jtdepartment = new JTextField("", 20);
    JButton badd = new JButton("添加");
    JButton breturn = new JButton("返回");

    public StuAddInfo() {
        this.setTitle("添加学生信息");
        JPanel pnorth = new JPanel();
        pnorth.setLayout(new GridLayout(2, 2));
        pnorth.add(jlnumber);
        pnorth.add(jtnumber);
        pnorth.add(jlname);
        pnorth.add(jtname);

        JPanel pcenter = new JPanel();
        pcenter.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pcenter.add(jlsex);
        jsexman.setSelected(true);
        bgr.add(jsexman);
        bgr.add(jsexwoman);
        pcenter.add(jsexman);
        pcenter.add(jsexwoman);

        JPanel psouth = new JPanel();
        psouth.setLayout(new GridLayout(4, 2));
        psouth.add(jlbirthday);
        psouth.add(jtbirthday);
        psouth.add(jldepartment);
        psouth.add(jtdepartment);
        psouth.add(badd);
        psouth.add(breturn);

        badd.addActionListener(this);
        breturn.addActionListener(this);

        this.add(pnorth, BorderLayout.NORTH);
        this.add(pcenter, BorderLayout.CENTER);
        this.add(psouth, BorderLayout.SOUTH);

        // 设置窗口大小
        this.setSize(new Dimension(400, 230));
        int windowWidth = this.getWidth(); // 获得窗口宽
        int windowHeight = this.getHeight(); // 获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
        Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
        int screenWidth = screenSize.width; // 获取屏幕的宽
        int screenHeight = screenSize.height; // 获取屏幕的高

        // 设置窗口居中
        this.setLocation(screenWidth/2 - windowWidth/2, screenHeight/2 - windowHeight/2); // 设置窗口居中显示
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj.equals(badd)) {
            Connection conn = null;
            Statement stat = null;
            PreparedStatement ps = null;
            String sql = "insert into xuesheng values(?,?,?,?,?)";
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3308/student?useUnicode=true&characterEncoding=utf8", "root", "8888");
                ps = conn.prepareStatement(sql);
                ps.setString(1, jtnumber.getText());
                ps.setString(2, jtname.getText());
                // 获取性别
                String sex = "";
                if (jsexman.isSelected()) { sex = "男"; }
                if (jsexwoman.isSelected()) { sex = "女"; }
                ps.setString(3, sex);
                ps.setString(4, jtbirthday.getText());
                ps.setString(5, jtdepartment.getText());

                int i = ps.executeUpdate();
                if (i > 0) {
                    JOptionPane.showMessageDialog(this, "添加成功", "学生信息管理系统", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (ClassNotFoundException a) {
                JOptionPane.showMessageDialog(this, "驱动程序错误或不存在", "学生信息管理系统", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException b) {
                b.printStackTrace();
            } finally {
                try {
                    if (conn != null) conn.close();
                    System.out.println("数据库关闭成功");
                } catch (SQLException c) {
                    System.out.println("数据库关闭失败");
                    c.printStackTrace();
                }
            }
        }

        if (obj.equals(breturn)) {
            this.dispose();
        }
    }
}