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
public class ButtonCellEditor implements TableCellEditor {
    private Consumer<OnClickEvent> actionListener;
    private String name;

    public ButtonCellEditor(String name,Consumer<OnClickEvent> actionListener) {
        this.name = name;
        this.actionListener = actionListener;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JButton del = new JButton(name);
        del.addActionListener(e -> actionListener.accept(new OnClickEvent(table, value, isSelected, row, column)));
        return del;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return false;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    @Override
    public boolean stopCellEditing() {
        return false;
    }

    @Override
    public void cancelCellEditing() {

    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {

    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {

    }


    @Getter
    public class OnClickEvent{
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
