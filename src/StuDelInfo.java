import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class StuDelInfo extends JFrame implements ActionListener {
    JLabel jlnumber = new JLabel("学号：");
    JTextField jtnumber = new JTextField("",20);
    JButton bdel = new JButton("删除");
    JButton breturn = new JButton("返回");
    
    public StuDelInfo() {
        this.setTitle("删除学生信息");
        this.setLayout(new GridLayout(2,2));
        this.add(jlnumber);
        this.add(jtnumber);
        this.add(bdel);
        this.add(breturn);
        
        bdel.addActionListener(this);
        breturn.addActionListener(this);
        
        this.setSize(400,150);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        this.setLocation(screenSize.width/2 - 200, screenSize.height/2 - 75);
        this.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj.equals(bdel)) {
            String number = jtnumber.getText();
            if (number.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入学号", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "您真的要删除学号："+number+"的信息吗？", 
                    "确认删除", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
                
                conn = DatabaseUtil.getConnection();
                String sql = "delete from xuesheng where xuehao = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, number);
                
                int i = ps.executeUpdate();
                if (i > 0) {
                    JOptionPane.showMessageDialog(this, "删除成功");
                } else {
                    JOptionPane.showMessageDialog(this, "未找到该学号的学生");
                }
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "删除失败: " + ex.getMessage());
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
