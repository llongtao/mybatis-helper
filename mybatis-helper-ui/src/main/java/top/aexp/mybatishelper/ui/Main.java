package top.aexp.mybatishelper.ui;

import javax.swing.*;

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
