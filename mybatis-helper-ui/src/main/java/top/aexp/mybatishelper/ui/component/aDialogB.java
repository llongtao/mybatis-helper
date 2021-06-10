package top.aexp.mybatishelper.ui.component;

import top.aexp.mybatishelper.ui.ConfigDataHolder;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class aDialogB extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JLabel jLabelMerberIDTitle = null;
    private JTextField jTextFieldMerberID = null;
    private JButton jButtonSure = null;
    private JButton jButtonExit = null;
    private Frame owner = null;
    private Menu configMenu;

    public aDialogB(Frame owner, Menu configMenu) {
        super(owner, "添加配置", true);
        this.owner = owner;
        this.configMenu=configMenu;
        initialize();
    }

    private void initialize() {
        this.setSize(300, 160);
        this.setContentPane(getJContentPane());
        this.setLocationRelativeTo(this);
        this.setVisible(true);

    }

    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jLabelMerberIDTitle = new JLabel();
            //jLabelMerberIDTitle.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.BOLD, 12));
            jLabelMerberIDTitle.setText("请输入配置名");
            jLabelMerberIDTitle.setBounds(new Rectangle(40, 30, 90, 30));
            jContentPane = new JPanel();
            jContentPane.setLayout(null);
            jContentPane.add(jLabelMerberIDTitle, null);
            jContentPane.add(getJTextFieldMerberID(), null);
            jContentPane.add(getJButtonSure(), null);
            jContentPane.add(getJButtonExit(), null);
        }
        return jContentPane;
    }

    private JTextField getJTextFieldMerberID() {
        if (jTextFieldMerberID == null) {
            jTextFieldMerberID = new JTextField();
            jTextFieldMerberID.setBounds(new Rectangle(162, 37, 114, 33));
        }
        return jTextFieldMerberID;
    }


    private JButton getJButtonSure() {
        if (jButtonSure == null) {
            jButtonSure = new JButton();
            jButtonSure.setBounds(new Rectangle(61, 98, 70, 33));
            //jButtonSure.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.BOLD, 12));
            jButtonSure.setText("确定");
            jButtonSure.addActionListener(e -> {
                String configName = jTextFieldMerberID.getText();
                ConfigDataHolder.setUseConfig(configName);
                owner.setTitle("mybatis生成器(" + configName + ")");
                configMenu.removeAll();
                List<String> configList = ConfigDataHolder.getConfigList();
                for (String s : configList) {
                    MenuItem menuItem;
                    if (Objects.equals(s, configName)) {
                        menuItem = new MenuItem(s + "√");
                    } else {
                        menuItem = new MenuItem(s);
                        menuItem.addActionListener(
                                event -> ConfigDataHolder.setUseConfig(s)
                        );
                    }
                    configMenu.add(menuItem);
                }

                MenuItem addMenuItem = new MenuItem("+");
                addMenuItem.addActionListener(e1 -> {
                    new aDialogB(owner,configMenu);
                });
                configMenu.add(addMenuItem);


                setVisible(false);
            });
        }
        return jButtonSure;
    }


    private JButton getJButtonExit() {
        if (jButtonExit == null) {
            jButtonExit = new JButton();
            jButtonExit.setBounds(new Rectangle(192, 98, 70, 33));
            //jButtonExit.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.BOLD, 12));
            jButtonExit.setText("取消");
            jButtonExit.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    setVisible(false);
                }
            });
        }
        return jButtonExit;
    }


}  

