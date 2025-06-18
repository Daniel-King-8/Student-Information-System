import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

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
        
        this.add(pnorth, BorderLayout.NORTH);
        this.add(pcenter, BorderLayout.CENTER);

        this.setSize(400,180);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        this.setLocation(screenSize.width/2 - 200, screenSize.height/2 - 90);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            if (obj.equals(bfind)) {
                String number = jtnumber.getText();
                String name = jtname.getText();
                
                if (number.isEmpty() && name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "请输入学号或姓名");
                    return;
                }
                
                conn = DatabaseUtil.getConnection();
                String sql = "select * from xuesheng where xuehao = ? or xingming = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, number);
                ps.setString(2, name);
                rs = ps.executeQuery();
                
                if (rs.next()) {
                    jtnumber.setText(rs.getString(1));
                    jtname.setText(rs.getString(2));
                    jtsex.setText(rs.getString(3));
                    jtbirthday.setText(rs.getString(4));
                    jtdepartment.setText(rs.getString(5));
                } else {
                    JOptionPane.showMessageDialog(this, "未找到相关学生信息");
                }
            }
            
            if (obj.equals(bedit)) {
                String number = jtnumber.getText();
                if (number.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "请先查询要修改的学生");
                    return;
                }
                
                conn = DatabaseUtil.getConnection();
                String sql = "update xuesheng set xingming=?, xingbie=?, chushengriqi=?, xueyuan=? where xuehao=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, jtname.getText());
                ps.setString(2, jtsex.getText());
                ps.setString(3, jtbirthday.getText());
                ps.setString(4, jtdepartment.getText());
                ps.setString(5, jtnumber.getText());
                
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "修改成功");
                } else {
                    JOptionPane.showMessageDialog(this, "修改失败");
                }
            }
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "操作失败: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, ps, rs);
        }
        
        if (obj.equals(breturn)) {
            this.dispose();
        }
    }
}