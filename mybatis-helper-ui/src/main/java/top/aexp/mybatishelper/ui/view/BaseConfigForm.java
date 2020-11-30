package top.aexp.mybatishelper.ui.view;


import top.aexp.mybatishelper.core.model.Config;
import top.aexp.mybatishelper.ui.ConfigDataHolder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Objects;

/**
 * @author LILONGTAO
 */
public class BaseConfigForm {

    private JCheckBox checkbox;
    private JCheckBox rebuild;
    private JTextField use;
    private JPasswordField pwd;
    private JTextField dbUrl;
    private JPanel pane;
    private JComboBox js;

    public BaseConfigForm() {
        Config data = ConfigDataHolder.getData();

        GridBagLayout gridLayout = new GridBagLayout();
        GridBagConstraints grid1 = new GridBagConstraints();
        grid1.gridx = 1;
        grid1.gridy = 0;
        GridBagConstraints grid12 = new GridBagConstraints();
        grid12.gridx = 1 ;
        grid12.gridy = 1;
        GridBagConstraints grid2 = new GridBagConstraints();
        grid2.gridx = 0;
        grid2.gridy = 2;
        GridBagConstraints grid3 = new GridBagConstraints();
        grid3.gridx = 1;
        grid3.gridy = 2;
        GridBagConstraints grid4 = new GridBagConstraints();
        grid4.gridx = 0;
        grid4.gridy = 3;
        GridBagConstraints grid5 = new GridBagConstraints();
        grid5.gridx = 1;
        grid5.gridy = 3;
        GridBagConstraints grid6 = new GridBagConstraints();
        grid6.gridx = 0;
        grid6.gridy = 4;
        GridBagConstraints grid7 = new GridBagConstraints();
        grid7.gridx = 1;
        grid7.gridy = 4;
        GridBagConstraints grid8 = new GridBagConstraints();
        grid8.gridx = 0;
        grid8.gridy = 5;
        GridBagConstraints grid9 = new GridBagConstraints();
        grid9.gridx = 1;
        grid9.gridy = 5;


        checkbox = new JCheckBox("生成表结构");
        checkbox.setSelected(Objects.equals(data.getUseDb(),true));
        rebuild = new JCheckBox("重新生成表");
        rebuild.setSelected(Objects.equals(data.getUseDb(),false));
        rebuild.addItemListener(e -> update());
        checkbox.addItemListener(e -> {

            if (ItemEvent.SELECTED == e.getStateChange()) {
                dbUrl.setEnabled(true);
                use.setEnabled(true);
                pwd.setEnabled(true);
                dbUrl.setEnabled(true);
                update();
            } else {
                dbUrl.setEnabled(false);
                use.setEnabled(false);
                pwd.setEnabled(false);
                dbUrl.setEnabled(false);
                update();
            }
        });

        js = new JComboBox<>(new String[]{"mysql", "pgsql"});
        js.setSelectedIndex(0);
        js.setPreferredSize(new Dimension(150, 25));
        js.addItemListener(e -> update());
        JLabel db = new JLabel("数据库:");
        JLabel dbUrlL = new JLabel("地址:");
        dbUrl = new JTextField(data.getBaseDbUrl(),24);
        dbUrl.setEditable(true);
        //dbUrl.setBounds(0,0,150,25);
        dbUrl.setSize(150, 25);
        dbUrl.setMinimumSize(new Dimension(150, 25));
        dbUrl.setPreferredSize(new Dimension(150, 25));

        JLabel username = new JLabel("帐号:");
        JLabel userpwd = new JLabel("密码:");
        use = new JTextField(data.getBaseDbUsername(),24);
        use.setSize(150, 25);
        use.setMinimumSize(new Dimension(150, 25));
        use.setPreferredSize(new Dimension(150, 25));
        pwd = new JPasswordField(data.getBaseDbPassword(),24);
        pwd.setSize(1500, 25);
        pwd.setMinimumSize(new Dimension(150, 25));
        pwd.setPreferredSize(new Dimension(150, 25));
        pane = new JPanel();

        pane.setLayout(gridLayout);
        pane.add(checkbox, grid1);
        pane.add(rebuild, grid12);
        pane.add(db, grid2);
        pane.add(js, grid3);
        pane.add(dbUrlL, grid4);
        pane.add(dbUrl, grid5);
        pane.add(username, grid6);
        pane.add(use, grid7);
        pane.add(userpwd, grid8);
        pane.add(pwd, grid9);


        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        };

        js.addVetoableChangeListener(e -> update());
        dbUrl.getDocument().addDocumentListener(documentListener);
        use.getDocument().addDocumentListener(documentListener);
        pwd.getDocument().addDocumentListener(documentListener);

        ConfigDataHolder.registerDataChangeConsumer(config -> {
            config.setDbType(Objects.toString(js.getSelectedItem()));
            config.setBaseDbUrl( dbUrl.getText());
            config.setBaseDbUsername(use.getText());
            config.setBaseDbPassword(new String(pwd.getPassword()));
            config.setUseDb(checkbox.isSelected());
            config.setDropTable(rebuild.isSelected());
        });
    }

    private void update() {
        ConfigDataHolder.updateBaseConfig(Objects.toString(js.getSelectedItem()), dbUrl.getText(), use.getText(), new String(pwd.getPassword()), checkbox.isSelected(),rebuild.isSelected());
    }

    public JPanel getForm() {
        return pane;
    }
}
