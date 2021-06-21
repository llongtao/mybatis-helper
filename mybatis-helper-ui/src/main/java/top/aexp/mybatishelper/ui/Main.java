package top.aexp.mybatishelper.ui;

import top.aexp.mybatishelper.ui.component.AddConfigDialog;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author lilongtao 2020/11/30
 */
public class Main {

    public static final AtomicReference<JFrame> MAIN_FRAME = new AtomicReference<>();

    /**
     * 创建并显示GUI。
     */
    public static void createAndShowGui() {
        String configName = ConfigDataHolder.loadUseConfig();


        // 确保一个漂亮的外观风格
        JFrame.setDefaultLookAndFeelDecorated(true);

        // 创建及设置窗口
        JFrame frame = new JFrame("mybatis生成器(" + configName + ")");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (int) (toolkit.getScreenSize().getWidth() - 1000) / 2;

        int y = (int) (toolkit.getScreenSize().getHeight() - 700) / 2;

        frame.setLocation(x, y);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setJMenuBar(getjMenuBar(configName));

        MainSwing mainSwing = new MainSwing();
        frame.getContentPane().add(mainSwing.initCenter());

        // 显示窗口
        frame.pack();

        frame.setVisible(true);
        MAIN_FRAME.set(frame);

    }

    private static JMenuBar getjMenuBar(String configName) {
        List<String> configList = ConfigDataHolder.getConfigList();
        /*
         * 创建一个菜单栏
         */
        JMenuBar menuBar = new JMenuBar();

        /*
         * 创建一级菜单
         */
        JMenu configMenu = new JMenu("配置");
        menuBar.add(configMenu);
        configMenu.addSeparator();

        for (String s : configList) {
            JMenuItem menuItem;
            if (Objects.equals(s, configName)) {
                menuItem = new JMenuItem(s + "√");
            } else {
                menuItem = new JMenuItem(s);
                menuItem.addActionListener(
                        e -> {
                            ConfigDataHolder.setUseConfig(s);
                            SwingUtilities.invokeLater(() -> {
                                JFrame jFrame = MAIN_FRAME.get();
                                if (jFrame != null) {
                                    jFrame.setVisible(false);
                                    createAndShowGui();
                                }
                            });
                        }
                );
            }
            configMenu.add(menuItem);
        }

        JMenuItem addMenuItem = new JMenuItem("+");
        addMenuItem.addActionListener(e -> new AddConfigDialog(configMenu));
        configMenu.add(addMenuItem);
        return menuBar;
    }

    public static void main(String[] args) {
        // 显示应用 GUI
        SwingUtilities.invokeLater(Main::createAndShowGui);
    }
}
