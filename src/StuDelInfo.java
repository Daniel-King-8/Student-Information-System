
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class StuDelInfo extends JFrame implements ActionListener {
    JLabel jlnumber = new JLabel("学号：");
    JTextField jtnumber = new JTextField("",20);
    JButton bdel = new JButton("删除");
    JButton Breturn = new JButton("返回");
    public StuDelInfo(){
        this.setTitle("删除学生信息");
        this.setLayout(new GridLayout(2,2));
        this.add(jlnumber);
        this.add(jtnumber);
        this.add(bdel);
        this.add(Breturn);
        bdel.addActionListener(this);
        Breturn.addActionListener(this);
        this.setSize(new Dimension(400,150));
        int windowWidth = this.getWidth();
        int windowHeight = this.getHeight();
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        this.setLocation(screenWidth/2-windowWidth/2,screenHeight/2-windowHeight/2);
        this.setVisible(true);

    }
    public void actionPerformed(ActionEvent e){
        Object obj = e.getSource();
        if(e.getSource().equals(bdel))
        {
            String number=jtnumber.getText();
            Connection conn = null;
            ResultSet res = null;
            Statement stat = null;
            String sql = "delete from xuesheng where xuehao ='"+number+"'";
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3308/student?useUnicode=true&characterEncoding=gbk", "root", "8888");
                    stat = conn.createStatement();
                    int j = JOptionPane.showConfirmDialog(this,"您真的要删除学号："+number+"的信息吗？","学生信息管理系统",JOptionPane.YES_NO_OPTION);
                    if(j == JOptionPane.NO_OPTION){return;};
                    int i = stat.executeUpdate(sql);
                    if (i>0){
                        JOptionPane.showMessageDialog(this,"删除成功","学生信息",JOptionPane.INFORMATION_MESSAGE);

                    }

            } catch (ClassNotFoundException a) {
                a.printStackTrace();
            }
            catch(SQLException h){
                h.printStackTrace();
            }
        finally{
            try{
                conn.close();
                System.out.println("数据库关闭成功！");
            }
            catch(SQLException j){
                j.printStackTrace();
            }
        }        if(obj.equals(Breturn)){
            this.dispose();
        }
    }
    }
}