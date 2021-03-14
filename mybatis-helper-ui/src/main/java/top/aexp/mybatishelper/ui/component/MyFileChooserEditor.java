package top.aexp.mybatishelper.ui.component;


import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.io.File;
import java.util.Vector;

/**
 * @author LILONGTAO
 */
public class MyFileChooserEditor extends AbstractCellEditor implements TableCellEditor {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6546334664166791132L;

    private String path;


    private final JPanel panel;

    private JButton button;

    private final int row;

    private final int column;

    private final Vector<Vector<?>> dataVector;

    public MyFileChooserEditor(Vector<Vector<?>> dataVector,  int row, int column) {
        this.row = row;
        this.column = column;
        this.dataVector = dataVector;
        this.path = dataVector.get(row).get(column).toString();

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        initButton();
        panel.add(button, BorderLayout.CENTER);
    }

    private void initButton() {

        button = new JButton();
        button.addActionListener(e -> {
            String path = dataVector.get(row).get(column).toString();
            JFileChooser chooserDescriptor = new  JFileChooser(new File(path));
            chooserDescriptor.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int status=chooserDescriptor.showOpenDialog(null);
            if (status == 0) {
                setPath(chooserDescriptor.getSelectedFile().getPath());
            }
            //stopped!!!!
            fireEditingStopped();
        });
    }


    private void setPath(String path) {
        this.path = path;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return path;
    }


}
