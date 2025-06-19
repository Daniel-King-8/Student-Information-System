import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainFrame extends JFrame implements ActionListener {
    private final boolean isAdmin;
    JButton badd, bedit, bfind, bdelete, bbrowse, bScoreInput, bUserManage;
    
    public MainFrame(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setTitle("学生信息管理系统" + (isAdmin ? " - 管理员" : ""));
        
        badd = new JButton("添加");
        bedit = new JButton("修改");
        bfind = new JButton("成绩查询");
        bdelete = new JButton("删除");
        bbrowse = new JButton("浏览");
        bScoreInput = new JButton("成绩录入");
        bUserManage = new JButton("账号管理");
        
        badd.addActionListener(this);
        bedit.addActionListener(this);
        bfind.addActionListener(this);
        bdelete.addActionListener(this);
        bbrowse.addActionListener(this);
        bScoreInput.addActionListener(this);
        bUserManage.addActionListener(this);
        
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        // 根据权限显示按钮
        if (isAdmin) {
            add(badd);
            add(bedit);
            add(bScoreInput);
            add(bUserManage);
        }
        
        // 所有用户可见
        add(bfind);
        
        // 仅管理员可见
        if (isAdmin) {
            add(bdelete);
            add(bbrowse);
        }
        
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(badd)) {
            new StuAddInfo();
        }
        if (e.getSource().equals(bedit)) {
            new StuEditInfo();
        }
        if (e.getSource().equals(bfind)) {
            new StuFindInfo();
        }
        if (e.getSource().equals(bdelete)) {
            new StuDelInfo();
        }
        if (e.getSource().equals(bbrowse)) {
            new StuBrowse();
        }
        if (e.getSource().equals(bScoreInput)) {
            new ScoreInputFrame();
        }
        if (e.getSource().equals(bUserManage)) {
            new UserManagementFrame();
        }
    }
}
