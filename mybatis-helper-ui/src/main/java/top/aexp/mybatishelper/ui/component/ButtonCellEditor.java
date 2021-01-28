package top.aexp.mybatishelper.ui.component;

import lombok.Getter;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.function.Consumer;

/**
 * @author lilongtao 2020/11/30
 */
public class ButtonCellEditor extends AbstractCellEditor implements TableCellEditor {
    private final Consumer<OnClickEvent> actionListener;
    private String name;


    public ButtonCellEditor(String name, Consumer<OnClickEvent> actionListener) {
        this.actionListener = actionListener;
        this.name = name;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JButton jButton = new JButton(name);

        panel.add(jButton, BorderLayout.CENTER);
        jButton.addActionListener(e -> actionListener.accept(new OnClickEvent(table, value, isSelected, row, column)));
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }

    @Override
    public boolean stopCellEditing() {
        return true;
    }


    @Getter
    public class OnClickEvent {
        private JTable table;
        private Object value;
        private boolean isSelected;
        private int row;
        private int column;

        public OnClickEvent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table;
            this.value = value;
            this.isSelected = isSelected;
            this.row = row;
            this.column = column;
        }
    }
}
