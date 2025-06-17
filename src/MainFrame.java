import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainFrame extends JFrame implements ActionListener {
    JButton badd, bedit, bfind, bdelete, bbrowse; // 增加,修改,查询,删除,浏览5个按钮
    
    public MainFrame() { // 构造函数生成主界面
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
        // 设置窗口大小
        this.setSize(new Dimension(400, 300));
        int windowWidth = this.getWidth(); // 获得窗口宽
        int windowHeight = this.getHeight(); // 获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
        Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
        int screenWidth = screenSize.width; // 获取屏幕的宽
        int screenHeight = screenSize.height; // 获取屏幕的高
        
        // 设置窗口居中
        this.setLocation(screenWidth/2 - windowWidth/2, screenHeight/2 - windowHeight/2); // 设置窗口居中显示
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

    public void actionPerformed(ActionEvent e) { // 判断窗口事件
        // 获得事件源
        Object obj = e.getSource();
        if (obj.equals(badd)) { // 执行添加程序
            new StuAddInfo();
        }
        if (obj.equals(bedit)) { // 执行修改程序
            new StuEditInfo();
        }
        if (obj.equals(bfind)) { // 执行查询程序
            new StuFindInfo();
        }
        if (obj.equals(bdelete)) { // 执行删除程序
            new StuDelInfo();
        }
        if (obj.equals(bbrowse)) { // 执行浏览程序
            StuBrowse sbw = new StuBrowse();
        }
    }

    public static void main(String[] args) {
        MainFrame mf = new MainFrame();
    }
}