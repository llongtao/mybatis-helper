package top.aexp.mybatishelper.ui.component;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author LILONGTAO
 */
public class MyFileChooserRenderer implements TableCellRenderer {
    private JPanel panel;
    private JButton button;
    private Object value;
    public MyFileChooserRenderer(){
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
        this.value=value;
        button.setText(StringUtils.isEmpty((String)value)?"请选择文件夹":String.valueOf(value));
        return panel;
    }
}
