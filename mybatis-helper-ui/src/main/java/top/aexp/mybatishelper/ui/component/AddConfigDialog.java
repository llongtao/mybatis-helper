package top.aexp.mybatishelper.ui.component;

import top.aexp.mybatishelper.ui.ConfigDataHolder;
import top.aexp.mybatishelper.ui.Main;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import javax.swing.*;


public class AddConfigDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JTextField jTextFieldMerberID = null;
    private JButton jButtonSure = null;
    private JButton jButtonExit = null;
    private JMenu configMenu;

    public AddConfigDialog( JMenu configMenu) {
        super(Main.MAIN_FRAME.get(), "添加配置", true);
        this.configMenu=configMenu;
        initialize();
    }

    private void initialize() {
        this.setSize(185, 120);
        this.setContentPane(getJContentPane());
        this.setFont(new Font("宋体", Font.PLAIN,12));
        this.setLocationRelativeTo(this);
        this.setVisible(true);

    }

    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(null);
            jContentPane.add(getJTextFieldMerberID(), null);
            jContentPane.add(getJButtonSure(), null);
            jContentPane.add(getJButtonExit(), null);
        }
        return jContentPane;
    }

    private JTextField getJTextFieldMerberID() {
        if (jTextFieldMerberID == null) {
            jTextFieldMerberID = new JTextField();
            jTextFieldMerberID.setBounds(new Rectangle(10, 10, 150, 25));
        }
        return jTextFieldMerberID;
    }


    private JButton getJButtonSure() {
        if (jButtonSure == null) {
            jButtonSure = new JButton();
            jButtonSure.setBounds(new Rectangle(15, 40, 60, 25));
            //jButtonSure.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.BOLD, 12));
            jButtonSure.setText("确定");
            jButtonSure.addActionListener(e -> {
                String configName = jTextFieldMerberID.getText();
                ConfigDataHolder.setUseConfig(configName);
                Main.MAIN_FRAME.get().setTitle("mybatis生成器(" + configName + ")");
                configMenu.removeAll();
                List<String> configList = ConfigDataHolder.getConfigList();
                for (String s : configList) {
                    JMenuItem menuItem;
                    if (Objects.equals(s, configName)) {
                        menuItem = new JMenuItem(s + "√");
                    } else {
                        menuItem = new JMenuItem(s);
                        menuItem.addActionListener(
                                event -> ConfigDataHolder.setUseConfig(s)
                        );
                    }
                    configMenu.add(menuItem);
                }

                JMenuItem addMenuItem = new JMenuItem("+");
                addMenuItem.addActionListener(e1 -> {
                    new AddConfigDialog(configMenu);
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
            jButtonExit.setBounds(new Rectangle(90, 40, 60, 25));
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

