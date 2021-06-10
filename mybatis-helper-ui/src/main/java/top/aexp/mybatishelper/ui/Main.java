package top.aexp.mybatishelper.ui;

import top.aexp.mybatishelper.ui.component.aDialogB;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * @author lilongtao 2020/11/30
 */
public class Main {
    /**
     * 创建并显示GUI。
     */
    private static void createAndShowGui() {
        String configName = ConfigDataHolder.loadUseConfig();
        List<String> configList = ConfigDataHolder.getConfigList();


        // 确保一个漂亮的外观风格
        JFrame.setDefaultLookAndFeelDecorated(true);

        // 创建及设置窗口
        JFrame frame = new JFrame("mybatis生成器(" + configName + ")");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /*
         * 创建一个菜单栏
         */
        MenuBar menuBar = new MenuBar();

        /*
         * 创建一级菜单
         */
        Menu configMenu = new Menu("配置");
        menuBar.add(configMenu);
        configMenu.addSeparator();

        for (String s : configList) {
            MenuItem menuItem;
            if (Objects.equals(s, configName)) {
                menuItem = new MenuItem(s + "√");
            } else {
                menuItem = new MenuItem(s);
                menuItem.addActionListener(
                        e -> ConfigDataHolder.setUseConfig(s)
                );
            }
            configMenu.add(menuItem);
        }

        MenuItem addMenuItem = new MenuItem("+");
        addMenuItem.addActionListener(e -> {
            new aDialogB(frame,configMenu);
        });
        configMenu.add(addMenuItem);

        frame.setMenuBar(menuBar);

        MainSwing mainSwing = new MainSwing();
        frame.getContentPane().add(mainSwing.initCenter());

        // 显示窗口
        frame.pack();

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // 显示应用 GUI
        SwingUtilities.invokeLater(Main::createAndShowGui);
    }
}
