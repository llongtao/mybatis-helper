package top.aexp.mybatishelper.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lilongtao 2020/11/30
 */
public class Main {
    /**
     * 创建并显示GUI。
     */
    private static void createAndShowGui() {
        // 确保一个漂亮的外观风格
        JFrame.setDefaultLookAndFeelDecorated(true);

        // 创建及设置窗口
        JFrame frame = new JFrame("mybatis生成器");
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
        List<String> configList = ConfigDataHolder.getConfigList();
        for (String s : configList) {
            configMenu.add(new MenuItem(s));
        }
        frame.setMenuBar(menuBar);

        ConfigDataHolder.loadConfig("config");
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
