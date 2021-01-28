package top.aexp.mybatishelper.ui.component;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author LILONGTAO
 */
public class ButtonCellRenderer implements TableCellRenderer {
    private JPanel panel;
    private JButton button;
    private String name;

    public ButtonCellRenderer(String name){
        this.name = name;
        initButton();
        initPanel();
        panel.add(button,BorderLayout.CENTER);
    }
    private void initButton(){
        button =new JButton();
    }
    private void initPanel(){
        panel=new JPanel();
        panel.setLayout(new BorderLayout());
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        button.setText(name);
        return panel;
    }
}
