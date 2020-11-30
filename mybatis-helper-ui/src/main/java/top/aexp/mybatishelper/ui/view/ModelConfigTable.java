package top.aexp.mybatishelper.ui.view;

import top.aexp.mybatishelper.core.model.Config;
import top.aexp.mybatishelper.core.model.EntityField;
import top.aexp.mybatishelper.core.utils.StringUtils;
import top.aexp.mybatishelper.ui.ConfigDataHolder;
import top.aexp.mybatishelper.ui.component.ButtonCellEditor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

/**
 * @author LILONGTAO
 */
public class ModelConfigTable {

    private JTable modelConfigTable;

    public ModelConfigTable() {
        Vector<String> columnNamesVector = new Vector<>();
        columnNamesVector.add("java列名");
        columnNamesVector.add("数据库列名");
        columnNamesVector.add("java类型");
        columnNamesVector.add("长度");
        columnNamesVector.add("默认值");
        columnNamesVector.add("非空");
        columnNamesVector.add("描述");
        columnNamesVector.add("添加");
        columnNamesVector.add("删除");
        Vector<Vector> dataVector = new Vector<>();
        Config config = ConfigDataHolder.getData();
        java.util.List<EntityField> baseEntityFieldList = config.getBaseEntityFieldList();


        if (baseEntityFieldList != null) {
            baseEntityFieldList.forEach(item -> {
                Vector<Object> vector = new Vector<>();
                vector.add(item.getName());
                vector.add(item.getColumnName());
                vector.add(item.getType());
                vector.add(item.getLength());
                vector.add(item.getDefaultValue());
                vector.add(!item.getNullable());
                vector.add(item.getDescription());
                vector.add("");
                vector.add("");
                dataVector.add(vector);
            });
        } else {
            Vector<Object> vector = new Vector<>();
            vector.add("");
            vector.add("");
            vector.add("");
            vector.add("");
            vector.add("");
            vector.add(true);
            vector.add("");
            vector.add("");
            vector.add("");
            dataVector.add(vector);
        }


        DefaultTableModel defaultTableModel = new DefaultTableModel(dataVector, columnNamesVector) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        defaultTableModel.addTableModelListener(e -> {
            if (e.getSource() instanceof DefaultTableModel) {
                int column = e.getColumn();
                if (column == 0) {
                    int row = e.getLastRow();
                    String newValue = defaultTableModel.getValueAt(row, 0).toString();
                    String s = StringUtils.transformUnderline(newValue);
                    defaultTableModel.setValueAt(s, row, 1);
                }
                ConfigDataHolder.updateModelConfig(dataVector);
            }

        });

        modelConfigTable = new JTable(defaultTableModel);
        //表格
//        modelConfigTable.setEnableAntialiasing(true);
//        modelConfigTable.setExpandableItemsEnabled(true);
//        modelConfigTable.setStriped(true);


        modelConfigTable.getColumnModel().getColumn(8).setCellEditor(new ButtonCellEditor("删除",
                e -> {
                    System.out.println("del" + e.getTable().getSelectedRow());
                    System.out.println(dataVector);
                    if (defaultTableModel.getRowCount() > 1) {
                        //stopped!!!!
                        //fireEditingStopped();
                        defaultTableModel.removeRow(e.getTable().getSelectedRow());

                    }
                    System.out.println(dataVector);
                }
        ));


        modelConfigTable.getColumnModel().getColumn(7).setCellEditor(
                new ButtonCellEditor("删除",
                        e -> {
                            System.out.println("add" + dataVector.size());
                            Vector<Object> vector = new Vector<>();
                            vector.add("");
                            vector.add("");
                            vector.add("");
                            vector.add("");
                            vector.add("");
                            vector.add(true);
                            vector.add("");
                            vector.add("");
                            vector.add("");
                            System.out.println(dataVector);

                            defaultTableModel.insertRow(defaultTableModel.getRowCount(), vector);
                            System.out.println(dataVector);
                        }));
        modelConfigTable.getColumnModel().getColumn(8).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton del = new JButton("删除");
            del.addActionListener(e -> {
                System.out.println("del" + table.getSelectedRow());
                System.out.println(dataVector);
                if (defaultTableModel.getRowCount() > 1) {
                    defaultTableModel.removeRow(table.getSelectedRow());
                }
                System.out.println(dataVector);
            });

            return del;
        });
        modelConfigTable.getColumnModel().getColumn(7).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> new JButton("添加"));

        modelConfigTable.getColumnModel().getColumn(8).setPreferredWidth(40);
        modelConfigTable.getColumnModel().getColumn(7).setPreferredWidth(40);
        modelConfigTable.getColumnModel().getColumn(5).setPreferredWidth(40);

    }

    public JTable getModelConfigTable() {
        return modelConfigTable;
    }
}
