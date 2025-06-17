import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainFrame extends JFrame implements ActionListener {
    JButton badd, bedit, bfind, bdelete, bbrowse;
    
    public MainFrame() {
        this.setTitle("学生信息管理系统");
        badd = new JButton("添加");
        bedit = new JButton("修改");
        bfind = new JButton("查询");
        bdelete = new JButton("删除");
        bbrowse = new JButton("浏览");
        
        badd.addActionListener(this);
        bedit.addActionListener(this);
        bfind.addActionListener(this);
        bdelete.addActionListener(this);
        bbrowse.addActionListener(this);
        
        this.setSize(400, 300);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        this.setLocation(screenSize.width/2 - 200, screenSize.height/2 - 150);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        FlowLayout flow = new FlowLayout(FlowLayout.LEFT, 10, 10);
        this.setLayout(flow);
        this.add(badd);
        this.add(bedit);
        this.add(bfind);
        this.add(bdelete);
        this.add(bbrowse);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj.equals(badd)) {
            new StuAddInfo();
        }
        if (obj.equals(bedit)) {
            new StuEditInfo();
        }
        if (obj.equals(bfind)) {
            new StuFindInfo();
        }
        if (obj.equals(bdelete)) {
            new StuDelInfo();
        }
        if (obj.equals(bbrowse)) {
            new StuBrowse();
        }
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
