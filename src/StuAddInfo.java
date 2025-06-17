import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

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

        this.setSize(400, 230);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        this.setLocation(screenSize.width/2 - 200, screenSize.height/2 - 115);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj.equals(badd)) {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = DatabaseUtil.getConnection();
                String sql = "insert into xuesheng values(?,?,?,?,?)";
                ps = conn.prepareStatement(sql);
                ps.setString(1, jtnumber.getText());
                ps.setString(2, jtname.getText());
                String sex = jsexman.isSelected() ? "男" : "女";
                ps.setString(3, sex);
                ps.setString(4, jtbirthday.getText());
                ps.setString(5, jtdepartment.getText());

                int i = ps.executeUpdate();
                if (i > 0) {
                    JOptionPane.showMessageDialog(this, "添加成功", "学生信息管理系统", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "操作失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                DatabaseUtil.close(conn, ps, null);
            }
        }
        if (obj.equals(breturn)) {
            this.dispose();
        }
    }
}
